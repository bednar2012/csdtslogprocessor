package com.test.logprocessor.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.test.logprocessor.domain.EventState;
import com.test.logprocessor.domain.LogEvent;
import lombok.extern.slf4j.Slf4j;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Slf4j
public class LogGenerator {

    private static final List<String> IDS = new ArrayList<>();
    private static final Map<String, String> APPLICATION_TYPES = new HashMap<>();

    private static final long LOGS_TO_CREATE = 5000;
    private static final int DURATION_MAX = 10;
    private static final int SPACE_MAX = 10;

    public static void main(String[] args) throws IOException {
        FileWriter fw = new FileWriter("log.log");
        IDS.add("scsmbstgra");
        IDS.add("scsmbstgrb");
        IDS.add("scsmbstgrc");

        APPLICATION_TYPES.put(null, null);
        APPLICATION_TYPES.put("APPLICATION_LOG", "12345");

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        Random idsRandom = new Random(100);
        Random typesRandom = new Random(100);
        Random durationRandom = new Random(100);
        Random spaceRandom = new Random(100);
        long currentTime = System.currentTimeMillis();

        long space = 0;
        for (int i = 0; i <= LOGS_TO_CREATE; i++) {

            String id = IDS.get(idsRandom.nextInt(IDS.size()));

            Map.Entry<String, String> entry = new ArrayList<>(APPLICATION_TYPES.entrySet()).get(typesRandom.nextInt(APPLICATION_TYPES.size()));

            long startTimestamp = currentTime + space;
            long finishTimestamp = currentTime + space + durationRandom.nextInt(DURATION_MAX) + 1;
            LogEvent start = LogEvent.builder()
                    .id(id)
                    .state(EventState.STARTED)
                    .type(entry.getKey())
                    .host(entry.getValue())
                    .timestamp(startTimestamp)
                    .build();
            LogEvent finish = LogEvent.builder()
                    .id(id)
                    .state(EventState.FINISHED)
                    .type(entry.getKey())
                    .host(entry.getValue())
                    .timestamp(finishTimestamp)
                    .build();

            space += spaceRandom.nextInt(SPACE_MAX) + 1;

            String startLog = objectMapper.writeValueAsString(start);
            String finishLog = objectMapper.writeValueAsString(finish);

            fw.write(startLog);
            fw.write("\n");
            fw.write(finishLog);
            fw.write("\n");
        }

        fw.close();


    }


}
