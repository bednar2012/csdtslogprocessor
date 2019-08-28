package com.test.logprocessor.consumer;

import com.test.logprocessor.domain.Event;
import com.test.logprocessor.domain.EventState;
import com.test.logprocessor.domain.LogEvent;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.function.Predicate;

@Slf4j
public class LogEventConsumer extends QueueConsumerRunnable<LogEvent> {

    private Map<LogEventKey, List<LogEvent>> startedEventsMap = new HashMap<>();
    private Map<LogEventKey, List<LogEvent>> finishedEventsMap = new HashMap<>();
    private BlockingQueue<Event> eventsQueue;

    public LogEventConsumer(BlockingQueue<LogEvent> logEventsQueue, BlockingQueue<Event> eventsQueue) {
        super(logEventsQueue);
        this.eventsQueue = eventsQueue;
    }

    protected void consume(LogEvent logEvent) {
        if (logEvent.getState() == EventState.STARTED) {
            handleStartLogEvent(logEvent);
        }
        if (logEvent.getState() == EventState.FINISHED) {
            handleFinishedLogEvent(logEvent);
        }
    }

    private void handleStartLogEvent(LogEvent startLogEvent) {
        log.debug("START event found: {}", startLogEvent);
        LogEventKey key = new LogEventKey(startLogEvent);
        List<LogEvent> finishedEvents = finishedEventsMap.get(key);
        Optional<LogEvent> finishEventOptional = findFirstAndRemove(finishedEvents, (event) -> event.getTimestamp() >= startLogEvent.getTimestamp());
        if (!finishEventOptional.isPresent()) {
            log.debug("STARTED event found without FINISHED: {}", startLogEvent);
            storeEvent(startLogEvent, startedEventsMap);
        } else {
            LogEvent finishLogEvent = finishEventOptional.get();
            log.debug("FINISHED event {} with STARTED: {} match", finishLogEvent, startLogEvent);
            putEventToQueue(startLogEvent, finishLogEvent);
        }
    }

    private void handleFinishedLogEvent(LogEvent finishedLogEvent) {
        LogEventKey key = new LogEventKey(finishedLogEvent);
        List<LogEvent> startedEventsQueue = startedEventsMap.get(key);
        Optional<LogEvent> startEventOptional = findFirstAndRemove(startedEventsQueue, (event) -> event.getTimestamp() <= finishedLogEvent.getTimestamp());
        if (!startEventOptional.isPresent()) {
            log.debug("FINISHED event before any STARTED: {}", finishedLogEvent);
            storeEvent(finishedLogEvent, finishedEventsMap);
        } else {
            LogEvent startedEvent = startEventOptional.get();
            log.debug("FINISHED event {} with STARTED: {} match", finishedLogEvent, startedEvent);
            putEventToQueue(startedEvent, finishedLogEvent);
        }
    }

    private void putEventToQueue(LogEvent startEvent, LogEvent finishedEvent) {
        try {
            Event event = Event.builder()
                    .host(finishedEvent.getHost())
                    .id(finishedEvent.getId())
                    .type(finishedEvent.getType())
                    .duration(finishedEvent.getTimestamp() - startEvent.getTimestamp())
                    .build();
            eventsQueue.put(event);
        } catch (InterruptedException e) {
            throw new RuntimeException("Exception when putting event to queue", e);
        }
    }

    private void storeEvent(LogEvent logEvent, Map<LogEventKey, List<LogEvent>> map) {
        LogEventKey key = new LogEventKey(logEvent);
        List<LogEvent> logEvents = map.computeIfAbsent(key, k -> new ArrayList<>());
        logEvents.add(logEvent);
        logEvents.sort(Comparator.comparing(LogEvent::getTimestamp));
    }

    private Optional<LogEvent> findFirstAndRemove(List<LogEvent> list, Predicate<LogEvent> predicate) {
        if (list == null || list.isEmpty()) {
            return Optional.empty();
        }
        Iterator<LogEvent> it = list.iterator();
        while (it.hasNext()) {
            LogEvent event = it.next();
            if (predicate.test(event)) {
                it.remove();
                return Optional.of(event);
            }
        }
        return Optional.empty();
    }

}
