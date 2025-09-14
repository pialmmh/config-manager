package com.telcobright.rtc.domainmodel.mysqlentity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "rateplan")
public class RatePlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "Type", nullable = false)
    private Integer type;

    @Column(name = "RatePlanName", nullable = false, length = 200, columnDefinition = "varchar(200) COLLATE utf8mb4_bin")
    private String ratePlanName;

    @Column(name = "Description", length = 200, columnDefinition = "varchar(200) COLLATE utf8mb4_bin")
    private String description;

    @Column(name = "date1", nullable = false)
    private LocalDateTime date1;

    @Column(name = "field1")
    private Integer field1;

    @Column(name = "field2")
    private Integer field2;

    @Column(name = "field3")
    private Integer field3;

    @Column(name = "field4", length = 45, columnDefinition = "varchar(45) COLLATE utf8mb4_bin")
    private String field4;

    @Column(name = "field5", length = 45, columnDefinition = "varchar(45) COLLATE utf8mb4_bin")
    private String field5;

    @Column(name = "TimeZone", nullable = false)
    private Integer timeZone;

    @Column(name = "idCarrier", nullable = false)
    private Integer idCarrier;

    @Column(name = "Currency", nullable = false, length = 10, columnDefinition = "varchar(10) COLLATE utf8mb4_bin")
    private String currency;

    @Column(name = "codedeletedate")
    private LocalDateTime codeDeleteDate;

    @Column(name = "ChangeCommitted")
    private Integer changeCommitted;

    @Column(name = "Resolution", nullable = false, columnDefinition = "int(11) DEFAULT 1")
    private Integer resolution;

    @Column(name = "mindurationsec", nullable = false, columnDefinition = "float DEFAULT 0")
    private Float minDurationSec;

    @Column(name = "SurchargeTime", nullable = false, columnDefinition = "int(11) DEFAULT 0")
    private Integer surchargeTime;

    @Column(name = "SurchargeAmount", nullable = false, precision = 20, scale = 8)
    private BigDecimal surchargeAmount;

    @Column(name = "Category", columnDefinition = "tinyint(4) DEFAULT 1")
    private Integer category;

    @Column(name = "SubCategory", columnDefinition = "tinyint(4) DEFAULT 1")
    private Integer subCategory;

    @Column(name = "BillingSpan", nullable = false, length = 10, columnDefinition = "varchar(10) COLLATE utf8mb4_bin DEFAULT '2'")
    private String billingSpan;

    @Column(name = "RateAmountRoundupDecimal")
    private Integer rateAmountRoundupDecimal;

//    @OneToMany(mappedBy = "ratePlan", fetch = FetchType.LAZY)
//    @JsonManagedReference
//    private List<Rate> rates;

    @ManyToOne
    @JoinColumn(name = "TimeZone", referencedColumnName = "id", updatable = false, insertable = false, foreignKey = @ForeignKey(name = "fk_timezone"))
    private Timezone timezone;

    @ManyToOne
    @JoinColumn(name = "BillingSpan", referencedColumnName = "ofbiz_uom_Id", updatable = false, insertable = false, foreignKey = @ForeignKey(name = "rateplan_ibfk_1"))
    private EnumBillingSpan enumBillingSpan;
    @Transient
    private LocalDateTime startDate;
    @Transient
    private LocalDateTime endDate;
}
