package freeswitch.repository.mysqlrepository.sms;

import com.telcobright.rtc.domainmodel.mysqlentity.sms.TimeBand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TimeBandRepository extends JpaRepository<TimeBand, Long> {
    List<TimeBand> findByPolicyId(Integer policyId);
}
