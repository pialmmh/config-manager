package com.telcobright.rtc.domainmodel.mysqlentity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "dialplan")
public class Dialplan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 100, unique = true)
    private String name;

    @Column(length = 200)
    private String description;

    @Column(name = "is_inbound_did", nullable = false)
    private Integer isInboundDid = 0;

//    @ManyToMany(fetch = FetchType.EAGER)
//    @JoinTable(
//            name = "dialplan_mapping",
//            foreignKey = @ForeignKey(name = "idDialplanPrefix"),
//            joinColumns = @JoinColumn(name = "idDialplan"),
//            inverseJoinColumns = @JoinColumn(name = "idDialplanPrefix")
//    )
//    private Set<DialplanPrefix> dialplanPrefixes;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "idDialplan")
    @JsonIgnore
    private Set<DialplanRoute> dialplanRoutes;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "idDialplan")
    @JsonIgnore
    private Set<DialplanMapping> dialplanMappings;

}
