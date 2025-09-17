package freeswitch.repository.mysqlrepository;

import com.telcobright.rtc.domainmodel.mysqlentity.DialplanPrefix;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DialplanPrefixRepository extends JpaRepository<DialplanPrefix, Integer> {

    //List<DialplanPrefix> findByDialplanId(Integer id);

//    @Query("SELECT dp FROM DialplanPrefix dp WHERE dp.dialPlan.id = :id")
//    List<DialplanPrefix> findDialplanPrefixByDialplanId(@Param("id") Integer id);
}
