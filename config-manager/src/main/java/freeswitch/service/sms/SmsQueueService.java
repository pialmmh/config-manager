package freeswitch.service.sms;

import com.telcobright.rtc.domainmodel.mysqlentity.sms.SmsQueue;
import freeswitch.repository.mysqlrepository.sms.SmsQueueRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SmsQueueService {
    private final SmsQueueRepository smsQueueRepository;

    public SmsQueueService(SmsQueueRepository smsQueueRepository) {
        this.smsQueueRepository = smsQueueRepository;
    }

    public List<SmsQueue> getTopicEntities() {
        return smsQueueRepository.findAll();
    }
}
