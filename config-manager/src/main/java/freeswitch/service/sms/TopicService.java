package freeswitch.service.sms;

import com.telcobright.rtc.domainmodel.mysqlentity.sms.SmsQueue;
import freeswitch.repository.mysqlrepository.sms.SmsQueueRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TopicService {
    private final SmsQueueRepository topicRepository;

    public TopicService(SmsQueueRepository topicRepository) {
        this.topicRepository = topicRepository;
    }

    public List<SmsQueue> getTopicEntities() {
        return topicRepository.findAll();
    }
}
