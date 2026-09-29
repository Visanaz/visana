"""Offline regression tests for the new controller code; no cloud credentials or calls."""
from dataclasses import replace
from datetime import datetime, timedelta, timezone
import json
from pathlib import Path
import sys
import unittest

sys.path.insert(0, str(Path(__file__).resolve().parent))
import control as c
import api_contract as api

SCHEDULE = json.loads(Path(__file__).with_name("schedule.json").read_text(encoding="utf-8-sig"))
NOW = datetime(2026, 9, 30, 2, 0, tzinfo=timezone.utc)


def request(operation="START_DEV", dryRun=False, executionId="evt-1", requestedBy="operator", **extra):
    return dict(operation=operation, dryRun=dryRun, executionId=executionId,
                requestedBy=requestedBy, **extra)


def good_evidence(now=NOW):
    fence = now - timedelta(hours=2)
    backup = c.Backup("backup-1", "SUCCESSFUL", fence + timedelta(minutes=5),
                      now - timedelta(minutes=20), fence)
    return c.StopEvidence(True, True, True, True, True, True, fence, fence, backup)


class FakeRuntime:
    def __init__(self, evidence=None):
        self.actions = []
        self.running = False
        self.ready = True
        self.kc = "NOT_PROVISIONED"
        self.health = True
        self.proof = evidence or good_evidence()
        self.fail_on = None

    def hit(self, name):
        self.actions.append(name)
        if self.fail_on == name:
            raise c.ControlError("PERMISSION_DENIED")

    def sql_running(self):
        self.hit("sql_running")
        return self.running

    def start_sql(self):
        self.hit("start_sql")
        self.running = True

    def sql_ready(self):
        self.hit("sql_ready")
        return self.ready

    def wait(self, seconds):
        self.hit("wait")

    def keycloak_status(self):
        self.hit("keycloak_status")
        return self.kc

    def run_snapshot(self):
        self.hit("run_snapshot")
        return {"mode": "AUTOMATIC", "min": 0, "max": 3, "manual": None}

    def start_run(self, snapshot):
        self.hit("start_run")

    def health_ready(self):
        self.hit("health_ready")
        return self.health

    def stop_evidence(self):
        self.hit("stop_evidence")
        return self.proof

    def stop_run(self):
        self.hit("stop_run")

    def stop_sql(self):
        self.hit("stop_sql")
        self.running = False


