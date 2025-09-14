package com.telcobright.rtc.domainmodel.mysqlentity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;
import java.util.Set;

@Entity
@Data
@Table(name = "route",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"RouteName", "SwitchId"})})
public class Route {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idroute", nullable = false)
    private Integer idroute;

    @Column(name = "RouteName", nullable = false, length = 45)
    private String routeName;

    @Column(name = "SwitchId", nullable = false)
    private Integer switchId;

    @Column(name = "CommonRoute", nullable = false, columnDefinition = "int(11) default 0")
    private Integer commonRoute = 0;

    @Column(name = "idPartner", nullable = false)
    private Integer idPartner;

    @Column(name = "NationalOrInternational", nullable = false, columnDefinition = "int(11) default 0")
    private Integer nationalOrInternational = 0;

    @Column(name = "Description", length = 45)
    private String description;

    @Column(name = "Status", nullable = false, columnDefinition = "int(11) default 0")
    private Integer status = 0;

    @JsonIgnore
    @Column(name = "date1")
    @Temporal(TemporalType.TIMESTAMP)
    private Date date1;

    @JsonIgnore
    @Column(name = "field1")
    private Integer field1;

    @JsonIgnore
    @Column(name = "field2")
    private Integer field2;

    @JsonIgnore
    @Column(name = "field3")
    private Integer field3;

    @JsonIgnore
    @Column(name = "field4")
    private Integer field4;

    @Column(name = "field5", length = 45)
    private String field5;

    @Column(name = "zone", length = 255)
    private String zone;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "idRoute")
    private Set<RouteMetadata> routeMetaData;
}
