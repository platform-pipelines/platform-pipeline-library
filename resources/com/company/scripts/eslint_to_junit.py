#!/usr/bin/env python3
"""Convert ESLint JSON output to JUnit XML.

ESLint's built-in formatters are only html, json, json-with-metadata and
stylish -- `--format junit` was moved out of core into a separate package.
Rather than add an npm dependency that has to resolve from the project's
node_modules, we take `--format json` and convert here.

Usage:  eslint --format json . | eslint_to_junit.py > eslint-report.xml
"""
import json
import sys
import xml.etree.ElementTree as ET

SEVERITY = {1: "warning", 2: "error"}


def main():
    try:
        files = json.load(sys.stdin)
    except Exception:
        # No parseable output (eslint crashed, or lint produced nothing).
        # Emit a valid empty suite so the junit step does not choke.
        files = []

    suites = ET.Element("testsuites")
    total_failures = 0

    for entry in files:
        path = entry.get("filePath", "unknown")
        messages = entry.get("messages") or []

        suite = ET.SubElement(
            suites,
            "testsuite",
            name=path,
            tests=str(max(len(messages), 1)),
            failures=str(sum(1 for m in messages if m.get("severity") == 2)),
            errors="0",
            skipped="0",
        )

        if not messages:
            # A clean file still needs a passing case, otherwise the file
            # never appears in the report and coverage of the run looks partial.
            ET.SubElement(suite, "testcase", name=path, classname=path)
            continue

        for msg in messages:
            rule = msg.get("ruleId") or "syntax-error"
            line = msg.get("line", 0)
            col = msg.get("column", 0)
            case = ET.SubElement(
                suite,
                "testcase",
                name=f"{rule} ({path}:{line}:{col})",
                classname=path,
            )
            if msg.get("severity") == 2:
                total_failures += 1
                failure = ET.SubElement(
                    case,
                    "failure",
                    message=msg.get("message", ""),
                    type=SEVERITY.get(msg.get("severity"), "error"),
                )
                failure.text = f"{path}:{line}:{col}\n{msg.get('message', '')}\nRule: {rule}"
            else:
                ET.SubElement(
                    case, "skipped", message=msg.get("message", "")
                )

    suites.set("failures", str(total_failures))
    sys.stdout.write(ET.tostring(suites, encoding="unicode"))
    sys.stdout.write("\n")


if __name__ == "__main__":
    main()
