package com.telcobright.rtc.domainmodel.mysqlentity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "rateassign")
public class RateAssign {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "Prefix", nullable = false)
    private Integer prefix;

    @Column(name = "description", length = 200, columnDefinition = "varchar(200) COLLATE utf8mb4_bin")
    private String description;

    @Column(name = "rateamount", nullable = false, precision = 20, scale = 8)
    private BigDecimal rateAmount;

    @Column(name = "WeekDayStart", nullable = false, columnDefinition = "int(11) DEFAULT 1")
    private Integer weekDayStart;

    @Column(name = "WeekDayEnd", nullable = false, columnDefinition = "int(11) DEFAULT 7")
    private Integer weekDayEnd;

//    @Column(name = "starttime")
//    private LocalDateTime startTime;
//
//    @Column(name = "endtime")
//    private LocalDateTime endTime;

    @Column(name = "Resolution", nullable = false, columnDefinition = "int(11) DEFAULT 1")
    private Integer resolution;

    @Column(name = "MinDurationSec", nullable = false)
    private Float minDurationSec;

    @Column(name = "SurchargeTime", nullable = false, columnDefinition = "int(11) DEFAULT 0")
    private Integer surchargeTime;

    @Column(name = "SurchargeAmount", nullable = false, precision = 20, scale = 8)
    private BigDecimal surchargeAmount;

    @Column(name = "idrateplan")
    private Long idRatePlan;

    @Column(name = "CountryCode", length = 20, columnDefinition = "varchar(20) COLLATE utf8mb4_bin")
    private String countryCode;

//    @Column(name = "date1")
//    private LocalDateTime date1;

    @Column(name = "field1")
    private Integer field1;

    @Column(name = "field2")
    private Integer field2;

    @Column(name = "field3", nullable = false)
    private Integer field3;

    @Column(name = "field4", length = 45, columnDefinition = "varchar(45) COLLATE utf8mb4_bin")
    private String field4;

    @Column(name = "field5", length = 45, columnDefinition = "varchar(45) COLLATE utf8mb4_bin")
    private String field5;

    @Column(name = "startdate", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "enddate")
    private LocalDateTime endDate;

    @Column(name = "Inactive", nullable = false, columnDefinition = "int(11) DEFAULT 0")
    private Integer inactive;

    @Column(name = "RouteDisabled", nullable = false, columnDefinition = "int(11) DEFAULT 0")
    private Integer routeDisabled;

    @Column(name = "Type", nullable = false)
    private Integer type;

    @Column(name = "Currency", nullable = false)
    private Integer currency;

    @Column(name = "OtherAmount1", columnDefinition = "float DEFAULT 51")
    private Float otherAmount1;

    @Column(name = "OtherAmount2", columnDefinition = "float DEFAULT 15")
    private Float otherAmount2;

    @Column(name = "OtherAmount3", columnDefinition = "float DEFAULT 20")
    private Float otherAmount3;

    @Column(name = "OtherAmount4", precision = 20, scale = 8)
    private BigDecimal otherAmount4;

    @Column(name = "OtherAmount5", precision = 20, scale = 8)
    private BigDecimal otherAmount5;

    @Column(name = "OtherAmount6", columnDefinition = "float DEFAULT 40")
    private Float otherAmount6;

    @Column(name = "OtherAmount7", columnDefinition = "float DEFAULT 15")
    private Float otherAmount7;

    @Column(name = "OtherAmount8", columnDefinition = "float DEFAULT 15")
    private Float otherAmount8;

    @Column(name = "OtherAmount9", columnDefinition = "float DEFAULT 30")
    private Float otherAmount9;

    @Column(name = "OtherAmount10", columnDefinition = "float DEFAULT 65.75")
    private Float otherAmount10;

    @Column(name = "TimeZoneOffsetSec", nullable = false, precision = 20, scale = 8)
    private BigDecimal timeZoneOffsetSec;

    @Column(name = "RatePosition")
    private Integer ratePosition;

    @Column(name = "IgwPercentageIn", columnDefinition = "float DEFAULT NULL")
    private Float igwPercentageIn;

    @Column(name = "ConflictingRateIds", length = 300, columnDefinition = "varchar(300) COLLATE utf8mb4_bin")
    private String conflictingRateIds;

    @Column(name = "ChangedByTaskId")
    private Long changedByTaskId;

    @Column(name = "ChangedOn")
    private LocalDateTime changedOn;

    @Column(name = "Status")
    private Integer status;

//    @Column(name = "idPreviousRate")
//    private Long idPreviousRate;

    @Column(name = "EndPreviousRate")
    private Boolean endPreviousRate;

    @Column(name = "Category", columnDefinition = "tinyint(4) DEFAULT 1")
    private Integer category;

    @Column(name = "SubCategory", columnDefinition = "tinyint(4) DEFAULT 1")
    private Integer subCategory;

    @Column(name = "ChangeCommitted", columnDefinition = "int(11) DEFAULT 0")
    private Integer changeCommitted;

    @Column(name = "ConflictingRates", length = 50, columnDefinition = "varchar(50) COLLATE utf8mb4_bin")
    private String conflictingRates;

    @Column(name = "OverlappingRates", length = 50, columnDefinition = "varchar(50) COLLATE utf8mb4_bin")
    private String overlappingRates;

    @Column(name = "Comment1", length = 200, columnDefinition = "varchar(200) COLLATE utf8mb4_bin")
    private String comment1;

    @Column(name = "Comment2", length = 200, columnDefinition = "varchar(200) COLLATE utf8mb4_bin")
    private String comment2;

    @Column(name = "BillingParams", length = 500, columnDefinition = "varchar(500) COLLATE utf8mb4_bin")
    private String billingParams;

    // Foreign key mapping
    @ManyToOne
    @JoinColumn(name = "Prefix", referencedColumnName = "id", updatable = false, insertable = false, foreignKey = @ForeignKey(name = "fk_ratePlanAssignTup"))
    private RatePlanAssignmentTuple ratePlanAssignmentTuple;

    @ManyToOne
    @JoinColumn(name = "Inactive", referencedColumnName = "id", updatable = false, insertable = false, foreignKey = @ForeignKey(name = "fk_rate_Plan"))
    private RatePlan ratePlan;
}
