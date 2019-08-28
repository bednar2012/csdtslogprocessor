package com.test.logprocessor.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.test.logprocessor.domain.LogEvent;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;

@Slf4j
public class LogEventProducer implements Runnable {

    private ObjectMapper objectMapper;
    private BlockingQueue<LogEvent> logEvents;
    private File logFile;

    public LogEventProducer(BlockingQueue<LogEvent> logEvents, File logFile) {
        this.logEvents = logEvents;
        this.logFile = logFile;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public void run() {
        produce();
    }

    private void produce() {
        try {
            Files.newBufferedReader(logFile.toPath()).lines().forEach(line -> {
                Optional<LogEvent> logEvent = parseLine(line);
                if (logEvent.isPresent()) {
                    log.debug("Read log event: {}", logEvent);
                    try {
                        logEvents.put(logEvent.get());
                    } catch (InterruptedException e) {
                        throw new RuntimeException("Exception during log event put to queue", e);
                    }
                }
            });
        } catch (IOException e) {
            throw new RuntimeException("Log file read error", e);
        }
    }

    private Optional<LogEvent> parseLine(String line) {
        try {
            return Optional.of(objectMapper.readValue(line, LogEvent.class));
        } catch (IOException e) {
            log.warn("Log event Parse exception", e);
            return Optional.empty();
        }
    }

}
