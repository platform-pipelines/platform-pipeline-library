#!/usr/bin/env python3
"""Print the id of the first PR comment containing a marker, else nothing.

Reads the GitHub comments JSON array on stdin.

On a malformed response (an API error body rather than a comment array) this
prints nothing, so the caller posts a new comment instead of editing. That is
the safe direction to fail: a duplicate comment is noise, whereas guessing an
id would edit somebody else's comment.
"""
import json
import sys

marker = sys.argv[1]
try:
    comments = json.load(sys.stdin)
except Exception:
    sys.exit()

for comment in comments:
    if marker in (comment.get("body") or ""):
        print(comment["id"])
        break
