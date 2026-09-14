#!/usr/bin/env python3
"""Summarise a CycloneDX SBOM: component count and top ecosystems."""
import collections
import json
import sys

try:
    doc = json.load(open(sys.argv[1], encoding="utf-8"))
except Exception:
    print("unreadable")
    sys.exit()

components = doc.get("components") or []
ecosystems = collections.Counter()

for c in components:
    purl = c.get("purl") or ""
    # purl looks like pkg:npm/lodash@4.17.21 — the segment after "pkg:" is the type
    if purl.startswith("pkg:") and "/" in purl:
        ecosystems[purl[4:].split("/", 1)[0]] += 1
    else:
        ecosystems[c.get("type", "unknown")] += 1

top = ", ".join(f"{k}={v}" for k, v in ecosystems.most_common(4))
print(f"{len(components)} components ({top})" if components else "0 components")
