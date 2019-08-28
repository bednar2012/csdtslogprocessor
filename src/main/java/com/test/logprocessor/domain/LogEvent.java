package com.test.logprocessor.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class LogEvent {

    private String id;
    private EventState state;
    private String type;
    private String host;
    private long timestamp;

}
