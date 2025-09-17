package freeswitch.service;

import com.telcobright.rtc.domainmodel.mysqlentity.PartnerPrefix;
import freeswitch.repository.mysqlrepository.PartnerPrefixRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PartnerPrefixService extends GenericCrudService<PartnerPrefix, Integer> {
    private final PartnerPrefixRepository repo;
    public PartnerPrefixService(PartnerPrefixRepository repo) {
        super(repo);
        this.repo = repo;
    }


    public List<PartnerPrefix> getPartnerPrefixEntities() {
        return repo.findAll();
    }
}
