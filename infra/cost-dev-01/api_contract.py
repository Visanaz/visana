"""Pure REST request specifications for a future Workflows adapter; no transport/auth tokens."""
from __future__ import annotations

from dataclasses import dataclass
import json
import re
from urllib.parse import quote

from control import (BUCKET, OBJECT, PROJECT, REGION, RUN, SQL, MAX_STATE_BYTES,
                     ControlError, validate_state)

CONTROL_IDENTITY = "visana-dev-schedule-control@visana-erp-dev.iam.gserviceaccount.com"
SQL_BASE = f"https://sqladmin.googleapis.com/sql/v1beta4/projects/{PROJECT}"
RUN_BASE = f"https://run.googleapis.com/v2/projects/{PROJECT}/locations/{REGION}"
STORAGE_BASE = f"https://storage.googleapis.com/storage/v1/b/{BUCKET}/o/{quote(OBJECT, safe='')}"
UPLOAD_BASE = f"https://storage.googleapis.com/upload/storage/v1/b/{BUCKET}/o"
SAFE_ID = re.compile(r"[A-Za-z0-9_-]{1,128}\Z")


@dataclass(frozen=True)
class RequestSpec:
    method: str
    url: str
    identity: str
    permission: str
    body: dict | None = None
    update_mask: str | None = None


def fixed_id(value: str) -> str:
    if not isinstance(value, str) or SAFE_ID.fullmatch(value) is None:
        raise ControlError("INVALID_RESOURCE_ID")
    return value


def sql_get() -> RequestSpec:
    return RequestSpec("GET", f"{SQL_BASE}/instances/{SQL}", CONTROL_IDENTITY,
                       "cloudsql.instances.get")


def sql_activation(policy: str) -> RequestSpec:
    if policy not in ("ALWAYS", "NEVER"):
        raise ControlError("INVALID_ACTIVATION_POLICY")
    return RequestSpec("PATCH", f"{SQL_BASE}/instances/{SQL}", CONTROL_IDENTITY,
                       "cloudsql.instances.get/update",
                       {"settings": {"activationPolicy": policy}})


def sql_operation_get(operation_id: str) -> RequestSpec:
    return RequestSpec("GET", f"{SQL_BASE}/operations/{fixed_id(operation_id)}",
                       CONTROL_IDENTITY, "cloudsql.instances.get")


def backup_list() -> RequestSpec:
    return RequestSpec("GET", f"{SQL_BASE}/instances/{SQL}/backupRuns",
                       CONTROL_IDENTITY, "cloudsql.backupRuns.list")


def run_get() -> RequestSpec:
    return RequestSpec("GET", f"{RUN_BASE}/services/{RUN}",
                       CONTROL_IDENTITY, "run.services.get")


def run_stop() -> RequestSpec:
    mask = "launchStage,scaling.scalingMode,scaling.manualInstanceCount"
    return RequestSpec("PATCH", f"{RUN_BASE}/services/{RUN}?updateMask={mask}",
                       CONTROL_IDENTITY, "run.services.update",
                       {"launchStage": "BETA", "scaling": {
                           "scalingMode": "MANUAL", "manualInstanceCount": 0}}, mask)


def run_restore(snapshot: dict) -> RequestSpec:
    if set(snapshot) != {"mode", "min", "max", "manual"}:
        raise ControlError("INVALID_SCALING_SNAPSHOT")
    if snapshot["mode"] not in ("AUTOMATIC", "MANUAL"):
        raise ControlError("INVALID_SCALING_SNAPSHOT")
    for key in ("min", "max"):
        if type(snapshot[key]) is not int or snapshot[key] < 0:
            raise ControlError("INVALID_SCALING_SNAPSHOT")
    if snapshot["min"] > snapshot["max"]:
        raise ControlError("INVALID_SCALING_SNAPSHOT")
    manual = snapshot["manual"]
    if manual is not None and (type(manual) is not int or manual < 0):
        raise ControlError("INVALID_SCALING_SNAPSHOT")
    if snapshot["mode"] == "MANUAL" and manual is None:
        raise ControlError("INVALID_SCALING_SNAPSHOT")
    mask = ("launchStage,scaling.scalingMode,scaling.manualInstanceCount,"
            "scaling.minInstanceCount,scaling.maxInstanceCount")
    body = {"launchStage": "BETA", "scaling": {
        "scalingMode": snapshot["mode"], "manualInstanceCount": manual,
        "minInstanceCount": snapshot["min"], "maxInstanceCount": snapshot["max"]}}
    return RequestSpec("PATCH", f"{RUN_BASE}/services/{RUN}?updateMask={mask}",
                       CONTROL_IDENTITY, "run.services.update", body, mask)


def run_operation_get(operation_name: str) -> RequestSpec:
    prefix = f"projects/{PROJECT}/locations/{REGION}/operations/"
    if not isinstance(operation_name, str) or not operation_name.startswith(prefix):
        raise ControlError("INVALID_RESOURCE_ID")
    fixed_id(operation_name[len(prefix):])
    return RequestSpec("GET", f"https://run.googleapis.com/v2/{operation_name}",
                       CONTROL_IDENTITY, "run.operations.get")


def object_metadata() -> RequestSpec:
    return RequestSpec("GET", STORAGE_BASE, CONTROL_IDENTITY, "storage.objects.get")


def object_generation(generation: int) -> RequestSpec:
    if type(generation) is not int or generation < 1:
        raise ControlError("INVALID_GENERATION")
    return RequestSpec("GET", f"{STORAGE_BASE}?alt=media&generation={generation}",
                       CONTROL_IDENTITY, "storage.objects.get")


def object_cas(document: dict, generation_match: int) -> RequestSpec:
    if type(generation_match) is not int or generation_match < 0:
        raise ControlError("INVALID_GENERATION")
    validate_state(document)
    encoded = json.dumps(document, separators=(",", ":"), ensure_ascii=False).encode()
    if len(encoded) > MAX_STATE_BYTES:
        raise ControlError("STATE_TOO_LARGE")
    url = (f"{UPLOAD_BASE}?uploadType=media&name={quote(OBJECT, safe='')}"
           f"&ifGenerationMatch={generation_match}")
    permission = ("storage.objects.create" if generation_match == 0
                  else "storage.objects.create,storage.objects.delete")
    return RequestSpec("POST", url, CONTROL_IDENTITY, permission, document)
