package freeswitch.service;

import com.telcobright.rtc.domainmodel.mysqlentity.DialplanMapping;
import freeswitch.repository.mysqlrepository.DialplanMappingRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service

public class DialplanMappingService {
    private final DialplanMappingRepository dialplanMappingRepository;

    public DialplanMappingService(DialplanMappingRepository dialplanMappingRepository) {
        this.dialplanMappingRepository = dialplanMappingRepository;
    }

    public List<DialplanMapping> getAllEntities() {
        return dialplanMappingRepository.findAll();
    }
}
