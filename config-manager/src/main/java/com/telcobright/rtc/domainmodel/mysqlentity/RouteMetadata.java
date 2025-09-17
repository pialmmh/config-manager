package com.telcobright.rtc.domainmodel.mysqlentity;

import jakarta.persistence.*;
import lombok.Data;


@Entity
@Data
@Table(name = "route_metadata")
public class RouteMetadata {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "idRoute")
    private Integer idRoute;

    @Column(name = "sipProfileName", length = 255)
    private String sipProfileName;

    @Column(name = "extra1", length = 255)
    private String extra1;

    @Column(name = "extra2", length = 255)
    private String extra2;

    @Column(name = "extra3", length = 255)
    private String extra3;
}
