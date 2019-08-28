package com.test.logprocessor.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
@Builder
public class Event {

    private static final long ALERT_THRESHOLD_MSC = 4;

    private String id;
    private String type;
    private String host;
    private long duration;

    public boolean isAlert() {
        return duration > ALERT_THRESHOLD_MSC;
    }

}
