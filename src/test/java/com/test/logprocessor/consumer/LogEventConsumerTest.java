package com.test.logprocessor.consumer;

import com.test.logprocessor.domain.Event;
import com.test.logprocessor.domain.EventState;
import com.test.logprocessor.domain.LogEvent;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class LogEventConsumerTest {

    private BlockingQueue<LogEvent> logEvents = new LinkedBlockingQueue<>(10);
    private BlockingQueue<Event> events = new LinkedBlockingQueue<>(10);

    private LogEvent started = LogEvent.builder()
            .id("1")
            .state(EventState.STARTED)
            .timestamp(10)
            .build();
    private LogEvent finished = LogEvent.builder()
            .id("1")
            .state(EventState.FINISHED)
            .timestamp(100)
            .build();

    @Before
    public void setUp() throws Exception {
        logEvents.clear();
        events.clear();
    }

    @Test
    public void consumeTestNaturalOrder() throws InterruptedException {

        LogEventConsumer logEventConsumer = new LogEventConsumer(logEvents, events);

        logEventConsumer.consume(started);
        logEventConsumer.consume(finished);

        assertEquals(1, events.size());

        Event event = events.take();
        assertEquals("1", event.getId());
        assertEquals(90, event.getDuration());

    }

    @Test
    public void consumeTestReversedOrder() throws InterruptedException {

        LogEventConsumer logEventConsumer = new LogEventConsumer(logEvents, events);

        logEventConsumer.consume(finished);
        logEventConsumer.consume(started);

        assertEquals(1, events.size());

        Event event = events.take();
        assertEquals("1", event.getId());
        assertEquals(90, event.getDuration());
        assertTrue(event.isAlert());

    }

}
