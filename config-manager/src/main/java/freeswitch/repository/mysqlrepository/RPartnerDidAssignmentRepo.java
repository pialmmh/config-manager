package freeswitch.repository.mysqlrepository;

import com.telcobright.rtc.domainmodel.mysqlentity.DidAssignmentRetailPartnerMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RPartnerDidAssignmentRepo extends JpaRepository<DidAssignmentRetailPartnerMapping, Integer> {
}
