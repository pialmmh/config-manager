package freeswitch.repository.mysqlrepository;

import com.telcobright.rtc.domainmodel.mysqlentity.RateAssign;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RateAssignRepository extends JpaRepository<RateAssign, String> {
    @Query("SELECT r FROM RateAssign r " +
            "LEFT JOIN r.ratePlanAssignmentTuple rt " +
            "WHERE r.startDate <= :currentTime " +
            "AND (r.endDate IS NULL OR r.endDate >= :currentTime) " +
            "AND rt.idService = 10 " +
            "AND rt.assignDirection = :direction " +
            "ORDER BY rt.priority")
    List<RateAssign> getRateAssignEntities(@Param("currentTime") LocalDateTime currentTime,
                                           @Param("direction") Integer serviceAssignmentDirection);
}
