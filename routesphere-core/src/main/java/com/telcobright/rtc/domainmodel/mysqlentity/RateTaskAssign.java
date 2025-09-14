package com.telcobright.rtc.domainmodel.mysqlentity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Data
@Entity
@Table(name = "ratetaskassign")
public class RateTaskAssign {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "Prefix", foreignKey = @ForeignKey(name = "ratetaskassign_ibfk_1"))
    private RatePlanAssignmentTuple prefix;

    @Column(name = "description", length = 100)
    private String description;

    @Column(name = "rateamount", length = 100)
    private String rateAmount;

    @Column(name = "WeekDayStart", length = 100, nullable = false)
    private String weekDayStart = "1";

    @Column(name = "WeekDayEnd", length = 100, nullable = false)
    private String weekDayEnd = "7";

    @Column(name = "starttime", length = 100)
    private String startTime;

    @Column(name = "endtime", length = 100)
    private String endTime;

    @Column(name = "Resolution", length = 100, nullable = false)
    private String resolution = "1";

    @Column(name = "MinDurationSec", length = 100)
    private String minDurationSec;

    @Column(name = "SurchargeTime", length = 100, nullable = false)
    private String surchargeTime = "0";

    @Column(name = "SurchargeAmount", length = 100, nullable = false)
    private String surchargeAmount = "0.000000000";

    @Column(name = "idrateplan", nullable = false)
    private Long idRatePlan;

    @ManyToOne
    @JoinColumn(name = "CountryCode", foreignKey = @ForeignKey(name = "ratetaskassign_ibfk_3"))
    private Partner countryCode;

    @Column(name = "date1", length = 100)
    private String date1;

    @Column(name = "field1", length = 100)
    private String field1;

    @Column(name = "field2", length = 100)
    private String field2;

    @Column(name = "field3", length = 100)
    private String field3;

    @Column(name = "field4", length = 100)
    private String field4;

    @Column(name = "field5", length = 100)
    private String field5;

    @Column(name = "startdate", length = 100)
    private String startDate;

    @Column(name = "enddate", length = 100)
    private String endDate;

    @ManyToOne
    @JoinColumn(name = "Inactive", foreignKey = @ForeignKey(name = "ratetaskassign_ibfk_2"))
    private RatePlan inactive;

    @Column(name = "RouteDisabled", length = 100, nullable = false)
    private String routeDisabled = "0";

    @Column(name = "Type", length = 100)
    private String type;

    @Column(name = "Currency", length = 100)
    private String currency;

    @Column(name = "OtherAmount1", length = 100, nullable = false)
    private String otherAmount1 = "51";

    @Column(name = "OtherAmount2", length = 100, nullable = false)
    private String otherAmount2 = "15";

    @Column(name = "OtherAmount3", length = 100, nullable = false)
    private String otherAmount3 = "20";

    @Column(name = "OtherAmount4", length = 100)
    private String otherAmount4;

    @Column(name = "OtherAmount5", length = 100)
    private String otherAmount5;

    @Column(name = "OtherAmount6", length = 100, nullable = false)
    private String otherAmount6 = "40";

    @Column(name = "OtherAmount7", length = 100, nullable = false)
    private String otherAmount7 = "15";

    @Column(name = "OtherAmount8", length = 100, nullable = false)
    private String otherAmount8 = "15";

    @Column(name = "OtherAmount9", length = 100, nullable = false)
    private String otherAmount9 = "30";

    @Column(name = "OtherAmount10", length = 100, nullable = false)
    private String otherAmount10 = "65.75";

    @Column(name = "TimeZoneOffsetSec", length = 100)
    private String timeZoneOffsetSec;

    @Column(name = "RatePosition", length = 100)
    private String ratePosition;

    @Column(name = "IgwPercentageIn", length = 100)
    private String igwPercentageIn;

    @Column(name = "ConflictingRateIds", length = 100)
    private String conflictingRateIds;

    @Column(name = "ChangedByTaskId", length = 100)
    private String changedByTaskId;

    @Column(name = "ChangedOn", length = 100)
    private String changedOn;

    @Column(name = "Status", length = 100)
    private String status;

    @Column(name = "idPreviousRate", length = 100)
    private String idPreviousRate;

    @Column(name = "EndPreviousRate", length = 100)
    private String endPreviousRate;

    @Column(name = "Category", length = 100, nullable = false)
    private String category = "1";

    @Column(name = "SubCategory", length = 100, nullable = false)
    private String subCategory = "1";

    @Column(name = "changecommitted", nullable = false)
    private Integer changeCommitted = 0;

    @Column(name = "OverlappingRates", length = 50)
    private String overlappingRates;

    @Column(name = "ConflictingRates", length = 50)
    private String conflictingRates;

    @Column(name = "AffectedRates", length = 50)
    private String affectedRates;

    @Column(name = "PartitionDate", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date partitionDate;

    @Column(name = "Comment1", length = 200)
    private String comment1;

    @Column(name = "Comment2", length = 200)
    private String comment2;
}
