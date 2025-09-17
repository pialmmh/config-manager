package freeswitch.repository.mysqlrepository;

import com.telcobright.rtc.domainmodel.mysqlentity.Partner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PartnerRepository extends JpaRepository<Partner, Integer > {

    List<Partner> findByPartnerType(int i);

    @Query("SELECT p FROM Partner p " +
            "LEFT JOIN FETCH p.partnerPrefixes " +
            "LEFT JOIN FETCH p.routes " +
            "LEFT JOIN FETCH p.didAssignments"
    )
    List<Partner> findPartnerWithPrefixes();
}
