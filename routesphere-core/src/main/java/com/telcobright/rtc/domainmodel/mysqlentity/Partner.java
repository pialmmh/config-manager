package com.telcobright.rtc.domainmodel.mysqlentity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;
import java.util.Set;

@Entity
@Data
@Table(name = "partner")
public class Partner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idPartner")
    private Integer idPartner;

    @Column(name = "PartnerName", nullable = false, unique = true)
    private String partnerName;

    @Column(name = "AlternateNameInvoice", length = 400)
    private String alternateNameInvoice;

    @Column(name = "AlternateNameOther", length = 400)
    private String alternateNameOther;

    @Column(name = "Address1", length = 100)
    private String address1;

    @Column(name = "Address2", length = 100)
    private String address2;

    @Column(name = "City", length = 45)
    private String city;

    @Column(name = "State", length = 45)
    private String state;

    @Column(name = "PostalCode", length = 45)
    private String postalCode;

    @Column(name = "Country", length = 45)
    private String country;

    @Column(name = "Telephone", length = 45)
    private String telephone;

    @Column(name = "email", length = 50)
    private String email;

    @Column(name = "CustomerPrePaid", nullable = false)
    private Integer customerPrePaid;

    @Column(name = "PartnerType", nullable = false)
    private Integer partnerType;

    @JsonIgnore
    @Column(name = "billingdate")
    private Integer billingDate;

    @JsonIgnore
    @Column(name = "AllowedDaysForInvoicePayment")
    private Integer allowedDaysForInvoicePayment;

    @JsonIgnore
    @Column(name = "timezone")
    private Integer timezone;

    @Column(name = "date1")
    private Date date1;

    @Column(name = "field1")
    private Integer callSrcId;

    @JsonIgnore
    @Column(name = "field2")
    private Integer field2;

    @JsonIgnore
    @Column(name = "field3")
    private Integer field3;

    @JsonIgnore
    @Column(name = "field4", length = 45)
    private String field4;

    @JsonIgnore
    @Column(name = "field5", length = 45)
    private String field5;

    @JsonIgnore
    @Column(name = "refasr")
    private Float refasr;

    @JsonIgnore
    @Column(name = "refacd")
    private Float refacd;

    @JsonIgnore
    @Column(name = "refccr")
    private Float refccr;

    @JsonIgnore
    @Column(name = "refccrbycc")
    private Float refccrbycc;

    @JsonIgnore
    @Column(name = "refpdd")
    private Float refpdd;

    @JsonIgnore
    @Column(name = "refasrfas")
    private Float refasrfas;

    @Column(name = "DefaultCurrency", nullable = false, columnDefinition = "int(11) default 1")
    private Integer defaultCurrency;

    @Column(name = "invoiceAddress", length = 200)
    private String invoiceAddress;

    @Column(name = "vatRegistrationNo", length = 45)
    private String vatRegistrationNo;

    @Column(name = "paymentAdvice", length = 1000)
    private String paymentAdvice;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "idPartner")
    @JsonIgnore
    private Set<PartnerPrefix> partnerPrefixes;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "idPartner")
    @JsonIgnore
    private Set<Route> routes;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "partner_id")
    @JsonIgnore
    private Set<DidAssignment> didAssignments;
}
