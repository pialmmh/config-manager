package com.telcobright.rtc.domainmodel.mysqlentity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "didAssignment_retailPartner_Mapping",
        uniqueConstraints = @UniqueConstraint(columnNames = {"idRetailPartner", "idDidAssignment"}))
@Getter
@Setter
public class DidAssignmentRetailPartnerMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "idRetailPartner", nullable = false, foreignKey = @ForeignKey(name = "fk_mapping_retailpartner"))
    private RetailPartner retailPartner;

    @ManyToOne
    @JoinColumn(name = "idDidAssignment", nullable = false, foreignKey = @ForeignKey(name = "fk_mapping_didassignment"))
    private DidAssignment didAssignment;
}
