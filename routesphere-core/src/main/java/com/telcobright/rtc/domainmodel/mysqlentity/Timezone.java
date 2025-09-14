package com.telcobright.rtc.domainmodel.mysqlentity;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;

@Data
@Entity
@Table(name = "timezone")
public class Timezone implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "zone_id", nullable = false)
    private int zoneId;

    @Column(name = "abbreviation", length = 6, nullable = false)
    private String abbreviation;

    @Column(name = "time_start", nullable = false)
    private int timeStart;

    @Column(name = "gmt_offset", nullable = false)
    private int gmtOffset;

    @Column(name = "dst", length = 1, nullable = false)
    private char dst;

    @Column(name = "offsetdesc", length = 50)
    private String offsetDesc;

}
