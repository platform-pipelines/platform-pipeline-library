#!/usr/bin/env python3
"""Summarise a `terraform show -json <plan>` document.

Prints a one-line tally and marks destructive changes, because "3 to destroy"
buried in 400 lines of plan output is exactly what gets skimmed past during an
approval. Exits 0 always -- the pipeline decides what to do with the summary.
"""
import collections
import json
import sys

DESTRUCTIVE = {"delete", "replace"}


def classify(actions):
    """Map a resource_change.change.actions list to a single verb."""
    a = list(actions)
    if a == ["no-op"]:
        return None
    if a == ["create"]:
        return "create"
    if a == ["update"]:
        return "update"
    if a == ["delete"]:
        return "delete"
    # Terraform expresses a replace as delete+create or create+delete
    # depending on create_before_destroy.
    if set(a) == {"create", "delete"}:
        return "replace"
    if a == ["read"]:
        return None
    return "+".join(a)


def main():
    try:
        plan = json.load(open(sys.argv[1], encoding="utf-8"))
    except Exception:
        print("unreadable plan")
        return

    tally = collections.Counter()
    destroyed = []

    for rc in plan.get("resource_changes") or []:
        verb = classify((rc.get("change") or {}).get("actions") or [])
        if not verb:
            continue
        tally[verb] += 1
        if verb in DESTRUCTIVE:
            destroyed.append(rc.get("address", "?"))

    if not tally:
        print("no changes")
        return

    order = ["create", "update", "replace", "delete"]
    parts = [f"{tally[v]} to {v}" for v in order if tally[v]]
    parts += [f"{n} {v}" for v, n in tally.items() if v not in order]

    line = ", ".join(parts)
    if destroyed:
        shown = ", ".join(destroyed[:5])
        more = f" (+{len(destroyed) - 5} more)" if len(destroyed) > 5 else ""
        line += f" | DESTRUCTIVE: {shown}{more}"

    print(line)


if __name__ == "__main__":
    main()
