package com.telcobright.rtc.domainmodel.mysqlentity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name = "call_src")
public class CallSrc {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 100)
    private String name;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "callSrcId")
    private List<DialplanPrefix> dialPlanPrefixes;
}
