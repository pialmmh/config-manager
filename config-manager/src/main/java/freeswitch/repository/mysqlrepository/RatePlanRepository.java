package freeswitch.repository.mysqlrepository;

import com.telcobright.rtc.domainmodel.mysqlentity.RatePlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RatePlanRepository extends JpaRepository<RatePlan, String> {
    @Query("Select rp from RatePlan rp")
    List<RatePlan> findAllRatePlanEntities();
}
