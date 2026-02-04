# TextEditor

Basic Java Swing text editor example that demonstrates concurrent main and UI threads with a background SearchTask worker, plus search, regex support, and toolbar actions.

## Requirements
- Java 25 (configured via Gradle toolchain)

## Build
```
./gradlew build
```

## Run
This project uses `ApplicationRunner` to launch the UI. Run it from your IDE or with Gradle:

```
./gradlew run
```

If `run` is not configured, you can run the `com.example.texteditor.ApplicationRunner` class directly.

## Tests
```
./gradlew test
```

## Notes
- Icons are loaded from classpath resources under `pics/icons`.
- Log4j2 is included as the logging backend.
