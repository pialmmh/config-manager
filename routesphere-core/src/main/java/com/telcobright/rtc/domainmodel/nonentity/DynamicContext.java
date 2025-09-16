package com.telcobright.rtc.domainmodel.nonentity;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.telcobright.rtc.domainmodel.mysqlentity.*;
import com.telcobright.rtc.domainmodel.mysqlentity.sms.Campaign;
import com.telcobright.rtc.domainmodel.mysqlentity.sms.SmsQueue;
import lombok.Getter;

import java.util.*;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonDeserialize(using = DynamicContextDeserializer.class)
public final class DynamicContext {

    // Immutable maps for various entity relationships
    @JsonProperty("callSourceMap")
    private final Map<Integer, CallSrc> callSourceMap;
    @JsonProperty("partners")
    private final Map<Integer, Partner> partners;
    @JsonProperty("prefixWisePartners")
    private final Map<String, Partner> prefixWisePartners;
    @JsonProperty("prefixWisePartnerPrefixes")
    private final Map<String, PartnerPrefix> prefixWisePartnerPrefixes;
    @JsonProperty("dppWiseDialplanMapping")
    private final Map<Integer, List<DialplanMapping>> dppWiseDialplanMapping;
    @JsonProperty("routeWisePartners")
    private final Map<String, Partner> routeWisePartners;
    @JsonProperty("callerIdWisePartners")
    private final Map<String, Partner> callerIdWisePartners;
    @JsonProperty("prefixVsPartners")
    private final Map<String, Partner> prefixVsPartners;
    @JsonProperty("sipAccountWisePartners")
    private final Map<String, Partner> sipAccountWisePartners;
    @JsonProperty("partnerWiseDidNumbers")
    private final Map<Integer, List<String>> partnerWiseDidNumbers;
    @JsonProperty("prefixWiseDialplanPrefixes")
    private final Map<String, DialplanPrefix> prefixWiseDialplanPrefixes;
    @JsonProperty("idVsDialplan")
    private final Map<Integer, Dialplan> idVsDialplan;
    @JsonProperty("rPartnerVsDidAssignments")
    private final Map<String, List<DidAssignment>> rPartnerVsDidAssignments;
    @JsonProperty("didNumbVsPartners")
    private final Map<String, Partner> didNumbVsPartners;
    @JsonProperty("didNumVsRpartners")
    private final Map<String, List<RetailPartner>> didNumVsRpartners;
    @JsonProperty("partnerWiseRatePlans")
    private final Map<String, List<RatePlan>> partnerWiseRatePlans;
    @JsonProperty("ratePlans")
    private final Map<Integer, RatePlan> ratePlans;
    @JsonProperty("ratePlanWiseTodaysRates")
    private final Map<Integer, Map<String, Rate>> ratePlanWiseTodaysRates;
    @JsonProperty("rateAssignsCustomer")
    private final List<RateAssign> rateAssignsCustomer;
    @JsonProperty("rateAssignsSupplier")
    private final List<RateAssign> rateAssignsSupplier;
    @JsonProperty("partnerVsRoutes")
    private final Map<Integer, List<Route>> partnerVsRoutes;

    //sms related
    @JsonProperty("campaigns")
    private final Map<Integer, Campaign> campaigns;
    @JsonProperty("smsQueue")
    private final Map<Integer, SmsQueue> smsQueue;
    @JsonProperty("enumJobStatus")
    private final Map<String, Integer> enumJobStatus;

    // Default constructor for empty context
    public DynamicContext() {
        this.callSourceMap = Collections.emptyMap();
        this.partners = Collections.emptyMap();
        this.prefixWisePartners = Collections.emptyMap();
        this.prefixWisePartnerPrefixes = Collections.emptyMap();
        this.dppWiseDialplanMapping = Collections.emptyMap();
        this.routeWisePartners = Collections.emptyMap();
        this.callerIdWisePartners = Collections.emptyMap();
        this.prefixVsPartners = Collections.emptyMap();
        this.sipAccountWisePartners = Collections.emptyMap();
        this.partnerWiseDidNumbers = Collections.emptyMap();
        this.prefixWiseDialplanPrefixes = Collections.emptyMap();
        this.idVsDialplan = Collections.emptyMap();
        this.rPartnerVsDidAssignments = Collections.emptyMap();
        this.didNumbVsPartners = Collections.emptyMap();
        this.didNumVsRpartners = Collections.emptyMap();
        this.partnerWiseRatePlans = Collections.emptyMap();
        this.ratePlans = Collections.emptyMap();
        this.ratePlanWiseTodaysRates = Collections.emptyMap();
        this.rateAssignsCustomer = Collections.emptyList();
        this.rateAssignsSupplier = Collections.emptyList();
        this.partnerVsRoutes = Collections.emptyMap();
        this.campaigns = Collections.emptyMap();
        this.smsQueue = Collections.emptyMap();
        this.enumJobStatus = Collections.emptyMap();
    }


    // Getters for commonly accessed fields
    public Map<Integer, Partner> getPartners() {
        return partners;
    }

    public Map<String, Partner> getRouteWisePartners() {
        return routeWisePartners;
    }

    public Map<String, Partner> getSipAccountWisePartners() {
        return sipAccountWisePartners;
    }

    public Map<String, List<DidAssignment>> getRPartnerVsDidAssignments() {
        return rPartnerVsDidAssignments;
    }

    /**
     * Catch-all setter that ignores any attempts to set properties during deserialization.
     * This allows Jackson to deserialize without errors while keeping the object immutable.
     */
    @JsonAnySetter
    public void setAny(String key, Object value) {
        // Intentionally do nothing - keep the object immutable
    }
}