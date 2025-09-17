package com.telcobright.rtc.domainmodel.mysqlentity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "packageaccount")
@NoArgsConstructor
@Data
public class PackageAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_packageaccount")
    private Long id;

    @Column(name = "id_PackagePurchase", nullable = false, updatable = false)
    private Long idpackagePurchase;

    @Column(name = "name")
    private String name;

    @Column(name = "lastAmount",nullable = false, precision = 20, scale = 6)
    private BigDecimal lastAmount;

    @Column(name = "balanceBefore",nullable = false, precision = 20, scale = 6)
    private BigDecimal balanceBefore;

    @Column(name = "balanceAfter",nullable = false, precision = 20, scale = 6)
    private BigDecimal balanceAfter;

    @Column(name = "uom",nullable = false)
    private String uom;
}