class ControllerTests(unittest.TestCase):
    def setUp(self):
        self.now = NOW
        self.store = c.MemoryStore()
        self.runtime = FakeRuntime()
        self.controller = c.Controller(self.store, self.runtime, lambda: self.now, SCHEDULE)

    def on(self):
        self.controller.execute(request())

    def assert_code(self, code, call):
        with self.assertRaises(c.ControlError) as caught:
            call()
        self.assertEqual(code, caught.exception.code)

    def test_allowlist_every_resource(self):
        for key, value in c.ALLOWLIST.items():
            with self.subTest(key=key):
                self.assert_code("DEV_ALLOWLIST_MISMATCH",
                                 lambda: self.controller.execute(request(**{key: value + "-other"})))
        self.assertEqual([], self.runtime.actions)
        self.assertEqual([], self.store.matches)

    def test_arbitrary_fields_forbidden(self):
        self.assert_code("UNSUPPORTED_INPUT", lambda: self.controller.execute(request(url="https://example.org")))
        self.assert_code("UNSUPPORTED_INPUT", lambda: self.controller.execute(request(project="prod")))

    def test_dry_run_missing_and_true_never_write(self):
        payload = request()
        payload.pop("dryRun")
        result = self.controller.execute(payload)
        self.assertEqual("DRY_RUN", result["status"])
        self.assertEqual([], self.store.matches)
        self.assertEqual([], self.runtime.actions)
        self.assertEqual("DRY_RUN", self.controller.execute(request(dryRun=True))["status"])

    def test_explicit_dry_run_operation_overrides_false(self):
        self.assertEqual("DRY_RUN", self.controller.execute(request("DRY_RUN_STOP"))["status"])
        self.assertEqual([], self.store.matches)

    def test_invalid_dry_run_type(self):
        self.assert_code("INVALID_DRY_RUN", lambda: self.controller.execute(request(dryRun="false")))

    def test_start_off_creates_generation_zero(self):
        self.assertEqual("ON", self.controller.execute(request())["status"])
        self.assertEqual(0, self.store.matches[0])
        self.assertEqual(c.State.ON, c.validate_state(self.store.item.document))
        self.assertEqual(["sql_running", "start_sql", "sql_ready", "keycloak_status",
                          "run_snapshot", "start_run", "health_ready"], self.runtime.actions)

    def test_start_on_is_noop(self):
        self.on()
        before = len(self.store.matches)
        self.assertEqual("NO_OP_ON", self.controller.execute(request(executionId="evt-2"))["status"])
        self.assertEqual(before, len(self.store.matches))

    def test_duplicate_event_is_idempotent(self):
        self.on()
        before = len(self.store.matches)
        self.assertEqual("DUPLICATE", self.controller.execute(request())["status"])
        self.assertEqual(before, len(self.store.matches))

    def test_cas_conflict_first_insert(self):
        self.store.conflict_once = True
        self.assert_code("CAS_CONFLICT", lambda: self.controller.execute(request()))
        self.assertEqual([], self.runtime.actions)

    def test_cas_conflict_transition(self):
        self.store.conflict_once = True
        self.assert_code("CAS_CONFLICT", lambda: self.controller.execute(request()))
        self.assertEqual([], self.store.matches)

    def test_corrupt_state(self):
        self.on()
        self.store.item.document["state"] = "BOGUS"
        self.assert_code("CORRUPT_STATE", lambda: self.controller.execute(request("STATUS")))

    def test_oversize_state(self):
        self.on()
        self.store.item.document["requestedBy"] = "x" * c.MAX_STATE_BYTES
        self.assert_code("STATE_TOO_LARGE", lambda: self.controller.execute(request("STATUS")))

    def test_keycloak_not_provisioned_is_skipped(self):
        self.on()
        self.assertIn("keycloak_status", self.runtime.actions)
        self.assertEqual("ON", self.store.item.document["state"])

    def test_unready_keycloak_keeps_sql_on(self):
        self.runtime.kc = "ERROR"
        self.assert_code("KEYCLOAK_NOT_READY", lambda: self.controller.execute(request()))
        self.assertEqual(c.State.ERROR, c.validate_state(self.store.item.document))
        self.assertTrue(self.runtime.running)
        self.assertNotIn("start_run", self.runtime.actions)

    def test_sql_unready_keeps_run_off(self):
        self.runtime.ready = False
        self.assert_code("SQL_NOT_READY", lambda: self.controller.execute(request()))
        self.assertNotIn("start_run", self.runtime.actions)

    def test_snapshot_with_secrets_rejected_before_run_change(self):
        self.runtime.run_snapshot = lambda: {"mode": "AUTOMATIC", "min": 0, "max": 3,
                                             "manual": None, "unexpectedSensitiveField": True}
        self.assert_code("CORRUPT_STATE", lambda: self.controller.execute(request()))
        self.assertNotIn("start_run", self.runtime.actions)
        self.assertTrue(self.runtime.running)

    def test_start_health_failure_is_partial_error(self):
        self.runtime.health = False
        self.assert_code("HEALTH_NOT_READY", lambda: self.controller.execute(request()))
        self.assertEqual(c.State.ERROR, c.validate_state(self.store.item.document))
        self.assertTrue(self.runtime.running)

    def test_start_permission_error(self):
        self.runtime.fail_on = "start_sql"
        self.assert_code("PERMISSION_DENIED", lambda: self.controller.execute(request()))
        self.assertEqual(c.State.ERROR, c.validate_state(self.store.item.document))

    def test_start_timeout(self):
        self.runtime.ready = False
        self.runtime.wait = lambda seconds: setattr(self, "now", NOW + timedelta(minutes=16))
        self.assert_code("START_TIMEOUT", lambda: self.controller.execute(request()))
        self.assertEqual(c.State.ERROR, c.validate_state(self.store.item.document))

    def test_retry_is_bounded(self):
        self.runtime.ready = False
        self.assert_code("SQL_NOT_READY", lambda: self.controller.execute(request()))
        self.assertEqual(4, self.runtime.actions.count("sql_ready"))
        self.assertEqual(3, self.runtime.actions.count("wait"))

    def test_stop_on_order_sql_last(self):
        self.on()
        self.runtime.actions.clear()
        self.assertEqual("OFF", self.controller.execute(request("STOP_DEV", executionId="evt-2"))["status"])
        self.assertLess(self.runtime.actions.index("stop_run"), self.runtime.actions.index("stop_sql"))

    def test_restart_restores_exact_scaling_snapshot(self):
        self.on()
        expected = dict(self.store.item.document["resourceSnapshot"])
        self.controller.execute(request("STOP_DEV", executionId="evt-2"))
        self.runtime.actions.clear()
        received = []
        self.runtime.start_run = lambda snapshot: received.append(snapshot)
        self.assertEqual("ON", self.controller.execute(request("START_DEV", executionId="evt-3"))["status"])
        self.assertEqual([expected], received)
        self.assertEqual(expected, self.store.item.document["resourceSnapshot"])

    def test_execution_id_cannot_change_operation(self):
        self.on()
        self.assert_code("EXECUTION_ID_REUSED", lambda: self.controller.execute(request("STOP_DEV")))

    def test_stop_off_noop(self):
        self.on()
        self.controller.execute(request("STOP_DEV", executionId="evt-2"))
        before = len(self.store.matches)
        self.assertEqual("NO_OP_OFF", self.controller.execute(request("STOP_DEV", executionId="evt-3"))["status"])
        self.assertEqual(before, len(self.store.matches))

    def test_missing_backup_preserves_sql(self):
        self.on()
        self.runtime.proof = replace(self.runtime.proof, backup=None)
        self.assert_code("BACKUP_MISSING", lambda: self.controller.execute(request("STOP_DEV", executionId="evt-2")))
        self.assertTrue(self.runtime.running)
        self.assertNotIn("stop_sql", self.runtime.actions)
        self.assertEqual(c.State.MANUAL_HOLD, c.validate_state(self.store.item.document))

    def test_failed_backup(self):
        self.on()
        b = replace(self.runtime.proof.backup, status="FAILED")
        self.runtime.proof = replace(self.runtime.proof, backup=b)
        self.assert_code("BACKUP_NOT_SUCCESSFUL", lambda: self.controller.execute(request("STOP_DEV", executionId="evt-2")))
        self.assertNotIn("stop_sql", self.runtime.actions)

    def test_backup_before_fence(self):
        self.on()
        b = replace(self.runtime.proof.backup, started_at=self.runtime.proof.fence_at - timedelta(seconds=1))
        self.runtime.proof = replace(self.runtime.proof, backup=b)
        self.assert_code("BACKUP_TOO_OLD", lambda: self.controller.execute(request("STOP_DEV", executionId="evt-2")))
        self.assertTrue(self.runtime.running)

    def test_backup_coverage_unproven(self):
        self.on()
        b = replace(self.runtime.proof.backup, recoverable_through=None)
        self.runtime.proof = replace(self.runtime.proof, backup=b)
        self.assert_code("BACKUP_COVERAGE_UNPROVEN", lambda: self.controller.execute(request("STOP_DEV", executionId="evt-2")))

    def test_write_after_fence(self):
        self.on()
        self.runtime.proof = replace(self.runtime.proof, last_write_at=NOW)
        self.assert_code("WRITE_AFTER_FENCE", lambda: self.controller.execute(request("STOP_DEV", executionId="evt-2")))
        self.assertTrue(self.runtime.running)

    def test_pending_sql_operation(self):
        self.on()
        self.runtime.proof = replace(self.runtime.proof, sql_operations_idle=False)
        self.assert_code("QUIESCENCE_UNPROVEN", lambda: self.controller.execute(request("STOP_DEV", executionId="evt-2")))

    def test_concurrent_deploy(self):
        self.on()
        self.runtime.proof = replace(self.runtime.proof, deployments_idle=False)
        self.assert_code("QUIESCENCE_UNPROVEN", lambda: self.controller.execute(request("STOP_DEV", executionId="evt-2")))

    def test_transactions_or_writers_unknown(self):
        self.on()
        self.runtime.proof = replace(self.runtime.proof, transactions_zero=False)
        self.assert_code("QUIESCENCE_UNPROVEN", lambda: self.controller.execute(request("STOP_DEV", executionId="evt-2")))

    def test_bogota_deadline(self):
        self.on()
        self.now = datetime(2026, 9, 30, 4, 0, tzinfo=timezone.utc)  # 23:00 Sep 29 Bogota
        self.assert_code("BACKUP_DEADLINE", lambda: self.controller.execute(request("STOP_DEV", executionId="evt-2")))
        self.assertTrue(self.runtime.running)

    def test_after_midnight_still_past_same_fence_deadline(self):
        self.on()
        self.now = datetime(2026, 9, 30, 5, 1, tzinfo=timezone.utc)  # 00:01 next Bogota day
        self.assert_code("BACKUP_DEADLINE", lambda: self.controller.execute(request("STOP_DEV", executionId="evt-2")))
        self.assertTrue(self.runtime.running)

    def test_backup_age_over_24h(self):
        self.on()
        b = replace(self.runtime.proof.backup, ended_at=NOW - timedelta(hours=25))
        self.runtime.proof = replace(self.runtime.proof, backup=b)
        self.assert_code("BACKUP_TOO_OLD", lambda: self.controller.execute(request("STOP_DEV", executionId="evt-2")))

    def test_tags_unsafe(self):
        self.on()
        self.runtime.proof = replace(self.runtime.proof, tags_safe=False)
        self.assert_code("QUIESCENCE_UNPROVEN", lambda: self.controller.execute(request("STOP_DEV", executionId="evt-2")))
        self.assertTrue(self.runtime.running)

    def test_partial_failure_requires_manual_recovery(self):
        self.runtime.health = False
        self.assert_code("HEALTH_NOT_READY", lambda: self.controller.execute(request()))
        self.runtime.health = True
        self.assert_code("MANUAL_RECOVERY_REQUIRED", lambda: self.controller.execute(request(executionId="evt-2")))

    def test_workflow_source_is_inert(self):
        path = Path(__file__).with_name("workflow.json")
        source = json.loads(path.read_text(encoding="utf-8-sig"))
        steps = source["main"]["steps"]
        names = {next(iter(step)) for step in steps}
        self.assertTrue({"checkOperation", "checkTarget", "gateLive", "rejectLive"}.issubset(names))
        self.assertEqual("LIVE_DISABLED_PENDING_A07_GATES", steps[-1]["rejectLive"]["raise"])
        self.assertNotIn('"call"', json.dumps(source))
        self.assertNotIn("Authorization", json.dumps(source))

    def test_bogota_timezone(self):
        self.assertEqual("America/Bogota", c.BOGOTA.key)
        self.assertEqual(23, datetime(2026, 9, 30, 4, tzinfo=timezone.utc).astimezone(c.BOGOTA).hour)

    def test_evidence_changed_before_sql_stop(self):
        self.on()
        original = self.runtime.stop_evidence
        count = 0
        def changing():
            nonlocal count
            count += 1
            proof = original()
            return replace(proof, writers_quiesced=False) if count >= 2 else proof
        self.runtime.stop_evidence = changing
        self.assert_code("STOP_EVIDENCE_CHANGED", lambda: self.controller.execute(request("STOP_DEV", executionId="evt-2")))
        self.assertNotIn("stop_sql", self.runtime.actions)

    def test_stop_permission_denied_keeps_sql(self):
        self.on()
        self.runtime.fail_on = "stop_run"
        self.assert_code("PERMISSION_DENIED", lambda: self.controller.execute(request("STOP_DEV", executionId="evt-2")))
        self.assertTrue(self.runtime.running)

    def test_secret_redaction(self):
        result = c.safe_log(executionId="evt-1", operation="STOP_DEV", disallowedField="synthetic", resource="https://example.invalid/path")
        self.assertNotIn("disallowedField", result)
        self.assertEqual("REDACTED", result["resource"])

    def test_r1_schedule_config_is_exact_and_rejects_drift(self):
        self.assertEqual(c.SCHEDULE_CONTRACT, c.validate_schedule(SCHEDULE))
        bad = dict(SCHEDULE, stopDeadline="00:00")
        self.assert_code("R1_SCHEDULE_MISMATCH", lambda: c.validate_schedule(bad))

    def test_retry_bound(self):
        self.assertEqual((5, 15, 30), c.RETRY_DELAYS)
        self.assertEqual(timedelta(minutes=15), c.START_LIMIT)


