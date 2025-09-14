package com.telcobright.rtc.domainmodel.mysqlentity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "dialplanroute")
public class DialplanRoute {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "idDialPlan", nullable = false)
    private Integer idDialPlan;

    @OneToOne(fetch =  FetchType.LAZY)
    @JoinColumn(name = "idRoute", nullable = false)
    private Route route;

    @Column(name = "priority")
    private Integer priority;
}
