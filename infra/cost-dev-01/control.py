"""Offline COST-DEV-01 controller model. No SDK, network, files, or cloud adapter."""
from __future__ import annotations

from dataclasses import dataclass, field
from datetime import datetime, time, timedelta, timezone
from enum import Enum
import json
from typing import Any, Protocol
from zoneinfo import ZoneInfo

PROJECT = "visana-erp-dev"
REGION = "us-central1"
SQL = "visana-db-dev"
RUN = "visana-api-dev"
BUCKET = "visana-erp-dev-schedule-state"
OBJECT = "cost-dev-01/current.json"
BOGOTA = ZoneInfo("America/Bogota")
MAX_STATE_BYTES = 256 * 1024
RETRY_DELAYS = (5, 15, 30)
START_LIMIT = timedelta(minutes=15)
BACKUP_AGE_LIMIT = timedelta(hours=24)
ALLOWLIST = {"projectId": PROJECT, "region": REGION, "sqlInstance": SQL,
             "runService": RUN, "stateBucket": BUCKET, "stateObject": OBJECT}


# Exact R1 values; external schedule.json is validated against this contract.
SCHEDULE_CONTRACT = {
    "timeZone": "America/Bogota", "weekdays": [1, 2, 3, 4, 5],
    "sqlStart": "07:30", "closeNotice": "17:30", "qaFreeze": "17:45",
    "quiescenceTarget": "18:00", "backupWindowStart": "18:00",
    "backupWindowLastStart": "22:00", "stopDeadline": "23:00",
    "backupPollMinutes": 15, "startDeadlineMinutes": 15,
    "retryDelaysSeconds": [5, 15, 30],
}


def validate_schedule(schedule: dict) -> dict:
    if schedule != SCHEDULE_CONTRACT:
        raise ControlError("R1_SCHEDULE_MISMATCH")
    return dict(schedule)


class ControlError(Exception):
    def __init__(self, code: str):
        self.code = code
        super().__init__(code)


class State(str, Enum):
    OFF = "OFF"
    STARTING_SQL = "STARTING_SQL"
    WAITING_SQL = "WAITING_SQL"
    STARTING_SERVICES = "STARTING_SERVICES"
    ON = "ON"
    FREEZING = "FREEZING"
    QUIESCING = "QUIESCING"
    WAITING_BACKUP = "WAITING_BACKUP"
    STOPPING_SERVICES = "STOPPING_SERVICES"
    STOPPING_SQL = "STOPPING_SQL"
    ERROR = "ERROR"
    MANUAL_HOLD = "MANUAL_HOLD"


class Operation(str, Enum):
    START_DEV = "START_DEV"
    STOP_DEV = "STOP_DEV"
    STATUS = "STATUS"
    DRY_RUN_START = "DRY_RUN_START"
    DRY_RUN_STOP = "DRY_RUN_STOP"


NEXT = {
    State.OFF: {State.STARTING_SQL},
    State.STARTING_SQL: {State.WAITING_SQL, State.ERROR},
    State.WAITING_SQL: {State.STARTING_SERVICES, State.ERROR},
    State.STARTING_SERVICES: {State.ON, State.ERROR},
    State.ON: {State.FREEZING},
    State.FREEZING: {State.QUIESCING, State.MANUAL_HOLD},
    State.QUIESCING: {State.WAITING_BACKUP, State.MANUAL_HOLD},
    State.WAITING_BACKUP: {State.STOPPING_SERVICES, State.MANUAL_HOLD},
    State.STOPPING_SERVICES: {State.STOPPING_SQL, State.MANUAL_HOLD},
    State.STOPPING_SQL: {State.OFF, State.ERROR},
    State.ERROR: set(), State.MANUAL_HOLD: set(),
}


def as_utc(value: datetime) -> datetime:
    if not isinstance(value, datetime) or value.tzinfo is None:
        raise ControlError("INVALID_TIME")
    return value.astimezone(timezone.utc)


def stamp(value: datetime) -> str:
    return as_utc(value).isoformat().replace("+00:00", "Z")


