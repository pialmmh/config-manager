package com.telcobright.rtc.domainmodel.mysqlentity.sms;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "SmsQueue")
@Data
public class SmsQueue {

    @Id
    private Integer id;

    @Column(name = "topic_name", nullable = false, length = 100, columnDefinition = "varchar(100) COLLATE utf8mb4_bin")
    private String topicName;

    @Column(name = "description", length = 255, columnDefinition = "varchar(255) COLLATE utf8mb4_bin")
    private String description;

    @Column(name = "admin_state", nullable = false)
    private Boolean adminState;

    @Column(name = "created_on", nullable = false, updatable = false)
    private LocalDateTime createdOn;

    @Column(name = "updated_on", nullable = false)
    private LocalDateTime updatedOn;
}

