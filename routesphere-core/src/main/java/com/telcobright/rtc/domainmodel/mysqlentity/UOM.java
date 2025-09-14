package com.telcobright.rtc.domainmodel.mysqlentity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "uom")
public class UOM {

    @Id
    @Column(name = "UOM_ID", nullable = false, length = 20)
    private String uomId;

    @Column(name = "UOM_TYPE_ID", length = 20)
    private String uomTypeId;

    @Column(name = "ABBREVIATION", length = 60)
    private String abbreviation;

    @Column(name = "DESCRIPTION", length = 255)
    private String description;

    @Column(name = "LAST_UPDATED_STAMP")
    private LocalDateTime lastUpdatedStamp;

    @Column(name = "LAST_UPDATED_TX_STAMP")
    private LocalDateTime lastUpdatedTxStamp;

    @Column(name = "CREATED_STAMP")
    private LocalDateTime createdStamp;

    @Column(name = "CREATED_TX_STAMP")
    private LocalDateTime createdTxStamp;
}
