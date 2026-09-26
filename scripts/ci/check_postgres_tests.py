"""Prevent disabledWithoutDocker from turning the required database gate into a silent skip."""
from pathlib import Path
import sys
import xml.etree.ElementTree as ET

reports = [ET.parse(path).getroot() for path in Path(sys.argv[1]).glob("TEST-*PostgreSql*.xml")]
if len(reports) < 5:
    raise SystemExit("Expected the five existing PostgreSQL integration suites")
for suite in reports:
    if int(suite.get("tests", "0")) == 0 or any(
        int(suite.get(key, "0")) for key in ("skipped", "failures", "errors")
    ):
        raise SystemExit("PostgreSQL integration suite failed or skipped: " + suite.get("name", "unknown"))
print("All PostgreSQL integration suites executed successfully")
