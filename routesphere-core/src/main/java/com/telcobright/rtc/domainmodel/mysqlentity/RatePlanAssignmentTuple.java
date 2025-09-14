package com.telcobright.rtc.domainmodel.mysqlentity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "rateplanassignmenttuple", uniqueConstraints = {
        @UniqueConstraint(name = "ind_Tuple", columnNames = {"idpartner", "route", "idService", "priority", "AssignDirection"})
})
public class RatePlanAssignmentTuple {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "idService", nullable = false)
    private Integer idService;

    @Column(name = "AssignDirection", nullable = false)
    private Integer assignDirection;

    @Column(name = "idpartner")
    private Integer idPartner;

    @Column(name = "route")
    private Integer route;

    @Column(name = "priority", nullable = false)
    private Integer priority;

    // Foreign key relationships
    @ManyToOne
    @JoinColumn(name = "route", referencedColumnName = "idroute", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "fk_route"))
    private Route routeEntity;

}
