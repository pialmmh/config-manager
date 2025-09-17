package freeswitch.repository.mysqlrepository.sms;

import com.telcobright.rtc.domainmodel.mysqlentity.sms.RetryInterval;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RetryIntervalRepository extends JpaRepository<RetryInterval, Long> {
    List<RetryInterval> findByPolicyId(int policyId);
    void deleteByPolicyId(int policyId);
    Optional<RetryInterval> findByPolicyIdAndRetryCount(int policyId, int retryCount);
}
