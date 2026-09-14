#!/usr/bin/env python3
"""Summarise `aws cloudformation describe-change-set` JSON.

Marks Replacement=True resources, which are the CloudFormation equivalent of
a Terraform replace: the resource is destroyed and recreated, so anything
stateful behind it is lost.
"""
import collections
import json
import sys

def main():
    try:
        doc = json.load(open(sys.argv[1], encoding="utf-8"))
    except Exception:
        print("unreadable changeset")
        return

    status = doc.get("Status", "")
    reason = doc.get("StatusReason", "") or ""

    # A FAILED change set is only a no-op when the reason says so. Any other
    # failure (a template format error, a missing parameter) must NOT be
    # reported as "no changes" -- that would let a broken template sail
    # through the pipeline as a successful deploy that did nothing.
    no_change_markers = ("didn't contain changes", "No updates are to be performed")
    if status == "FAILED":
        if any(m in reason for m in no_change_markers):
            print("no changes")
        else:
            print(f"failed: {reason[:160]}" if reason else "failed")
        return

    changes = doc.get("Changes") or []
    if not changes:
        print("no changes")
        return

    tally = collections.Counter()
    replaced = []

    for change in changes:
        rc = change.get("ResourceChange") or {}
        action = (rc.get("Action") or "Unknown").lower()
        tally[action] += 1
        if rc.get("Replacement") == "True":
            replaced.append(rc.get("LogicalResourceId", "?"))
            tally["replace"] += 1
            tally[action] -= 1

    order = ["add", "modify", "replace", "remove"]
    parts = [f"{tally[a]} to {a}" for a in order if tally[a] > 0]
    parts += [f"{n} {a}" for a, n in tally.items() if a not in order and n > 0]

    line = ", ".join(parts) if parts else "no changes"
    if replaced:
        shown = ", ".join(replaced[:5])
        more = f" (+{len(replaced) - 5} more)" if len(replaced) > 5 else ""
        line += f" | REPLACEMENT: {shown}{more}"

    print(line)


if __name__ == "__main__":
    main()
