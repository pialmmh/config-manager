package com.telcobright.rtc.domainmodel.nonentity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.telcobright.rtc.domainmodel.mysqlentity.*;
import com.telcobright.rtc.domainmodel.mysqlentity.sms.Campaign;
import com.telcobright.rtc.domainmodel.mysqlentity.sms.SmsQueue;
import lombok.Getter;

import java.util.*;

@Getter
public final class DynamicContext {

    // All fields are final and immutable
    private final Map<Integer, CallSrc> callSourceMap;
    private final Map<Integer, Partner> partners;
    private final Map<String, Partner> prefixWisePartners;
    private final Map<String, PartnerPrefix> prefixWisePartnerPrefixes;
    private final Map<Integer, List<DialplanMapping>> dppWiseDialplanMapping;
    private final Map<String, Partner> routeWisePartners;
    private final Map<String, Partner> callerIdWisePartners;
    private final Map<String, Partner> prefixVsPartners;
    private final Map<String, Partner> sipAccountWisePartners;
    private final Map<Integer, List<String>> partnerWiseDidNumbers;
    private final Map<String, DialplanPrefix> prefixWiseDialplanPrefixes;
    private final Map<Integer, Dialplan> idVsDialplan;
    private final Map<String, List<DidAssignment>> rPartnerVsDidAssignments;
    private final Map<String, Partner> didNumbVsPartners;
    private final Map<String, List<RetailPartner>> didNumVsRpartners;
    private final Map<String, List<RatePlan>> partnerWiseRatePlans;
    private final Map<Integer, RatePlan> ratePlans;
    private final Map<Integer, Map<String, Rate>> ratePlanWiseTodaysRates;
    private final List<RateAssign> rateAssignsCustomer;
    private final List<RateAssign> rateAssignsSupplier;
    private final Map<Integer, List<Route>> partnerVsRoutes;

    //sms related
    private HashMap<Integer, Campaign> campaigns = new HashMap<>();
    private HashMap<Integer, SmsQueue> smsQueue = new HashMap<>();
    private HashMap<String, Integer> enumJobStatus = new HashMap<>();

    public DynamicContext() {
        // Initialize with empty collections for now
        this.partners = Collections.unmodifiableMap(new HashMap<>());
        this.callSourceMap = Collections.unmodifiableMap(new HashMap<>());
        this.partnerWiseDidNumbers = Collections.unmodifiableMap(new HashMap<>());
        this.dppWiseDialplanMapping = Collections.unmodifiableMap(new HashMap<>());
        this.prefixWiseDialplanPrefixes = Collections.unmodifiableMap(new HashMap<>());
        this.rPartnerVsDidAssignments = Collections.unmodifiableMap(new HashMap<>());

        // Build derived maps
        this.prefixWisePartners = Collections.unmodifiableMap(new HashMap<>());
        this.routeWisePartners = Collections.unmodifiableMap(new HashMap<>());
        this.callerIdWisePartners = Collections.unmodifiableMap(new HashMap<>());
        this.prefixVsPartners = Collections.unmodifiableMap(new HashMap<>());
        this.sipAccountWisePartners = Collections.unmodifiableMap(new HashMap<>());
        this.prefixWisePartnerPrefixes = Collections.unmodifiableMap(new HashMap<>());
        this.idVsDialplan = Collections.unmodifiableMap(new HashMap<>());
        this.rateAssignsCustomer = Collections.unmodifiableList(new ArrayList<>());
        this.rateAssignsSupplier = Collections.unmodifiableList(new ArrayList<>());
        this.partnerWiseRatePlans = Collections.unmodifiableMap(new HashMap<>());
        this.ratePlans = Collections.unmodifiableMap(new HashMap<>());
        this.ratePlanWiseTodaysRates = Collections.unmodifiableMap(new HashMap<>());
        this.didNumbVsPartners = Collections.unmodifiableMap(new HashMap<>());
        this.didNumVsRpartners = Collections.unmodifiableMap(new HashMap<>());
        this.partnerVsRoutes = Collections.unmodifiableMap(new HashMap<>());

        //sms
        this.campaigns = new HashMap<>();
        this.enumJobStatus = new HashMap<>();
        this.smsQueue = new HashMap<>();
    }
}