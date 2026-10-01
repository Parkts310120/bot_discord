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

## Current engineering boundaries

This is intentionally documented as it exists today, not as a finished production system.

### In-memory state

The active race and submitted availability data are currently stored in process memory. Restarting the process loses that state.

### Discord coupling

Part of the workflow/domain logic currently lives inside JDA event handlers. A next step is to extract pure domain services that can be tested without Discord objects.

### Tests

The current repository does not yet contain the automated test suite required for portfolio-ready status.

Planned coverage includes:

- time-zone conversion across date boundaries;
- overnight availability intervals;
- invalid intervals;
- empty availability submissions;
- race duration validation;
- conversion between driver and official race zones.

### CI

A GitHub Actions workflow will be added after the test boundary is in place.

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
- [ ] add domain/time-zone tests;
- [ ] isolate testable domain logic from JDA handlers;
- [ ] add CI;
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

The bot already contains real functionality and authentic iteration history, but it is not being presented as production-ready until tests, CI and the remaining review gates are complete.
