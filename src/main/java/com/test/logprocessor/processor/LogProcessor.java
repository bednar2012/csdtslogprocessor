package com.test.logprocessor.processor;

import com.test.logprocessor.consumer.LogEventConsumer;
import com.test.logprocessor.consumer.SaveEventConsumer;
import com.test.logprocessor.domain.Event;
import com.test.logprocessor.domain.LogEvent;
import com.test.logprocessor.producer.LogEventProducer;
import com.test.logprocessor.repository.EventRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class LogProcessor {

    private static final long TERMINATION_TIMEOUT = 10;
    private static final int THREAD_POOL_SIZE = 3;

    private final String filePath;
    private final int logEventsQueueCapacity;
    private final int eventsQueueCapacity;
    private final EventRepository eventRepository;

    public LogProcessor(EventRepository eventRepository,
                        @Value("${logProcessor.logEventsQueueCapacity}") int logEventsQueueCapacity,
                        @Value("${logProcessor.filePath}") String filePath,
                        @Value("${logProcessor.eventsQueueCapacity}") int eventsQueueCapacity) {
        this.eventRepository = eventRepository;
        this.logEventsQueueCapacity = logEventsQueueCapacity;
        this.filePath = filePath;
        this.eventsQueueCapacity = eventsQueueCapacity;
    }

    public void process() throws InterruptedException {

        File logFile = new File(filePath);
        if (!logFile.exists()) {
            throw new IllegalStateException(String.format("Missing log file at path %s", filePath));
        }
        final ExecutorService service = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

        BlockingQueue<LogEvent> logEventsQueue = new LinkedBlockingQueue<>(logEventsQueueCapacity);
        BlockingQueue<Event> eventsQueue = new LinkedBlockingQueue<>(eventsQueueCapacity);

        service.execute(new LogEventProducer(logEventsQueue, logFile));
        service.execute(new LogEventConsumer(logEventsQueue, eventsQueue));
        service.execute(new SaveEventConsumer(eventsQueue, eventRepository));

        service.awaitTermination(TERMINATION_TIMEOUT, TimeUnit.SECONDS);
        service.shutdown();

    }

}
