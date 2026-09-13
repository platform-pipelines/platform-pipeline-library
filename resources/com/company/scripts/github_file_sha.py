#!/usr/bin/env python3
"""Print the blob sha from a GitHub contents API response, or nothing.

Prints nothing when the file does not exist (the API returns a 404 body),
which is how githubCommitFile distinguishes create from update.
"""
import json
import sys

try:
    data = json.load(sys.stdin)
except Exception:
    sys.exit()

if isinstance(data, dict) and "sha" in data:
    print(data["sha"])
