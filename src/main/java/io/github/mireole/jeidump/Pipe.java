package io.github.mireole.jeidump;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class Pipe {
    private final Path path;
    private List<String> buffer;

    public Pipe(String path) {
        this.path = Paths.get(path);
        // This only works on Linux because mkfifo is a Linux command
        try {
            Runtime.getRuntime().exec("mkfifo " + path);
        } catch (IOException e) {
            JEIDump.LOGGER.error("Error creating pipe", e);
        }
    }

    // Reads and only returns when a new line is available
    public String read() {
        if (this.buffer == null) {
            try {
                this.buffer = Files.readAllLines(this.path);
            } catch (IOException e) {
                JEIDump.LOGGER.error("Error reading from pipe", e);
            }
        }
        while (this.buffer.isEmpty() && !Thread.interrupted()) {
            try {
                Thread.sleep(100);
                this.buffer = Files.readAllLines(this.path);
            } catch (IOException | InterruptedException e) {
                JEIDump.LOGGER.error("Error reading from pipe", e);
            }
        }
        if (Thread.interrupted()) {
            return null;
        }
        return this.buffer.remove(0);
    }

    public void close() {
        try {
            Files.delete(this.path);
        } catch (IOException e) {
            JEIDump.LOGGER.error("Error deleting file", e);
        }
    }
}
