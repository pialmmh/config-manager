package com.telcobright.rtc.domainmodel.mysqlentity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "packagepurchase")
public class PackagePurchase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @OneToOne
    @JoinColumn(name = "id_package", referencedColumnName = "id", nullable = false)
    private Package pkg;

    @Column(name = "id_Partner", nullable = false)
    private Long idPartner;

    @Column(name = "purchaseDate",nullable = false)
    private LocalDateTime purchaseDate;

    @Column(name = "expireDate",nullable = false)
    private LocalDateTime expireDate;

    @Column(name = "status",nullable = false, length = 50)
    private String status;

    @Column(name = "paid",nullable = false)
    private Long paid;

    @Column(name = "autoRenewalStatus",nullable = false)
    private Boolean autoRenewalStatus;

    @Column(name = "price",nullable = false,precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "vat", nullable = false, precision = 10, scale = 2)
    private BigDecimal vat;

    @Column(name = "AIT",nullable = false, precision = 10, scale = 2)
    private BigDecimal ait;

    @Column(name = "priority",nullable = false)
    private Integer priority;

    @Column(name = "onSelectPriority")
    private Integer onSelectPriority;

    @Column(name = "discount",nullable = false)
    private Integer discount;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY,orphanRemoval = true)
    @JoinColumn(name = "id_PackagePurchase")
    private List<PackageAccount> packageAccounts;
}
