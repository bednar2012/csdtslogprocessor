package com.test.logprocessor;

import com.test.logprocessor.processor.LogProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
@RequiredArgsConstructor
public class LogProcessorApplication implements ApplicationRunner {

    final LogProcessor logProcessor;

    public static void main(String[] args) {
        log.info("STARTING THE APPLICATION");
        SpringApplication.run(LogProcessorApplication.class, args);
        log.info("APPLICATION FINISHED");
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        logProcessor.process();
    }


}
