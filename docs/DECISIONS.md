# Engineering Decisions

This file records decisions already reflected in the codebase or approved for the portfolio recovery. It intentionally separates current behavior from future work.

## 001 — Use environment configuration for Discord credentials

**Status:** current

The bot reads `DISCORD_TOKEN` from the process environment.

Reason:
- keeps the credential out of normal source code;
- works locally and in hosted environments;
- avoids coupling runtime configuration to the repository.

The repository's `.gitignore` excludes `.env`.

A history-aware secret scan is still required before the project is considered pin-ready.

## 002 — Store canonical race timestamps as Instant

**Status:** current

Race start/end are stored as `Instant`, while the selected IANA time-zone ID is kept separately.

Reason:
- gives a stable absolute timestamp;
- preserves the race's official display/conversion context;
- avoids treating a fixed offset as a full time zone.

## 003 — Use IANA ZoneId values

**Status:** current

Examples include:

- `America/Sao_Paulo`
- `America/New_York`
- `Europe/London`
- `Asia/Tokyo`

Reason:
- daylight-saving rules and historical offsets are handled by the Java time-zone database;
- drivers can express availability in their own local zone.

## 004 — Keep prototype state in memory for now

**Status:** current limitation

The active race and availability sessions are process-local.

Reason:
- the initial prototype focuses on interaction and time-zone behavior;
- adding a database before the domain flow was stable would add infrastructure without solving the first problem.

Cost:
- no restart recovery;
- no horizontal scaling;
- no durable audit history.

Persistence is a later roadmap item.

## 005 — Extract deterministic logic before adding infrastructure

**Status:** approved recovery direction

Before introducing persistence or broader architecture, isolate and test:

- interval rules;
- time-zone conversion;
- race duration validation;
- availability transformation.

Reason:
- these rules can be verified without Discord;
- the current largest technical risk is untested domain behavior, not lack of infrastructure.

## 006 — Do not rewrite authentic Git history for presentation

**Status:** approved portfolio rule

Existing commits are preserved even when early commits are imperfect.

Reason:
- authentic iteration is stronger evidence than a manufactured clean history;
- the portfolio must not fake seniority or contribution activity.

## 007 — Do not claim production readiness before verification

**Status:** approved portfolio rule

The README explicitly lists missing tests, CI and persistence.

Reason:
- technical credibility comes from accurate boundaries;
- roadmap items must not be presented as implemented features.