def read_time(value: str) -> datetime:
    try:
        return as_utc(datetime.fromisoformat(value.replace("Z", "+00:00")))
    except (AttributeError, TypeError, ValueError) as exc:
        raise ControlError("INVALID_TIME") from exc


def parse_request(request: dict[str, Any]) -> tuple[Operation, bool, str, str]:
    if not isinstance(request, dict):
        raise ControlError("INVALID_REQUEST")
    if set(request) - (set(ALLOWLIST) | {"operation", "dryRun", "executionId", "requestedBy"}):
        raise ControlError("UNSUPPORTED_INPUT")
    if any(request[k] != v for k, v in ALLOWLIST.items() if k in request):
        raise ControlError("DEV_ALLOWLIST_MISMATCH")
    try:
        op = Operation(request["operation"])
    except (KeyError, TypeError, ValueError) as exc:
        raise ControlError("INVALID_OPERATION") from exc
    dry = request.get("dryRun", True)
    if type(dry) is not bool:
        raise ControlError("INVALID_DRY_RUN")
    if op in (Operation.DRY_RUN_START, Operation.DRY_RUN_STOP):
        dry = True
    eid, actor = request.get("executionId"), request.get("requestedBy")
    if not isinstance(eid, str) or not 1 <= len(eid) <= 128 or not eid.isascii() or not eid.replace("-", "").replace("_", "").isalnum():
        raise ControlError("INVALID_EXECUTION_ID")
    if not isinstance(actor, str) or not 1 <= len(actor) <= 128 or any(c in actor for c in "\r\n"):
        raise ControlError("INVALID_REQUESTED_BY")
    return op, dry, eid, actor


STATE_KEYS = {"schemaVersion", "environment", "projectId", "operation", "state",
              "executionId", "eventId", "startedAt", "updatedAt", "requestedBy",
              "dryRun", "fence", "leaseUntil", "previousState",
              "lastSuccessfulBackup", "resourceSnapshot", "errorCode"}


def validate_state(doc: dict[str, Any]) -> State:
    if not isinstance(doc, dict) or set(doc) - STATE_KEYS:
        raise ControlError("CORRUPT_STATE")
    if doc.get("schemaVersion") != 1 or doc.get("environment") != "DEV" or doc.get("projectId") != PROJECT or doc.get("dryRun") is not False:
        raise ControlError("CORRUPT_STATE")
    try:
        state = State(doc["state"])
        Operation(doc["operation"])
        read_time(doc["startedAt"])
        read_time(doc["updatedAt"])
        read_time(doc["leaseUntil"])
    except (KeyError, ValueError, TypeError, ControlError) as exc:
        raise ControlError("CORRUPT_STATE") from exc
    if not isinstance(doc.get("executionId"), str) or not doc["executionId"]:
        raise ControlError("CORRUPT_STATE")
    if not isinstance(doc.get("fence"), int) or doc["fence"] < 1:
        raise ControlError("CORRUPT_STATE")
    snapshot = doc.get("resourceSnapshot", {})
    if not isinstance(snapshot, dict) or set(snapshot) - {"mode", "min", "max", "manual"}:
        raise ControlError("CORRUPT_STATE")
    if snapshot:
        if snapshot.get("mode") not in ("AUTOMATIC", "MANUAL"):
            raise ControlError("CORRUPT_STATE")
        for key in ("min", "max"):
            if type(snapshot.get(key)) is not int or snapshot[key] < 0:
                raise ControlError("CORRUPT_STATE")
        if snapshot.get("manual") is not None and (type(snapshot["manual"]) is not int or snapshot["manual"] < 0):
            raise ControlError("CORRUPT_STATE")
    if len(json.dumps(doc, separators=(",", ":"), ensure_ascii=False).encode()) > MAX_STATE_BYTES:
        raise ControlError("STATE_TOO_LARGE")
    return state


@dataclass(frozen=True)
class Stored:
    document: dict[str, Any]
    generation: int


class Store(Protocol):
    def read(self) -> Stored | None: ...
    def cas(self, document: dict[str, Any], generation_match: int) -> Stored: ...


