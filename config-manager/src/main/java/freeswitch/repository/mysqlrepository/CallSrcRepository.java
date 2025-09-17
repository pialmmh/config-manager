package freeswitch.repository.mysqlrepository;

import com.telcobright.rtc.domainmodel.mysqlentity.CallSrc;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CallSrcRepository extends JpaRepository<CallSrc, Integer> {
//    @Query("SELECT c FROM CallSrc c " +
//            "LEFT JOIN FETCH c.dialplans d ")
//    List<CallSrc> findAllWithDialPlanPrefix();

}
