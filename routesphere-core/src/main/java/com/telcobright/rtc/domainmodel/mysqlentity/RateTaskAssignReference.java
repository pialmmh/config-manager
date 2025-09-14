package com.telcobright.rtc.domainmodel.mysqlentity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "ratetaskassignreference")
public class RateTaskAssignReference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "idRatePlan", foreignKey = @ForeignKey(name = "fk_idRatePlanassign"))
    private RatePlan ratePlan;

    @Column(name = "Description", length = 300)
    private String description;
}
