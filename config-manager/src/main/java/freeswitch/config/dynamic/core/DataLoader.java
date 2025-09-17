package freeswitch.config.dynamic.core;

import com.telcobright.rtc.domainmodel.mysqlentity.*;
import com.telcobright.rtc.domainmodel.mysqlentity.sms.Campaign;
import com.telcobright.rtc.domainmodel.mysqlentity.sms.SmsQueue;
import com.telcobright.rtc.domainmodel.nonentity.DynamicContext;
import freeswitch.repository.mysqlrepository.RPartnerDidAssignmentRepo;
import freeswitch.repository.mysqlrepository.RetailPartnerRepository;
import freeswitch.service.*;
import freeswitch.service.configloader.CallSrcService;
import freeswitch.service.sms.CampaignService;
import freeswitch.service.sms.EnumJobStatusService;
import freeswitch.service.sms.TopicService;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component

public class DataLoader {

    private final RateService rateService;
    private HashMap<String, List<DataLoader>> dynamicConfigs;

    private final CallSrcService callSrcService;
    private final PartnerService partnerService;
    private final PartnerPrefixService partnerPrefixService;
    private final RetailPartnerRepository retailPartnerRepository;
    private final DidAssignmentService didAssignmentService;
    private final DialplanMappingService dialplanMappingService;
    private final DialplanPrefixService dialplanPrefixService;
    private final DialplanService dialplanService;
    private final RPartnerDidAssignmentRepo rPartnerDidAssignmentRepo;
    private final CampaignService campaignService;
    private final EnumJobStatusService enumJobStatusService;
    private final TopicService topicService;
    private final RateAssignService rateAssignService;
    private final RatePlanService ratePlanService;
    private final RouteService routeService;



    private HashMap<Integer, CallSrc> callSources = new HashMap<>();
    private HashMap<Integer, Partner> partners = new HashMap<>();
    private HashMap<Integer, Route> routes = new HashMap<>();
    private HashMap<String, Partner> prefixWisePartners = new HashMap<>();
    private HashMap<String, PartnerPrefix> prefixWisePartnerPrefixes = new HashMap<>();
    private HashMap<Integer, List<DialplanMapping>> dialplanPrefixWiseDialplanMappings = new HashMap<>();
    private HashMap<String, Partner> routeWisePartners = new HashMap<>();
    private HashMap<String, Partner> callerIdWisePartners = new HashMap<>();
    private HashMap<String, Partner> prefixVsPartners = new HashMap<>();
    private HashMap<String, Partner> sipAccountWisePartners = new HashMap<>();
    private HashMap<Integer, List<String>> partnerWiseDidNumbers = new HashMap<>();
    private HashMap<String, DialplanPrefix> prefixWiseDialplanPrefixes = new HashMap<>();
    private HashMap<Integer, Dialplan> idVsDialplan = new HashMap<>();
    private HashMap<String, List<DidAssignment>> rPartnerVsDidAssignments = new HashMap<>();
    private HashMap<String,List<RatePlan>> partnerWiseRatePlans =  new HashMap<>();;
    private HashMap<Integer, RatePlan> ratePlans = new HashMap<>();
    private HashMap<Integer, Map<String, Rate>> ratePlanWisetodaysRates = new HashMap<Integer, Map<String, Rate>>();
    private final PackagePurchaseService packagePurchaseService;

    //todo:sms-----------------------------------------


    public DataLoader(CallSrcService callSrcService,
                      PartnerService partnerService,
                      PartnerPrefixService partnerPrefixService,
                      RetailPartnerRepository retailPartnerRepository,
                      DidAssignmentService didAssignmentService,
                      DialplanMappingService dialplanMappingService,
                      DialplanPrefixService dialplanPrefixService,
                      DialplanService dialplanService,
                      RPartnerDidAssignmentRepo rPartnerDidAssignmentRepo,
                      CampaignService campaignService,
                      EnumJobStatusService enumJobStatusService,
                      TopicService topicService, RateAssignService rateAssignService, RatePlanService ratePlanService, PackagePurchaseService packagePurchaseService, RateService rateService, RouteService routeService) {
        this.callSrcService = callSrcService;
        this.partnerService = partnerService;
        this.partnerPrefixService = partnerPrefixService;
        this.retailPartnerRepository = retailPartnerRepository;
        this.didAssignmentService = didAssignmentService;
        this.dialplanMappingService = dialplanMappingService;
        this.dialplanPrefixService = dialplanPrefixService;
        this.dialplanService = dialplanService;
        this.rPartnerDidAssignmentRepo = rPartnerDidAssignmentRepo;
        this.campaignService = campaignService;
        this.enumJobStatusService = enumJobStatusService;
        this.topicService = topicService;
        this.rateAssignService = rateAssignService;
        this.ratePlanService = ratePlanService;
        this.packagePurchaseService = packagePurchaseService;
        this.rateService = rateService;
        this.routeService = routeService;
    }


