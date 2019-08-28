package com.test.logprocessor.consumer;

import com.test.logprocessor.domain.Event;
import com.test.logprocessor.model.EventEntity;
import com.test.logprocessor.repository.EventRepository;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.BlockingQueue;

@Slf4j
public class SaveEventConsumer extends QueueConsumerRunnable<Event> {

    private final EventRepository eventRepository;

    public SaveEventConsumer(BlockingQueue<Event> queue, EventRepository eventRepository) {
        super(queue);
        this.eventRepository = eventRepository;
    }

    @Override
    protected void consume(Event event) {
        log.info("Saving Event: {}", event);

        EventEntity eventEntity = EventEntity.builder()
                .logId(event.getId())
                .type(event.getType())
                .host(event.getHost())
                .alert(event.isAlert())
                .duration(event.getDuration())
                .build();

        eventRepository.save(eventEntity);
        log.info("Event saved: {}", event);
    }

}