@dataclass
class MemoryStore:
    """Generation-matching test double, including generationMatch=0 on first insert."""
    item: Stored | None = None
    matches: list[int] = field(default_factory=list)
    conflict_once: bool = False

    def read(self) -> Stored | None:
        return self.item

    def cas(self, document: dict[str, Any], generation_match: int) -> Stored:
        if self.conflict_once:
            self.conflict_once = False
            raise ControlError("CAS_CONFLICT")
        current = self.item.generation if self.item else 0
        if current != generation_match:
            raise ControlError("CAS_CONFLICT")
        validate_state(document)
        self.item = Stored(json.loads(json.dumps(document)), current + 1)
        self.matches.append(generation_match)
        return self.item


@dataclass(frozen=True)
class Backup:
    backup_id: str | None
    status: str
    started_at: datetime | None
    ended_at: datetime | None
    recoverable_through: datetime | None


@dataclass(frozen=True)
class StopEvidence:
    deployments_idle: bool
    sql_operations_idle: bool
    writers_quiesced: bool
    transactions_zero: bool
    consumers_quiesced: bool
    tags_safe: bool
    fence_at: datetime | None
    last_write_at: datetime | None
    backup: Backup | None


def verify_stop(e: StopEvidence, now: datetime, stop_deadline: str) -> str:
    now = as_utc(now)
    if not all((e.deployments_idle, e.sql_operations_idle, e.writers_quiesced,
                e.transactions_zero, e.consumers_quiesced, e.tags_safe)):
        raise ControlError("QUIESCENCE_UNPROVEN")
    if e.fence_at is None or e.last_write_at is None:
        raise ControlError("FENCE_UNPROVEN")
    fence, last_write = as_utc(e.fence_at), as_utc(e.last_write_at)
    local_fence = fence.astimezone(BOGOTA)
    deadline = datetime.combine(local_fence.date(), time.fromisoformat(stop_deadline), BOGOTA)
    if now.astimezone(BOGOTA) >= deadline:
        raise ControlError("BACKUP_DEADLINE")
    if last_write > fence:
        raise ControlError("WRITE_AFTER_FENCE")
    b = e.backup
    if b is None or not b.backup_id:
        raise ControlError("BACKUP_MISSING")
    if b.status != "SUCCESSFUL":
        raise ControlError("BACKUP_NOT_SUCCESSFUL")
    if not (b.started_at and b.ended_at and b.recoverable_through):
        raise ControlError("BACKUP_COVERAGE_UNPROVEN")
    started, ended, covered = as_utc(b.started_at), as_utc(b.ended_at), as_utc(b.recoverable_through)
    if started < fence or ended < started or covered < fence or ended > now or now - ended > BACKUP_AGE_LIMIT:
        raise ControlError("BACKUP_TOO_OLD")
    return b.backup_id


def safe_log(**fields: Any) -> dict[str, Any]:
    codes = {"operation", "resource", "previousState", "newState", "status", "errorCode"}
    allowed = codes | {"executionId", "dryRun", "duration"}
    result = {}
    for key in allowed & fields.keys():
        value = fields[key]
        if key in {"dryRun", "duration"} and isinstance(value, (bool, int, float)):
            result[key] = value
        elif isinstance(value, str) and value.isascii() and value.replace("_", "").replace("-", "").isalnum():
            result[key] = value
        else:
            result[key] = "REDACTED"
    return result


def plan(op: Operation, state: State | None) -> tuple[str, ...]:
    if op in (Operation.START_DEV, Operation.DRY_RUN_START):
        return ("NO_OP_ON",) if state == State.ON else (
            "VALIDATE_ALLOWLIST", "READ_STATE", "CHECK_SQL", "START_SQL_IF_OFF",
            "WAIT_SQL_READY", "CHECK_KEYCLOAK_IF_PROVISIONED", "RESTORE_RUN_SCALING",
            "CHECK_HEALTH", "MARK_ON")
    if op in (Operation.STOP_DEV, Operation.DRY_RUN_STOP):
        return ("NO_OP_OFF",) if state == State.OFF else (
            "VALIDATE_ALLOWLIST", "READ_STATE", "FREEZE", "CHECK_DEPLOYMENTS",
            "CHECK_QUIESCENCE_AND_FENCE", "VERIFY_NEW_RECOVERABLE_BACKUP",
            "CHECK_NO_LATER_WRITES", "STOP_RUN", "CHECK_TAGS",
            "STOP_SQL_LAST", "MARK_OFF")
    return ("READ_STATE",)


