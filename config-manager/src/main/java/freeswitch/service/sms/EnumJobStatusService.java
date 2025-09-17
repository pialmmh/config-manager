package freeswitch.service.sms;

import com.telcobright.rtc.domainmodel.mysqlentity.sms.EnumJobStatus;
import freeswitch.repository.mysqlrepository.sms.EnumJobStatusRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EnumJobStatusService {
    private final EnumJobStatusRepository enumJobStatusRepository;

    public EnumJobStatusService(final EnumJobStatusRepository enumJobStatusRepository) {
        this.enumJobStatusRepository = enumJobStatusRepository;
    }

    public List<EnumJobStatus> findAll() {
        return enumJobStatusRepository.findAll();
    }
    public Optional<EnumJobStatus> findById(Integer id) {
        return enumJobStatusRepository.findById(id);
    }
}
