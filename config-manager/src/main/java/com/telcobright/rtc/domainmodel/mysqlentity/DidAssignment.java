package com.telcobright.rtc.domainmodel.mysqlentity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Table(name = "did_assignment")
@Data
@NoArgsConstructor
public class DidAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "did_number_id", nullable = false)
    private String didNumberId;

    @Column(name = "start_date", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date startDate;

    @Column(name = "expiry_date")
    @Temporal(TemporalType.DATE)
    private Date expiryDate;

    private String description;

    @Column(name = "partner_id" )
    private Integer idPartner;

    @Column(name = "idRetailPartner")
    private Integer idRetailPartner;


    public DidAssignment(Integer idPartner, String didNumberId, Date startDate, Date expiryDate, String description) {
        this.idPartner = idPartner;
        this.didNumberId = didNumberId;
        this.startDate = startDate;
        this.expiryDate = expiryDate;
        this.description = description;
    }
}