package freeswitch.repository.mysqlrepository.sms;

import com.telcobright.rtc.domainmodel.mysqlentity.sms.Policy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PolicyRepository extends JpaRepository<Policy, Integer> {
}
