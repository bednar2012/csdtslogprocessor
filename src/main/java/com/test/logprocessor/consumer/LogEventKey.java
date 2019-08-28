package com.test.logprocessor.consumer;

import com.test.logprocessor.domain.LogEvent;

import java.util.Objects;

public class LogEventKey {

    private String id;
    private String type;
    private String host;

    LogEventKey(LogEvent logEvent) {
        this.id = logEvent.getId();
        this.type = logEvent.getType();
        this.host = logEvent.getHost();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LogEventKey eventKey = (LogEventKey) o;
        return id.equals(eventKey.id) &&
                Objects.equals(type, eventKey.type) &&
                Objects.equals(host, eventKey.host);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, type, host);
    }

}
