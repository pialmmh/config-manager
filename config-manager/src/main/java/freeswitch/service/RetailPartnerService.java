package freeswitch.service;

import com.telcobright.rtc.domainmodel.mysqlentity.RetailPartner;
import freeswitch.repository.mysqlrepository.RetailPartnerRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RetailPartnerService {
    private final RetailPartnerRepository retailPartnerRepository;

    public RetailPartnerService(RetailPartnerRepository retailPartnerRepository) {
        this.retailPartnerRepository = retailPartnerRepository;
    }

    public HashMap<Integer, List<RetailPartner>> getHashMap() {
        List<RetailPartner> retailPartners = retailPartnerRepository.findAllEntities();
        Map<Integer, List<RetailPartner>> idPartnerVsRetailPartners =  retailPartners.stream()
                .collect(Collectors.groupingBy(rp -> rp.getPartner().getIdPartner()));
        return (HashMap<Integer, List<RetailPartner>>) idPartnerVsRetailPartners;


    }
}
