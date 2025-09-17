package freeswitch.service;


import com.telcobright.rtc.domainmodel.mysqlentity.RatePlan;
import freeswitch.repository.mysqlrepository.RatePlanRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RatePlanService {
    private final RatePlanRepository ratePlanRepository;

    public RatePlanService(RatePlanRepository ratePlanRepository) {
        this.ratePlanRepository = ratePlanRepository;
    }

    public HashMap<Integer, RatePlan> getRatePlans() {
        List<RatePlan> ratePlans = ratePlanRepository.findAllRatePlanEntities();

        return new HashMap<>(ratePlans.stream()
                .collect(Collectors.toMap(RatePlan::getId, ratePlan -> ratePlan)));
    }
}