class ApiContractTests(unittest.TestCase):
    def test_sql_patch_is_only_activation_policy(self):
        start = api.sql_activation("ALWAYS")
        stop = api.sql_activation("NEVER")
        self.assertEqual("PATCH", start.method)
        self.assertEqual({"settings": {"activationPolicy": "ALWAYS"}}, start.body)
        self.assertEqual({"settings": {"activationPolicy": "NEVER"}}, stop.body)
        self.assertEqual("visana-dev-schedule-control@visana-erp-dev.iam.gserviceaccount.com", start.identity)
        with self.assertRaises(c.ControlError):
            api.sql_activation("SUSPENDED")

    def test_run_stop_scales_only_fixed_dev_service(self):
        spec = api.run_stop()
        self.assertTrue(spec.url.startswith("https://run.googleapis.com/v2/projects/visana-erp-dev/locations/us-central1/services/visana-api-dev?"))
        self.assertEqual(0, spec.body["scaling"]["manualInstanceCount"])
        self.assertEqual("MANUAL", spec.body["scaling"]["scalingMode"])
        self.assertNotIn("template", spec.body)
        self.assertNotIn("traffic", spec.body)

    def test_run_restore_rejects_configuration_injection(self):
        snapshot = {"mode": "AUTOMATIC", "min": 0, "max": 3, "manual": None}
        spec = api.run_restore(snapshot)
        self.assertEqual("AUTOMATIC", spec.body["scaling"]["scalingMode"])
        with self.assertRaises(c.ControlError):
            api.run_restore(dict(snapshot, env={"DB_PASSWORD": "forbidden"}))

    def test_first_and_later_cas_preconditions(self):
        runtime = FakeRuntime()
        store = c.MemoryStore()
        c.Controller(store, runtime, lambda: NOW, SCHEDULE).execute(request())
        doc = store.item.document
        first = api.object_cas(doc, 0)
        later = api.object_cas(doc, store.item.generation)
        self.assertIn("ifGenerationMatch=0", first.url)
        self.assertIn(f"ifGenerationMatch={store.item.generation}", later.url)
        self.assertIn("cost-dev-01%2Fcurrent.json", later.url)
        self.assertNotIn("storage.objects.delete", first.permission)
        self.assertIn("storage.objects.delete", later.permission)

    def test_operation_ids_cannot_change_target(self):
        for value in ("../other", "op?x=y", "op/other", ""):
            with self.subTest(value=value), self.assertRaises(c.ControlError):
                api.sql_operation_get(value)
        with self.assertRaises(c.ControlError):
            api.run_operation_get("projects/prod/locations/us-central1/operations/op1")

    def test_no_transport_in_contract_module(self):
        source = Path(api.__file__).read_text(encoding="utf-8-sig")
        self.assertNotIn("urllib.request", source)
        self.assertNotIn("socket.", source)
        self.assertNotIn("requests.", source)


