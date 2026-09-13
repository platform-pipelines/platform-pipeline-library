#!/usr/bin/env python3
"""Print overall line coverage as a percentage, or -1 if it cannot be read.

Handles the three formats the pipeline produces:
  cobertura XML  (python, and some node setups)  line-rate="0.83"
  jacoco XML     (maven, gradle)                 <counter type="LINE" ...>
  go coverprofile (go)                           mode: set / file:line.col,... n m
  lcov info      (node/jest)                     LF: / LH: per file
"""
import re
import sys

path = sys.argv[1]
text = open(path, encoding="utf-8", errors="replace").read()

# cobertura
m = re.search(r'line-rate="([0-9.]+)"', text)
if m:
    print(round(float(m.group(1)) * 100, 1))
    sys.exit()

# jacoco
counters = re.findall(r'<counter type="LINE" missed="(\d+)" covered="(\d+)"', text)
if counters:
    missed = sum(int(a) for a, _ in counters)
    covered = sum(int(b) for _, b in counters)
    total = missed + covered
    print(round(covered * 100.0 / total, 1) if total else 0.0)
    sys.exit()

# lcov: LF = lines found, LH = lines hit, one pair per file
found = sum(int(v) for v in re.findall(r"^LF:(\d+)", text, re.MULTILINE))
hit = sum(int(v) for v in re.findall(r"^LH:(\d+)", text, re.MULTILINE))
if found:
    print(round(hit * 100.0 / found, 1))
    sys.exit()

# go coverprofile: "file.go:1.2,3.4 <statements> <count>"
statements = 0
covered_statements = 0
for line in text.splitlines():
    if line.startswith("mode:") or not line.strip():
        continue
    parts = line.rsplit(" ", 2)
    if len(parts) != 3:
        continue
    try:
        n, count = int(parts[1]), int(parts[2])
    except ValueError:
        continue
    statements += n
    if count > 0:
        covered_statements += n
if statements:
    print(round(covered_statements * 100.0 / statements, 1))
    sys.exit()

print(-1)
