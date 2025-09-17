package freeswitch.service;

import com.telcobright.rtc.domainmodel.mysqlentity.Dialplan;
import freeswitch.repository.mysqlrepository.DialplanRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class DialplanService {
    private final DialplanRepository dialplanRepository;

    public DialplanService(DialplanRepository dialplanRepository) {
        this.dialplanRepository = dialplanRepository;
    }

    public HashMap<Integer, Dialplan> getDialplansMap() {
        return new HashMap<>(dialplanRepository.findAll().stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(
                        Dialplan::getId,
                        dialplan -> dialplan
                )));
    }


}
