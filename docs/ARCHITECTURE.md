# Architecture

## Scope

Endurance Coordination Bot is currently a small Java/JDA application that coordinates an endurance race and driver availability through Discord interactions.

The architecture described here reflects the current code. Future-state items are separated explicitly.

## Runtime entry point

`br.com.endurancebot.Main`:

1. reads `DISCORD_TOKEN` from the environment;
2. builds the JDA client;
3. registers `BotListener`;
4. waits for Discord readiness;
5. registers slash commands.

No token is intentionally stored in source code.

## Interaction routing

`BotListener` is the Discord boundary.

It receives JDA events and delegates them to command objects:

```text
SlashCommandInteractionEvent
  -> BotListener
      -> PingCommand
      -> RaceCreateCommand

ButtonInteractionEvent
  -> BotListener
      -> AvailabilityCommand

StringSelectInteractionEvent
  -> BotListener
      -> RaceCreateCommand
      -> AvailabilityCommand

ModalInteractionEvent
  -> BotListener
      -> RaceCreateCommand
      -> AvailabilityCommand
```

This keeps top-level event routing centralized, but some domain logic is still coupled to Discord-specific types.

## Race creation

`RaceCreateCommand`:

- asks for the race's official time zone;
- collects name/date/start/duration through a Discord modal;
- validates a bounded duration;
- converts local race time to an `Instant`;
- stores start/end plus the selected official time zone in `Race`;
- keeps one `activeRace` in memory.

### Current limitation

Only one active race is represented in this command instance.

This is acceptable for the current prototype, but it is not a multi-race persistence model.

## Availability workflow

`AvailabilityCommand` manages an in-memory session per Discord user.

Current flow:

```text
driver clicks availability
  -> choose driver time zone
  -> create temporary session
  -> add one or more HH:mm intervals
  -> convert intervals relative to race timing
  -> submit availability
  -> move session from active to submitted map
```

### Current limitation

Session/submission data is stored in `HashMap` instances inside the running process.

Consequences:

- restart loses state;
- multiple bot instances would not share state;
- persistence/recovery is not implemented.

## Time model

The project uses Java's time API rather than fixed UTC offsets.

Relevant types:

- `Instant` for normalized race timestamps;
- `ZoneId` for IANA time-zone identity;
- `ZonedDateTime` for conversion/display;
- `LocalDate` / `LocalTime` for user input.

This matters because endurance teams can operate across daylight-saving and date-boundary differences.

## Security boundary

Current controls visible in the repository:

- Discord token comes from `DISCORD_TOKEN`;
- `.env` is ignored;
- race creation is registered with `MANAGE_SERVER` default permission;
- user-specific workflow replies are generally ephemeral.

Current limitations:

- no formal authorization service exists;
- authorization is currently primarily Discord-command permission/configuration;
- no persisted audit log exists;
- no rate-limit/application abuse layer exists.

## Testability problem

The main architectural gap is not project size. It is that some logic that should be deterministic is embedded in JDA handlers.

The recovery plan is to extract pure logic such as:

- interval validation;
- race-time conversion;
- availability conversion;
- race-duration rules.

Then JDA handlers can focus on:

- parsing interaction input;
- calling domain/application logic;
- formatting responses.

## Target direction

Without introducing unnecessary layers, the desired direction is:

```text
Discord/JDA adapter
       |
       v
application services
       |
       v
pure domain/time rules
       |
       +--> repository boundary (future)
```

The repository should stay small. No microservices, messaging layer or framework is justified by the current problem.
