package freeswitch.repository.mysqlrepository.sms;

import com.telcobright.rtc.domainmodel.mysqlentity.sms.SmsQueue;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SmsQueueRepository extends JpaRepository<SmsQueue, Integer> {
}
