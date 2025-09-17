package freeswitch.service;

import com.telcobright.rtc.domainmodel.mysqlentity.Partner;
import freeswitch.repository.mysqlrepository.PartnerRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PartnerService extends GenericCrudService<Partner, Integer> {
    private final PartnerRepository partnerRepository;

    public PartnerService(PartnerRepository partnerRepository) {
        super(partnerRepository);
        this.partnerRepository = partnerRepository;
    }


    public List<Partner> getPartnerEntities() {
        return partnerRepository.findPartnerWithPrefixes();
    }
}
