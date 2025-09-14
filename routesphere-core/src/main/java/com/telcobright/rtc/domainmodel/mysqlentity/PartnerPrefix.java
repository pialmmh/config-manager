package com.telcobright.rtc.domainmodel.mysqlentity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "partnerprefix")
@Data
public class PartnerPrefix {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "idPartner", nullable = false)
    private Integer idPartner;

    @Column(name = "PrefixType", nullable = false)
    private Integer prefixType;

    @Column(name = "Prefix", nullable = false, length = 20)
    private String prefix;

    @Column(name = "CommonTG")
    private Integer commonTG;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "idPartner", insertable = false, updatable = false)
//    private Partner partner;
}
