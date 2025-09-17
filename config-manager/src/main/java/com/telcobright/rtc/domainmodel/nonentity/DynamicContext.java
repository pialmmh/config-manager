package com.telcobright.rtc.domainmodel.nonentity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.telcobright.rtc.domainmodel.mysqlentity.*;
import freeswitch.config.dynamic.core.DataLoader;
import com.telcobright.rtc.domainmodel.mysqlentity.sms.Campaign;
import com.telcobright.rtc.domainmodel.mysqlentity.sms.SmsQueue;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@Getter
public final class DynamicContext {

    @JsonIgnore
    private final DataLoader dataLoader;




    // All fields are final and immutable
    private final Map<Integer, CallSrc> callSourceMap;
    private final Map<Integer, Partner> partners;

//    private final Map<Integer, Route> routes;
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

    //todo----------sms
    private HashMap<Integer, Campaign> campaigns = new HashMap<>();
    private HashMap<Integer, SmsQueue> smsQueue = new HashMap<>();
    private HashMap<String, Integer> enumJobStatus = new HashMap<>();


    public DynamicContext(DataLoader dataLoader) {
        this.dataLoader = dataLoader;
        // Load all data during construction
        this.partners = Collections.unmodifiableMap(dataLoader.loadPartner());
        this.callSourceMap = Collections.unmodifiableMap(dataLoader.loadCallSrc());
        this.partnerWiseDidNumbers = Collections.unmodifiableMap(
                dataLoader.loadPartnerVsDidNumbers(new HashMap<>()));
        this.dppWiseDialplanMapping = Collections.unmodifiableMap(
                dataLoader.loadDppVsDialplanMapping(new HashMap<>()));
        this.prefixWiseDialplanPrefixes = Collections.unmodifiableMap(
                dataLoader.loadPrefixVsDialplanPrefix(new HashMap<>()));
        this.rPartnerVsDidAssignments = Collections.unmodifiableMap(
                dataLoader.loadRPartnerVsDidAssignment(new HashMap<>()));

        // Build derived maps
        this.prefixWisePartners = buildPrefixWisePartners(partners);
        this.routeWisePartners = buildRouteWisePartners(partners);
        this.callerIdWisePartners = Collections.unmodifiableMap(
                dataLoader.getCallerIdWisePartner());
        this.prefixVsPartners = Collections.unmodifiableMap(
                dataLoader.getPrefixWisePartner1());
        this.sipAccountWisePartners = Collections.unmodifiableMap(
                dataLoader.getSipAccountWisePartner());
        this.prefixWisePartnerPrefixes = Collections.unmodifiableMap(
                dataLoader.getPrefixWisePartnerPrefixes());
        this.idVsDialplan = Collections.unmodifiableMap(
                dataLoader.getIdVsDialplan());
        this.rateAssignsCustomer = Collections.unmodifiableList(
                dataLoader.getRateAssignsCustomer());
        this.rateAssignsSupplier = Collections.unmodifiableList(
                dataLoader.getRateAssignsSupplier());
        this.partnerWiseRatePlans = Collections.unmodifiableMap(
                dataLoader.getPartnerWiseRatePlans());
        this.ratePlans = Collections.unmodifiableMap(
                dataLoader.getRatePlans());
        this.ratePlanWiseTodaysRates = Collections.unmodifiableMap(
                dataLoader.getRatePlanWiseTodaysRates());
        this.didNumbVsPartners = Collections.unmodifiableMap(
                dataLoader.getDidVsPartner());
        this.didNumVsRpartners = Collections.unmodifiableMap(
                dataLoader.getDidVsRetailPartners());
        this.partnerVsRoutes = Collections.unmodifiableMap(
                dataLoader.getPartnerVsRoutes());

        //todo sms------------
        this.campaigns = new HashMap<>(dataLoader.getCampaigns());
        this.enumJobStatus = new HashMap<>(dataLoader.getEnumJobStatus());
        this.smsQueue = new HashMap<>(dataLoader.getSmsQueue());
    }

    private Map<String, Partner> buildPrefixWisePartners(Map<Integer, Partner> partners) {
        Map<String, Partner> map = new HashMap<>();
        for (Partner partner : partners.values()) {
            Set<PartnerPrefix> prefixes = partner.getPartnerPrefixes();
            if (prefixes != null) {
                prefixes.forEach(prefix -> map.put(prefix.getPrefix(), partner));
            }
        }
        return Collections.unmodifiableMap(map);
    }

    private Map<String, Partner> buildRouteWisePartners(Map<Integer, Partner> partners) {
        Map<String, Partner> map = new HashMap<>();
        for (Partner partner : partners.values()) {
            Set<Route> routes = partner.getRoutes();
            if (routes != null) {
                routes.forEach(route -> map.put(route.getField5(), partner));
            }
        }
        return Collections.unmodifiableMap(map);
    }
}