package freeswitch.repository.mysqlrepository.sms;


import com.telcobright.rtc.domainmodel.mysqlentity.sms.RetryCauseCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RetryCauseCodeRepository extends JpaRepository<RetryCauseCode, Integer> {
    List<RetryCauseCode> findByPolicyId(int policyId);
    void deleteByPolicyId(int policyId);
}