class A06ContractTests(unittest.TestCase):
    def fence(self, **changes):
        return replace(c.FenceEvidence(True, True, True, True), **changes)

    def quiescence(self, **changes):
        return replace(c.QuiescenceEvidence(True, True, True, True, True, True, True), **changes)

    def backup(self, **changes):
        fence = datetime(2026, 9, 29, 17, 45, tzinfo=c.BOGOTA)
        backup = c.BackupMetadata("run-1", "SUCCESSFUL",
                                  fence + timedelta(minutes=15), fence + timedelta(minutes=30),
                                  "AUTOMATED", None, c.SQL, fence + timedelta(minutes=40))
        return fence, replace(backup, **changes)

    def coverage(self, **changes):
        fence, backup = self.backup()
        args = dict(fence_established_at=fence, last_accepted_write_at=None,
                    no_later_writes=True, recovery_coverage_confirmed=True)
        args.update(changes)
        return c.evaluate_backup_coverage(backup, **args)

    def health(self, **changes):
        return replace(c.HealthEvidence(True, True, True, 200, "UP", True, "UP", "READY"), **changes)

    def test_no_fence(self):
        self.assertEqual(c.Gate.DENY, c.evaluate_fence(self.fence(logical_fence_owned=False)))

    def test_logical_fence_only(self):
        self.assertEqual(c.Gate.DENY, c.evaluate_fence(self.fence(run_manual_zero=False)))

    def test_scaling_zero_all_routes(self):
        self.assertEqual(c.Gate.ALLOW, c.evaluate_fence(self.fence()))

    def test_tag_still_accessible(self):
        self.assertEqual(c.Gate.DENY, c.evaluate_fence(self.fence(all_write_routes_blocked=False)))

    def test_fence_unknown(self):
        self.assertEqual(c.Gate.UNKNOWN, c.evaluate_fence(self.fence(run_operation_complete=None)))

    def test_drain_incomplete(self):
        self.assertEqual(c.Gate.DENY, c.evaluate_quiescence(self.quiescence(drain_complete=False)))

    def test_deployment_in_progress(self):
        self.assertEqual(c.Gate.DENY, c.evaluate_quiescence(self.quiescence(deployments_idle=False)))

    def test_concurrent_transition(self):
        self.assertEqual(c.Gate.DENY, c.evaluate_quiescence(self.quiescence(controller_transition_idle=False)))

    def test_all_quiescence_signals(self):
        self.assertEqual(c.Gate.ALLOW, c.evaluate_quiescence(self.quiescence()))

    def test_unknown_active_writes(self):
        self.assertEqual(c.Gate.UNKNOWN, c.evaluate_quiescence(self.quiescence(active_writes_zero=None)))

    def test_backup_success_covers_fence(self):
        self.assertEqual(c.Coverage.COVERED, self.coverage())

    def test_backup_too_early(self):
        fence, backup = self.backup(start_time=datetime(2026, 9, 29, 17, 44, tzinfo=c.BOGOTA))
        self.assertEqual(c.Coverage.NOT_COVERED, c.evaluate_backup_coverage(
            backup, fence_established_at=fence, last_accepted_write_at=None,
            no_later_writes=True, recovery_coverage_confirmed=True))

    def test_backup_running(self):
        fence, backup = self.backup(status="RUNNING")
        self.assertEqual(c.Coverage.NOT_COVERED, c.evaluate_backup_coverage(
            backup, fence_established_at=fence, last_accepted_write_at=None,
            no_later_writes=True, recovery_coverage_confirmed=True))

    def test_backup_failed(self):
        fence, backup = self.backup(status="FAILED")
        self.assertEqual(c.Coverage.NOT_COVERED, c.evaluate_backup_coverage(
            backup, fence_established_at=fence, last_accepted_write_at=None,
            no_later_writes=True, recovery_coverage_confirmed=True))

    def test_backup_other_instance(self):
        fence, backup = self.backup(instance="other")
        self.assertEqual(c.Coverage.NOT_COVERED, c.evaluate_backup_coverage(
            backup, fence_established_at=fence, last_accepted_write_at=None,
            no_later_writes=True, recovery_coverage_confirmed=True))

    def test_backup_metadata_missing(self):
        fence, backup = self.backup(end_time=None)
        self.assertEqual(c.Coverage.UNKNOWN, c.evaluate_backup_coverage(
            backup, fence_established_at=fence, last_accepted_write_at=None,
            no_later_writes=True, recovery_coverage_confirmed=True))

    def test_write_after_fence(self):
        fence, backup = self.backup()
        self.assertEqual(c.Coverage.NOT_COVERED, c.evaluate_backup_coverage(
            backup, fence_established_at=fence, last_accepted_write_at=fence + timedelta(seconds=1),
            no_later_writes=True, recovery_coverage_confirmed=True))

    def test_timezone_at_window_boundary(self):
        fence, backup = self.backup(start_time=datetime(2026, 9, 30, 3, 0, tzinfo=timezone.utc),
                                    end_time=datetime(2026, 9, 30, 3, 10, tzinfo=timezone.utc),
                                    queried_at=datetime(2026, 9, 30, 3, 20, tzinfo=timezone.utc))
        self.assertEqual(c.Coverage.COVERED, c.evaluate_backup_coverage(
            backup, fence_established_at=fence, last_accepted_write_at=None,
            no_later_writes=True, recovery_coverage_confirmed=True))

    def test_backup_type_not_r1(self):
        fence, backup = self.backup(backup_type="ON_DEMAND")
        self.assertEqual(c.Coverage.NOT_COVERED, c.evaluate_backup_coverage(
            backup, fence_established_at=fence, last_accepted_write_at=None,
            no_later_writes=True, recovery_coverage_confirmed=True))

    def test_recovery_proof_missing(self):
        self.assertEqual(c.Coverage.UNKNOWN, self.coverage(recovery_coverage_confirmed=None))

    def test_later_write_status_unknown(self):
        self.assertEqual(c.Coverage.UNKNOWN, self.coverage(no_later_writes=None))

    def test_health_200_up(self):
        result = c.evaluate_authenticated_health(self.health())
        self.assertEqual((c.Gate.ALLOW,) * 4,
                         (result.process_ready, result.application_healthy,
                          result.database_healthy, result.overall))

    def test_health_200_down(self):
        self.assertEqual(c.Gate.DENY, c.evaluate_authenticated_health(self.health(body_status="DOWN")).overall)

    def test_health_401(self):
        self.assertEqual(c.Gate.DENY, c.evaluate_authenticated_health(self.health(http_status=401)).overall)

    def test_health_403(self):
        self.assertEqual(c.Gate.DENY, c.evaluate_authenticated_health(self.health(http_status=403)).overall)

    def test_health_timeout(self):
        self.assertEqual(c.Gate.DENY, c.evaluate_authenticated_health(self.health(timed_out=True)).overall)

    def test_idp_not_provisioned(self):
        self.assertEqual(c.Gate.UNKNOWN, c.evaluate_authenticated_health(
            self.health(idp_status="NOT_PROVISIONED")).overall)

    def test_datasource_not_visible(self):
        result = c.evaluate_authenticated_health(self.health(datasource_included=None))
        self.assertEqual(c.Gate.UNKNOWN, result.database_healthy)
        self.assertEqual(c.Gate.UNKNOWN, result.overall)

    def test_stop_only_all_green(self):
        self.assertEqual(c.Gate.ALLOW, c.can_stop_dev(c.Gate.ALLOW, c.Gate.ALLOW, c.Coverage.COVERED))

    def test_stop_denied_by_any_failure(self):
        self.assertEqual(c.Gate.DENY, c.can_stop_dev(c.Gate.ALLOW, c.Gate.DENY, c.Coverage.COVERED))

    def test_stop_unknown_fails_closed(self):
        self.assertEqual(c.Gate.UNKNOWN, c.can_stop_dev(c.Gate.ALLOW, c.Gate.UNKNOWN, c.Coverage.COVERED))
        self.assertNotEqual(c.Gate.ALLOW, c.can_stop_dev(c.Gate.ALLOW, c.Gate.ALLOW, c.Coverage.UNKNOWN))

    def test_offline_stop_scales_run_before_backup_evidence(self):
        runtime, store = FakeRuntime(), c.MemoryStore()
        controller = c.Controller(store, runtime, lambda: NOW, SCHEDULE)
        controller.execute(request())
        runtime.actions.clear()
        controller.execute(request("STOP_DEV", executionId="evt-2"))
        self.assertLess(runtime.actions.index("stop_run"), runtime.actions.index("stop_evidence"))
        self.assertEqual(1, runtime.actions.count("stop_run"))

    def test_workflow_preflight_has_a06_gates_and_no_cloud_calls(self):
        source = json.loads(Path(__file__).with_name("workflow.json").read_text(encoding="utf-8-sig"))
        encoded = json.dumps(source)
        for required in ("logicalFence", "runManualZero", "allWriteRoutesBlocked",
                         "activeWritesZero", "backupCoverage", "stopUnknown"):
            self.assertIn(required, encoded)
        self.assertNotIn('"call"', encoded)
        self.assertIn("LIVE_DISABLED_PENDING_A07_GATES", encoded)


if __name__ == "__main__":
    unittest.main()