    public HashMap<String, List<DidAssignment>> loadRPartnerVsDidAssignment(HashMap<String, List<DidAssignment>> newRPartnerVsDidAssignments) {
        rPartnerDidAssignmentRepo.findAll().forEach(mapping -> {
            newRPartnerVsDidAssignments
                    .computeIfAbsent(mapping.getRetailPartner().getUserName(), k -> new ArrayList<>())
                    .add(mapping.getDidAssignment());
        });
        return newRPartnerVsDidAssignments;
    }

    public HashMap<String, DialplanPrefix> loadPrefixVsDialplanPrefix(HashMap<String, DialplanPrefix> newPrefixWiseDialplanPrefixes) {
        dialplanPrefixService.getDialplanPrefixEntities().forEach(dialplanPrefix ->
                newPrefixWiseDialplanPrefixes.put(dialplanPrefix.getPrefix(), dialplanPrefix));
//        prefixWiseDialplanPrefixes = newPrefixWiseDialplanPrefixes;
        return newPrefixWiseDialplanPrefixes;
    }

    public HashMap<Integer, List<DialplanMapping>> loadDppVsDialplanMapping(HashMap<Integer, List<DialplanMapping>> newDppWiseDialplanMapping) {
        dialplanMappingService.getAllEntities().forEach(dialplanMapping -> {
            Integer dialplanPrefixId = dialplanMapping.getIdDialplanPrefix();
            newDppWiseDialplanMapping.computeIfAbsent(dialplanPrefixId, k -> new ArrayList<>()).add(dialplanMapping);
        });
//        dialplanPrefixWiseDialplanMappings = newDppWiseDialplanMapping;
        return newDppWiseDialplanMapping;
    }

    public HashMap<String, PartnerPrefix> loadPrefixVsPartnerPrefix(HashMap<String, PartnerPrefix> newPrefixWisePartnerPrefix) {
        partnerPrefixService.getPartnerPrefixEntities().forEach(partnerPrefix ->
                newPrefixWisePartnerPrefix.put(partnerPrefix.getPrefix(), partnerPrefix));
//        prefixWisePartnerPrefixes = newPrefixWisePartnerPrefix;
        return newPrefixWisePartnerPrefix;
    }

    public HashMap<Integer, List<String>> loadPartnerVsDidNumbers(HashMap<Integer, List<String>> newPartnerWiseDidNumbers) {
        didAssignmentService.getDidAssignmentEntities().forEach(didAssignment ->
                newPartnerWiseDidNumbers
                        .computeIfAbsent(didAssignment.getIdPartner(), k -> new ArrayList<>())
                        .add(didAssignment.getDidNumberId())
        );
//        partnerWiseDidNumbers = newPartnerWiseDidNumbers;
        return newPartnerWiseDidNumbers;
    }

