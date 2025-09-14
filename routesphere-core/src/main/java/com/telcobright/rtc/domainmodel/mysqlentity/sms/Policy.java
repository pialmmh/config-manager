package com.telcobright.rtc.domainmodel.mysqlentity.sms;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "policy")
public class Policy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 45)
    private String name;

    @Column(length = 255)
    private String description;

    @OneToMany()
    @JoinColumn(name = "policy_id")
    private Set<TimeBand> timeBands;//fixme:  set instead of list fixes mulipleBagFetch exception

    @OneToMany()
    @JoinColumn(name = "policy_id")
    private Set<RetryInterval> retryIntervals;

    @OneToMany()
    @JoinColumn(name = "policy_id")
    private Set<RetryCauseCode> retryCauseCodes;

}
