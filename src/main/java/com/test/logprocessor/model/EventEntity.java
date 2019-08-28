package com.test.logprocessor.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Getter
@Setter
@Table(name = "EVENT")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "EVENT_SEQ_GENERATOR")
    @SequenceGenerator(name = "EVENT_SEQ_GENERATOR", sequenceName = "EVENT_SEQ", allocationSize = 1)
    private Long id;
    private String logId;
    private String type;
    private String host;
    private Long duration;
    private boolean alert;

}
