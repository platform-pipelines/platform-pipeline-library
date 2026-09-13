#!/usr/bin/env python3
"""Print the SonarQube quality gate status for a finished analysis.

Usage: sonar_gate.py <ce_task_url> <sonar_host_url> <token>
Prints one of: OK, ERROR, WARN, or PENDING if the analysis is still running.
"""
import base64
import json
import sys
import urllib.request

task_url, host, token = sys.argv[1], sys.argv[2].rstrip("/"), sys.argv[3]
auth = base64.b64encode(f"{token}:".encode()).decode()


def get(url):
    req = urllib.request.Request(url, headers={"Authorization": f"Basic {auth}"})
    with urllib.request.urlopen(req, timeout=30) as resp:
        return json.load(resp)


task = get(task_url)["task"]
if task["status"] not in ("SUCCESS", "FAILED", "CANCELED"):
    print("PENDING")
    sys.exit()
if task["status"] != "SUCCESS":
    print("ERROR")
    sys.exit()

analysis_id = task.get("analysisId")
gate = get(f"{host}/api/qualitygates/project_status?analysisId={analysis_id}")
print(gate["projectStatus"]["status"])
