package com.telcobright.rtc.domainmodel.mysqlentity.sms;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "retry_interval")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RetryInterval {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "policy_id", nullable = false)
    private Integer policyId;

    @Column(name = "retry_count", nullable = false)
    private int retryCount;

    @Column(name = "interval_sec", nullable = false)
    private long intervalSec;
}
