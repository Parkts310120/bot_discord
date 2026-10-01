# Endurance Coordination Bot

A Java Discord bot for coordinating endurance racing teams.

The current project focuses on two practical problems:

1. creating an endurance race with an explicit official time zone;
2. collecting driver availability across different time zones and converting it back to the race's official clock.

The repository is being evolved as an engineering portfolio project from a real working prototype. The goal is to preserve the authentic history while improving testability, documentation, CI and separation between Discord-specific code and domain logic.

## What it does today

- registers Discord slash commands with JDA;
- exposes a `/ping` health-style command;
- lets authorized users create a race;
- stores race start/end as `Instant`;
- keeps the race's official `ZoneId`;
- lets drivers select their own time zone;
- accepts multiple availability intervals;
- converts availability between driver-local time and the race's official time;
- uses ephemeral Discord interactions for user-specific steps;
- reads the Discord token from `DISCORD_TOKEN`.

## Stack

- Java 21
- Maven
- JDA 6.4.2
- JUnit Jupiter
- Java Time API (`Instant`, `ZoneId`, `ZonedDateTime`)

## Current architecture

```text
Discord
  |
  v
BotListener
  |
  +--> PingCommand
  |
  +--> RaceCreateCommand
  |      |
  |      +--> Race
  |
  +--> AvailabilityCommand
         |
         +--> availability session state
         +--> timezone conversion
```

More detail: [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md)

## Configuration

The bot expects the Discord token in an environment variable:

```text
DISCORD_TOKEN
```

Do not commit real Discord credentials. The current `.gitignore` excludes `.env`.

## Local run

Requirements:

- JDK 21
- Maven
- a Discord application/bot token

With `DISCORD_TOKEN` configured in the environment:

```bash
mvn compile exec:java
```

The Maven configuration points the exec plugin to:

```text
br.com.endurancebot.Main
```

## Verification

The deterministic service/repository layer now has automated tests for:

- conversion from driver-local time to canonical `Instant` values;
- overnight intervals that cross a local date boundary;
- invalid IANA time-zone identifiers;
- race-date selection as seen in the driver's time zone;
- availability filtering by race;
- defensive list copies in the in-memory repository/catalog;
- time-zone catalog lookup and validation.

Run the same Maven verification used by CI:

```bash
mvn verify
```

The test suite does not connect to Discord and does not require `DISCORD_TOKEN`.

### CI

`.github/workflows/ci.yml` runs Maven verification on Java 21 for pushes to `main` / portfolio branches and pull requests targeting `main`.

The workflow uses read-only repository permissions and pins the checkout/setup actions to reviewed commit SHAs.

## Current engineering boundaries

This is intentionally documented as it exists today, not as a finished production system.

### In-memory state

The active race and submitted availability data are currently stored in process memory. Restarting the process loses that state.

### Discord coupling

Part of the workflow/domain logic currently lives inside JDA event handlers. The present tests cover existing pure services and repositories; JDA interaction handlers still need cleaner domain boundaries before they can be tested without Discord objects.

### Test gaps

Still not covered automatically:

- race-creation parsing and duration validation inside the JDA handler;
- empty availability submission behavior;
- interval overlap policy;
- Discord permission/integration behavior.

These are explicit gaps, not features claimed to be verified.

## Engineering decisions

See [docs/DECISIONS.md](docs/DECISIONS.md).

Key decisions already visible in the code:

- tokens come from environment variables rather than source code;
- race timestamps are normalized to `Instant`;
- explicit IANA time-zone IDs are retained for display/conversion;
- race creation requires Discord server-management permission;
- user-specific configuration steps use ephemeral replies;
- the project currently favors a small codebase over premature framework adoption.

## Roadmap

### P0 — portfolio recovery

- [x] preserve existing commit history;
- [x] document current architecture and limitations;
- [x] add deterministic domain/time-zone service tests;
- [ ] isolate remaining deterministic logic from JDA handlers;
- [x] add Java 21 CI with `mvn verify`;
- [ ] perform a history-aware secret scan;
- [ ] add screenshots/example interaction flow.

### Later

- persistent storage;
- multiple active races;
- stronger validation for overlapping intervals;
- explicit application/service boundaries;
- structured logging;
- production deployment guidance.

These are roadmap items, not features claimed to exist today.

## AI-assisted engineering

AI tools may be used for research, review, decomposition, test design, debugging and documentation.

The engineering workflow for this repository is:

```text
define the problem
  -> inspect the current behavior
  -> design the change
  -> test
  -> implement
  -> validate
  -> review
  -> decide
```

The goal is not to hide AI assistance. The goal is to keep the technical decisions, validation and responsibility explicit.

## Status

**Active recovery / portfolio hardening**

The bot now has deterministic automated tests and Java 21 CI, but it is not being presented as production-ready. JDA/domain separation, broader behavior coverage, a history-aware secret scan and the remaining portfolio review gates are still open.
