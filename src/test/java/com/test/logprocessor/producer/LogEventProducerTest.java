package com.test.logprocessor.producer;

import com.test.logprocessor.domain.LogEvent;
import org.junit.Test;

import java.io.File;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static org.junit.Assert.assertEquals;

public class LogEventProducerTest {

    @Test
    public void runTest() {

        //given
        Class clazz = LogEventProducerTest.class;
        File file = new File(Objects.requireNonNull(clazz.getClassLoader().getResource("test.log")).getFile());
        BlockingQueue<LogEvent> queue = new LinkedBlockingQueue<>(10);
        LogEventProducer logEventProducer = new LogEventProducer(queue, file);

        //when
        logEventProducer.run();

        //then
        assertEquals(9, queue.size());

    }

}
