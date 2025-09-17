package freeswitch.service;

import com.telcobright.rtc.domainmodel.mysqlentity.Rate;
import freeswitch.repository.mysqlrepository.RateRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RateService {
    private final RateRepository rateRepository;

    public RateService(RateRepository rateRepository) {
        this.rateRepository = rateRepository;
    }
    public HashMap<Integer, Map<String, Rate>> getRatesByRatePlanId(List<Long> ratePlanIds, LocalDateTime datetime) {
        List<Rate> Rates = rateRepository.findRatePlansWithValidRatesByIds(ratePlanIds, datetime);
        return new HashMap<>(Rates.stream()
                .collect(Collectors.groupingBy(
                        Rate::getIdRatePlan,
                        Collectors.toMap(
                                Rate::getPrefix,    // key: prefix
                                rate -> rate //,       // value: the Rate itself
//                                (existing, replacement) -> existing // handle duplicate prefixes
                        )
                )));
    }
}