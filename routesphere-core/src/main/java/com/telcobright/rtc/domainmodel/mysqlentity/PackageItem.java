package com.telcobright.rtc.domainmodel.mysqlentity;


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


}
