# TextEditor

Basic Java Swing text editor example that demonstrates concurrent main and UI threads with a background SearchTask worker, plus search, regex support, and toolbar actions.

Also includes a tiny Flipper demo GUI app. It demonstrates Swing concurrency and UI updates:  
- a background SwingWorker continuously flips a virtual coin, publishes results to the EDT, and updates the UI with heads/total/deviation while Start/Stop controls manage worker lifecycle and cancellation.
- EDT is the Event Dispatch Thread in Swing. It’s the single UI thread responsible for painting and handling events, so UI updates must happen on it to avoid race conditions or rendering glitches.

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
- These apps are created for educational purposes.
