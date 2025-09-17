package freeswitch.repository.mysqlrepository;


import com.telcobright.rtc.domainmodel.mysqlentity.DidNumber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DidNumberRepository extends JpaRepository<DidNumber, String> {
    // Custom query to find DID numbers by didPoolId
//    @Query("SELECT d FROM DidNumber d WHERE d.didPool.id = :didPoolId")
//    List<DidNumber> findByDidPoolId(@Param("didPoolId") Integer didPoolId);

    @Query("SELECT dn FROM DidNumber dn WHERE dn.id NOT IN (SELECT da.didNumberId FROM DidAssignment da)")
    List<DidNumber> findUnassignedDidNumbers();

    List<DidNumber> findByDidPoolId(Integer didPoolId);
}