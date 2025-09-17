package freeswitch.service;

import com.telcobright.rtc.domainmodel.mysqlentity.RateAssign;
import freeswitch.repository.mysqlrepository.RateAssignRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RateAssignService {

    private final RateAssignRepository rateAssignRepository;

    public RateAssignService(RateAssignRepository rateAssignRepository) {
        this.rateAssignRepository = rateAssignRepository;
    }

    public List<RateAssign> getRateAssignsCustomer() {
        return rateAssignRepository.getRateAssignEntities(LocalDateTime.now(), 1);
    }
    public List<RateAssign> getRateAssignsSupplier() {
        return rateAssignRepository.getRateAssignEntities(LocalDateTime.now(), 0);
    }

}
