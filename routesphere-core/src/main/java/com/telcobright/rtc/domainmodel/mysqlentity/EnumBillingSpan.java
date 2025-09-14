package com.telcobright.rtc.domainmodel.mysqlentity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.io.Serializable;

@Data
@Entity
@Table(name = "enumbillingspan")
public class EnumBillingSpan implements Serializable {

    @Id
    @Column(name = "ofbiz_uom_Id", length = 10, nullable = false)
    private String ofbizUomId;

    @Column(name = "Type", length = 45, nullable = false)
    private String type;

    @Column(name = "value", nullable = false)
    private long value;
}
