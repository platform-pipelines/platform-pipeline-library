#!/usr/bin/env python3
"""Print a one-line severity tally from a Trivy JSON report."""
import collections
import json
import sys

try:
    data = json.load(open(sys.argv[1], encoding="utf-8"))
except Exception:
    print("unreadable")
    sys.exit()

tally = collections.Counter()
for result in data.get("Results") or []:
    for vuln in result.get("Vulnerabilities") or []:
        tally[vuln.get("Severity", "UNKNOWN")] += 1

order = ["CRITICAL", "HIGH", "MEDIUM", "LOW", "UNKNOWN"]
parts = [f"{s}={tally[s]}" for s in order if tally[s]]
print(", ".join(parts) if parts else "clean")
