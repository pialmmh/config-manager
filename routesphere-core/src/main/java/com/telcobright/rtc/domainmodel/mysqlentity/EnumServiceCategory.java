package com.telcobright.rtc.domainmodel.mysqlentity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "enumservicecategory")
public class EnumServiceCategory {

    @Id
    @Column(name = "id")
    private Integer id;

    @Column(name = "Type", nullable = false, length = 45)
    private String type;
}
