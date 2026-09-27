"""Prevent disabledWithoutDocker from turning the required database gate into a silent skip."""
from pathlib import Path
import sys
import xml.etree.ElementTree as ET

reports = [ET.parse(path).getroot() for path in Path(sys.argv[1]).glob("TEST-*PostgreSql*.xml")]
if len(reports) < 6:
    raise SystemExit("Expected the five persistence suites and PostgreSQL 18 lifecycle suite")
for suite in reports:
    if int(suite.get("tests", "0")) == 0 or any(
        int(suite.get(key, "0")) for key in ("skipped", "failures", "errors")
    ):
        raise SystemExit("PostgreSQL integration suite failed or skipped: " + suite.get("name", "unknown"))
    if "POSTGRESQL18_EVIDENCE 18." not in "".join(suite.itertext()):
        raise SystemExit("Real PostgreSQL 18 server evidence missing: " + suite.get("name", "unknown"))
print("All six PostgreSQL 18 integration suites executed with real server version evidence")
