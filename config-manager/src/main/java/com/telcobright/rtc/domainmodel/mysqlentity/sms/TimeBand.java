package com.telcobright.rtc.domainmodel.mysqlentity.sms;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "time_band")
public class TimeBand {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "start_time")
    private LocalTime startTime;

    @Column(name = "end_time")
    private LocalTime endTime;

    @Column(name = "allow_or_restrict", nullable = false)
    private Boolean allowOrRestrict = false;

    @Column(name = "policy_id", nullable = false)
    private Integer policyId;

    @Column(name = "day")
    private String day;

    @Column(name = "specific_date_only")
    private Date specificDateOnly;

}