    public HashMap<Integer, Partner> loadPartner() {
        return new HashMap<>( partnerService.getPartnerEntities().stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(
                        Partner::getIdPartner,
                        partner -> partner,
                        (existingPartner, newPartner) -> existingPartner
                )));
    }

    public HashMap<Integer, CallSrc> loadCallSrc() {
        HashMap<Integer, CallSrc> newCallSrcMap = new HashMap<>();
        callSrcService.getCallSrcEntities().forEach(callSrc -> {
            List<DialplanPrefix> sortedDialPlanPrefixes = callSrc.getDialPlanPrefixes()
                    .stream()
                    .sorted(Comparator.comparingInt((DialplanPrefix p) -> p.getPrefix().length()).reversed())
                    .toList();
            callSrc.setDialPlanPrefixes(sortedDialPlanPrefixes);
            newCallSrcMap.put(callSrc.getId(), callSrc);
        });
//        callSources = newCallSrcMap;
        return newCallSrcMap;
    }


    public HashMap<String, Partner> getRouteWisePartner() {
        for(Integer key : partners.keySet()) {
            Partner partner = partners.get(key);
            Set<Route> routes = partner.getRoutes();
            routes.forEach(route -> routeWisePartners.put(route.getRouteName(), partner));
        }
        return routeWisePartners;
    }
    public HashMap<String, Partner> getCallerIdWisePartner() {
        for(Integer key : partners.keySet()) {
            Partner partner = partners.get(key);
            Set<PartnerPrefix> partnerCallerIds = partner.getPartnerPrefixes();
            partnerCallerIds.forEach(partnerCallerId -> {
                if(partnerCallerId.getPrefixType().toString().equals("2"))
                    callerIdWisePartners.put(partnerCallerId.getPrefix(), partner);
            });
        }
        return callerIdWisePartners;
    }
    public HashMap<String, Partner> getPrefixWisePartner1() {
        for(Integer key : partners.keySet()) {
            Partner partner = partners.get(key);
            Set<PartnerPrefix> partnerCallerIds = partner.getPartnerPrefixes();
            partnerCallerIds.forEach(partnerCallerId -> {
                if(partnerCallerId.getPrefixType().toString().equals("1"))
                    prefixVsPartners.put(partnerCallerId.getPrefix(), partner);
            });
        }
        LinkedHashMap<String, Partner> prefixesSortedByLength = prefixVsPartners.entrySet()
                .stream()
                .sorted((entry1, entry2) -> Integer.compare(entry2.getKey().length(), entry1.getKey().length()))  // Compare key lengths in reverse order
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new  // Collect into LinkedHashMap to maintain order
                ));
        return prefixesSortedByLength;
    }

    public HashMap<String, Partner> getSipAccountWisePartner() {
        List<RetailPartner> retailPartners = retailPartnerRepository.findAll();
        sipAccountWisePartners = new HashMap<>();
        retailPartners.forEach(retailPartner ->
        {
            sipAccountWisePartners.put(retailPartner.getUserName(), retailPartner.getPartner());
        });

        return sipAccountWisePartners;
    }
    public List<RateAssign> getRateAssignsCustomer() {
        return rateAssignService.getRateAssignsCustomer();
    }
    public List<RateAssign> getRateAssignsSupplier() {
        return rateAssignService.getRateAssignsSupplier();
    }
    public HashMap<String, List<RatePlan>> getPartnerWiseRatePlans() {
        HashMap<String, List<RatePlan>> ratePlanByPartnerId = rateAssignService.getRateAssignsCustomer()
                .stream()
                .collect(Collectors.groupingBy(
                        RateAssign::getCountryCode,
                        HashMap::new,
                        Collectors.mapping(rateAssign -> {
                            RatePlan ratePlan = rateAssign.getRatePlan();
                            // Set the start and end dates from RateAssign to RatePlan
                            ratePlan.setStartDate(rateAssign.getStartDate());
                            ratePlan.setEndDate(rateAssign.getEndDate());
                            return ratePlan;
                        }, Collectors.toList())
                ));
        this.partnerWiseRatePlans = ratePlanByPartnerId;
        return ratePlanByPartnerId;
    }


    public HashMap<Integer, RatePlan> getRatePlans() {
        return ratePlanService.getRatePlans();
    }
    public HashMap<Integer, Map<String, Rate>> getRatePlanWiseTodaysRates() {
        List<Long> ratePlanIds = new ArrayList<>();
        this.partnerWiseRatePlans
                .forEach((key, value) -> {
                    value.forEach(ratePlan -> {
                        ratePlanIds.add(ratePlan.getId().longValue());
                    });
                });
        HashMap<Integer, Map<String, Rate>> todaysRatesByRatePlanId = rateService.getRatesByRatePlanId(ratePlanIds.stream().distinct().toList(),
                                                                                                LocalDateTime.now());
        this.ratePlanWisetodaysRates = todaysRatesByRatePlanId;
        return todaysRatesByRatePlanId;
    }


    //todo-------------------sms---------

    public HashMap<Integer, Campaign> getCampaigns() {
        HashMap<Integer, Campaign> campaignMap = new HashMap<>();
        campaignService.getCampaignEntities().forEach(campaign ->{
                    campaignMap.put(campaign.getId(), campaign);
                });
        return campaignMap;
    }
    public HashMap<String, Integer> getEnumJobStatus() {
        HashMap<String, Integer> enumJobStatusMap = new HashMap<>();
        enumJobStatusService.findAll().forEach(enumJobstatus ->{
            enumJobStatusMap.put(enumJobstatus.getType(), enumJobstatus.getId());
        });
        return enumJobStatusMap;
    }

    public HashMap<String, DialplanPrefix> getPrefixWiseDialplanPrefixes(){
        return prefixWiseDialplanPrefixes;
    }

    public HashMap<Integer, List<String>> getPartnerWiseDidNumbers(){
        return partnerWiseDidNumbers;
    }

    public HashMap<String, Partner> getPrefixWisePartners() {
        return prefixWisePartners;
    }

    public List<Route> getRoutes() {
        return routeService.getRouteEntities();
    }

    public HashMap<Integer, Dialplan> getIdVsDialplan() {
        return dialplanService.getDialplansMap();
    }

    public HashMap<String, List<DidAssignment>> getrPartnerVsDidAssignments() {
        return rPartnerVsDidAssignments;
    }
    public HashMap<Integer, List<DialplanMapping>> getDppWiseDialplanMapping(){
        return dialplanPrefixWiseDialplanMappings;
    }
    public HashMap<Integer, CallSrc> getCallSrcMap() {
        return callSources;
    }

    public HashMap<Integer, Partner> getPartnerMap() {
        return partners;
    }
    public HashMap<String, PartnerPrefix> getPrefixWisePartnerPrefixes() {return prefixWisePartnerPrefixes;}


    @PostConstruct
    public void dumpConfigsInKafka() throws Exception {

    }

    public List<String> getDidNumbers() {
        return didAssignmentService.getDidAssignmentEntities().stream()
                .map(DidAssignment::getDidNumberId) // Extracts the didNumberId from each DidAssignment
                .collect(Collectors.toList()); // Collects them into a List
    }

    public Map<Integer, SmsQueue> getSmsQueue() {
        return topicService.getTopicEntities()
                .stream()
                .collect(Collectors.toMap(SmsQueue::getId, smsQueue -> smsQueue));
    }


    public HashMap<Long, Map<Long, PackagePurchase>> getIdPartnerVsPackagePurchaseMap() {
        return packagePurchaseService.getIdVsPackagePurchaseMap();
    }

    public HashMap<Long, Map<Long, PackagePurchase>>  updateMap(HashMap<Long, Map<Long, PackagePurchase>>  oldMap) {

        HashMap<Long, Map<Long, PackagePurchase>>  newMap = getIdPartnerVsPackagePurchaseMap();
        if(oldMap==null) return newMap;

        newMap.forEach((idPartner, newMapPackagePurchases) -> {
            if(oldMap.containsKey(idPartner)) { // old entry update PackagePurchases of old partners

                Map<Long, PackagePurchase> oldMapPackagePurchases = oldMap.get(idPartner);

                newMapPackagePurchases.forEach((idPkgPurchase, packagePurchase) -> {
                    if (oldMapPackagePurchases.containsKey(idPkgPurchase)) {  // update status of old pkgPurchase
                        oldMapPackagePurchases.get(idPkgPurchase)
                                .setStatus(packagePurchase.getStatus());
                    }
                    else oldMapPackagePurchases.put(idPkgPurchase, packagePurchase); // new pkg purchase of old partners
                });

                oldMap.put(idPartner, oldMapPackagePurchases);
            }
            else oldMap.put(idPartner, newMap.get(idPartner)); // new entry(partner, purchase) in db

        });

        return oldMap;
}

    public HashMap<String, Partner> getDidVsPartner() {
        List<DidAssignment> listOfDidAssignment = didAssignmentService.getDidAssignments().getBody()
                .stream()
                .filter(didAssignment -> didAssignment.getIdRetailPartner()==null)
                .toList();
        return createDidVsPartnerMap(listOfDidAssignment,loadPartner());
    }
    private HashMap<String, Partner> createDidVsPartnerMap(List<DidAssignment>didAssignmentList, HashMap<Integer, Partner> partners) {
        return didAssignmentList.stream()
                .filter(didAssignment -> didAssignment.getIdPartner()!=null)
                .filter(didAssignment -> partners.containsKey(didAssignment.getIdPartner()))
                .collect(Collectors.toMap(
                        DidAssignment::getDidNumberId,
                        didAssignment -> partners.get(didAssignment.getIdPartner()),
                        (existing,replacement)->existing,
                        HashMap::new
                ));
    }
    public HashMap<String, List<RetailPartner>> getDidVsRetailPartners() {
        List<DidAssignment> listOfDidAssignment = didAssignmentService.getDidAssignments().getBody()
                .stream()
                .filter(didAssignment -> didAssignment.getIdRetailPartner()!=null)
                .toList();
        Map<Integer, RetailPartner> retailPartnerMap = retailPartnerRepository.findAllEntities()
                .stream()
                .collect(Collectors.toMap(
                        RetailPartner::getId,
                        Function.identity(),
                        (existing, replacement) -> existing
                ));

        HashMap<String, List<RetailPartner>> result = new HashMap<>();

        listOfDidAssignment.forEach(didAssignment -> {
            RetailPartner partner = retailPartnerMap.get(didAssignment.getIdRetailPartner());
            if (partner != null && didAssignment.getDidNumberId() != null) {
                result.computeIfAbsent(didAssignment.getDidNumberId(), k -> new ArrayList<>())
                        .add(partner);
            }
        });

        return result;
    }


    public HashMap<Integer, List<Route>> getPartnerVsRoutes() {
        List<Route> allRoutes = routeService.getRouteEntities();
        HashMap<Integer,List<Route>> partnerRoutesMap = new HashMap<>(allRoutes.stream()
                .collect(Collectors.groupingBy(Route::getIdPartner)));
        return partnerRoutesMap;
    }

    public List<RetailPartner> getRetailPartners() {
        return retailPartnerRepository.findAllRetailPartners();
    }

    /**
     * Load DynamicContext for a specific database
     */
    public DynamicContext loadDynamicContext(String databaseName) {
        // DynamicContext constructor will call the necessary load methods
        return new DynamicContext(this);
    }
}