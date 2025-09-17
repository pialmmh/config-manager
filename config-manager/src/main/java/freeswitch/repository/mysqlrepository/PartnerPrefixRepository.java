package freeswitch.repository.mysqlrepository;

import com.telcobright.rtc.domainmodel.mysqlentity.PartnerPrefix;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PartnerPrefixRepository extends JpaRepository<PartnerPrefix, Integer> {
}
