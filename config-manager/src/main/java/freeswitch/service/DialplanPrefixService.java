package freeswitch.service;

import com.telcobright.rtc.domainmodel.mysqlentity.DialplanPrefix;
import freeswitch.repository.mysqlrepository.DialplanPrefixRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DialplanPrefixService {
    private final DialplanPrefixRepository dialplanPrefixRepository;

    public DialplanPrefixService(DialplanPrefixRepository dialplanPrefixRepository) {
        this.dialplanPrefixRepository = dialplanPrefixRepository;
    }

    @Transactional
    public List<DialplanPrefix> getDialplanPrefixEntities() {
        return dialplanPrefixRepository.findAll();
    }
}
