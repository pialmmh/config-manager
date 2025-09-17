package com.telcobright.rtc.domainmodel.mysqlentity.sms;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "enumjobstatus")
@Getter
@Setter
public class EnumJobStatus {

    @Id
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "Type", nullable = false, length = 45, columnDefinition = "varchar(45) COLLATE utf8mb4_bin")
    private String type;
}
