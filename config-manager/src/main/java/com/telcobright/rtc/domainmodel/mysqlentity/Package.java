package com.telcobright.rtc.domainmodel.mysqlentity;

import freeswitch.dto.PackageDto;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@Table(name = "package")
public class Package {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "basePrice", precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "vatPercent",nullable = false, precision = 5, scale = 2)
    private BigDecimal vat;

    @Column(name = "createDate", nullable = false)
    private LocalDateTime createDate;

    @Column(name = "validity", nullable = false)
    private Long validity;

    @Column(name = "aitPercent", nullable = false, precision = 5, scale = 2)
    private BigDecimal ait;

    @Column(name = "activeStatus",nullable = false)
    private boolean activeStatus;

    @OneToMany( fetch = FetchType.LAZY, cascade = CascadeType.ALL,orphanRemoval = true)
    @JoinColumn(name = "id_package")
    private Set<PackageItem> packageItems;

    @OneToMany(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_package")
    private Set<PackagePurchase> purchasepackages;

    public Package(PackageDto packageDto) {
        this.name = packageDto.getName();
        this.description = packageDto.getDescription();
        this.price = packageDto.getPrice();
        this.vat = packageDto.getVat();
        this.createDate = LocalDateTime.now();
        this.validity = packageDto.getValidity();
        this.ait = packageDto.getAit();
        this.activeStatus = packageDto.isActiveStatus();

    }

}
