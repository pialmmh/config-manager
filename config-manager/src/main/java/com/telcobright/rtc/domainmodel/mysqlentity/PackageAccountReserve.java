package com.telcobright.rtc.domainmodel.mysqlentity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "packageaccountreserve")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PackageAccountReserve {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_packageaccountreserve")
    private Long idPackageAccountReserve;

    @Column(name = "id_packageaccount", nullable = false)
    private Long idPackageAccount;

    @Column(name = "channel_call_uuid", nullable = false)
    private String channel_call_uuid;

    @Column(name = "id_PackagePurchase", nullable = false)
    private Long idPackagePurchase;

    @Column(name = "name")
    private String name;

    @Column(name = "reserveUnit", precision = 20, scale = 6)
    private BigDecimal reserveUnit;

    @Column(name = "uom")
    private String uom;

    @Column(name = "time")
    private String time;

    // Optional: Add relationships if needed
    // @ManyToOne(fetch = FetchType.LAZY)
    // @JoinColumn(name = "id_packageaccount", insertable = false, updatable = false)
    // private PackageAccount packageAccount;

    // @ManyToOne(fetch = FetchType.LAZY)
    // @JoinColumn(name = "id_PackagePurchase", insertable = false, updatable = false)
    // private PackagePurchase packagePurchase;

}