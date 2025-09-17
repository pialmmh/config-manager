package com.telcobright.rtc.domainmodel.mysqlentity;

import freeswitch.dto.PackageItemDTO;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Entity
@Table(name = "packageitem")
@NoArgsConstructor
public class PackageItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "id_package" ,updatable = false)
    private Long idPackage;

    @Column(name = "quantity", nullable = false, precision = 10, scale = 2)
    private BigDecimal quantity;

    @Column(name = "id_UOM", nullable = false)
    private String idUom;

    @Column(name = "category",nullable = false)
    private int category;

    @Column(name = "prefix",nullable = false)
    private String prefix;

    @Column(name = "description" ,nullable = false)
    private String description;

    public PackageItem(PackageItemDTO packageItem) {
        this.idPackage= packageItem.getIdPackage();
        this.quantity = packageItem.getQuantity();
        this.idUom = packageItem.getIdUom();
        this.category = packageItem.getCategory();
        this.prefix = packageItem.getPrefix();
        this.description = packageItem.getDescription();

    }

}