class Runtime(Protocol):
    def sql_running(self) -> bool: ...
    def start_sql(self) -> None: ...
    def sql_ready(self) -> bool: ...
    def wait(self, seconds: int) -> None: ...
    def keycloak_status(self) -> str: ...
    def run_snapshot(self) -> dict[str, Any]: ...
    def start_run(self, snapshot: dict[str, Any]) -> None: ...
    def health_ready(self) -> bool: ...
    def stop_evidence(self) -> StopEvidence: ...
    def stop_run(self) -> None: ...
    def stop_sql(self) -> None: ...


class Controller:
    def __init__(self, store: Store, runtime: Runtime, now, schedule: dict):
        self.store, self.runtime, self.now = store, runtime, now
        self.schedule = validate_schedule(schedule)

    def transition(self, item: Stored, next_state: State, eid: str, op: Operation,
                   actor: str, error: str | None = None,
                   snapshot: dict[str, Any] | None = None,
                   backup_id: str | None = None) -> Stored:
        prior = validate_state(item.document)
        if next_state not in NEXT[prior]:
            raise ControlError("INVALID_TRANSITION")
        doc = dict(item.document)
        doc.update(state=next_state.value, previousState=prior.value, updatedAt=stamp(self.now()),
                   executionId=eid, operation=op.value, requestedBy=actor, errorCode=error)
        if snapshot is not None:
            doc["resourceSnapshot"] = snapshot
        if backup_id is not None:
            doc["lastSuccessfulBackup"] = backup_id
        try:
            return self.store.cas(doc, item.generation)
        except ControlError as exc:
            if exc.code == "CAS_CONFLICT":
                latest = self.store.read()
                if latest and latest.document.get("executionId") != eid:
                    raise ControlError("CONCURRENT_EXECUTION") from exc
            raise

    def execute(self, request: dict[str, Any]) -> dict[str, Any]:
        op, dry, eid, actor = parse_request(request)
        item = self.store.read()
        state = validate_state(item.document) if item else None
        if dry or op == Operation.STATUS:
            return {"status": "DRY_RUN" if dry else "STATUS", "dryRun": dry,
                    "state": state.value if state else "UNINITIALIZED", "plan": plan(op, state),
                    "generation": item.generation if item else None}
        if item and item.document["executionId"] == eid:
            if item.document["operation"] != op.value:
                raise ControlError("EXECUTION_ID_REUSED")
            return {"status": "DUPLICATE", "state": state.value}
        if state not in (None, State.OFF, State.ON):
            raise ControlError("MANUAL_RECOVERY_REQUIRED")
        if op == Operation.START_DEV:
            if state == State.ON:
                return {"status": "NO_OP_ON"}
            return self.start(item, eid, op, actor)
        if op == Operation.STOP_DEV:
            if state == State.OFF:
                return {"status": "NO_OP_OFF"}
            if item is None:
                raise ControlError("STATE_UNINITIALIZED")
            return self.stop(item, eid, op, actor)
        raise ControlError("INVALID_OPERATION")

    def start(self, item: Stored | None, eid: str, op: Operation, actor: str) -> dict[str, Any]:
        now = stamp(self.now())
        restore = dict(item.document["resourceSnapshot"]) if item is not None else None
        if restore is not None and not restore:
            raise ControlError("SNAPSHOT_UNAVAILABLE")
        if item is None:
            doc = {"schemaVersion": 1, "environment": "DEV", "projectId": PROJECT,
                   "operation": op.value, "state": State.STARTING_SQL.value, "previousState": State.OFF.value,
                   "executionId": eid, "eventId": eid, "startedAt": now, "updatedAt": now,
                   "requestedBy": actor, "dryRun": False, "fence": 1,
                   "leaseUntil": stamp(self.now() + timedelta(minutes=self.schedule["startDeadlineMinutes"])),
                   "lastSuccessfulBackup": None, "resourceSnapshot": {}, "errorCode": None}
            item = self.store.cas(doc, 0)
        else:
            doc = dict(item.document)
            doc.update(fence=doc["fence"] + 1, startedAt=now, updatedAt=now,
                       leaseUntil=stamp(self.now() + timedelta(minutes=self.schedule["startDeadlineMinutes"])), state=State.STARTING_SQL.value,
                       previousState=State.OFF.value, operation=op.value,
                       executionId=eid, eventId=eid, requestedBy=actor, errorCode=None)
            item = self.store.cas(doc, item.generation)
        try:
            if not self.runtime.sql_running():
                self.runtime.start_sql()
            item = self.transition(item, State.WAITING_SQL, eid, op, actor)
            for attempt in range(len(self.schedule["retryDelaysSeconds"]) + 1):
                if as_utc(self.now()) >= read_time(item.document["leaseUntil"]):
                    raise ControlError("START_TIMEOUT")
                if self.runtime.sql_ready():
                    break
                if attempt == len(self.schedule["retryDelaysSeconds"]):
                    raise ControlError("SQL_NOT_READY")
                self.runtime.wait(self.schedule["retryDelaysSeconds"][attempt])
            if self.runtime.keycloak_status() not in ("NOT_PROVISIONED", "READY"):
                raise ControlError("KEYCLOAK_NOT_READY")
            item = self.transition(item, State.STARTING_SERVICES, eid, op, actor)
            snapshot = restore if restore is not None else self.runtime.run_snapshot()
            # Check and persist the scaling-only snapshot before any Run mutation.
            proposed = dict(item.document)
            proposed["resourceSnapshot"] = snapshot
            validate_state(proposed)
            item = self.store.cas(proposed, item.generation)
            self.runtime.start_run(snapshot)
            if not self.runtime.health_ready():
                raise ControlError("HEALTH_NOT_READY")
            item = self.transition(item, State.ON, eid, op, actor, snapshot=snapshot)
            return {"status": "ON", "generation": item.generation}
        except ControlError as exc:
            latest = self.store.read()
            if latest and latest.document.get("executionId") == eid:
                state = validate_state(latest.document)
                if State.ERROR in NEXT[state]:
                    self.transition(latest, State.ERROR, eid, op, actor, error=exc.code)
            raise

    def stop(self, item: Stored, eid: str, op: Operation, actor: str) -> dict[str, Any]:
        try:
            item = self.transition(item, State.FREEZING, eid, op, actor)
            item = self.transition(item, State.QUIESCING, eid, op, actor)
            first = self.runtime.stop_evidence()
            item = self.transition(item, State.WAITING_BACKUP, eid, op, actor)
            backup_id = verify_stop(first, self.now(), self.schedule["stopDeadline"])
            if self.runtime.stop_evidence() != first:
                raise ControlError("STOP_EVIDENCE_CHANGED")
            item = self.transition(item, State.STOPPING_SERVICES, eid, op, actor)
            self.runtime.stop_run()
            if not self.runtime.stop_evidence().tags_safe:
                raise ControlError("TAGS_UNSAFE")
            item = self.transition(item, State.STOPPING_SQL, eid, op, actor)
            if self.runtime.stop_evidence() != first:
                raise ControlError("STOP_EVIDENCE_CHANGED")
            self.runtime.stop_sql()
            item = self.transition(item, State.OFF, eid, op, actor, backup_id=backup_id)
            return {"status": "OFF", "backupId": backup_id, "generation": item.generation}
        except ControlError as exc:
            latest = self.store.read()
            if latest and latest.document.get("executionId") == eid:
                state = validate_state(latest.document)
                hold = State.MANUAL_HOLD if State.MANUAL_HOLD in NEXT[state] else State.ERROR
                if hold in NEXT[state]:
                    self.transition(latest, hold, eid, op, actor, error=exc.code)
            raise
