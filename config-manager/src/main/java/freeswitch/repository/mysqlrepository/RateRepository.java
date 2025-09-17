package freeswitch.repository.mysqlrepository;

import com.telcobright.rtc.domainmodel.mysqlentity.Rate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface  RateRepository extends JpaRepository<Rate, String> {
    @Query("SELECT r FROM Rate r " +
            "WHERE r.startDate <= :currentTime " +
            "AND (r.endDate IS NULL OR r.endDate >= :currentTime) " +
            "AND r.idRatePlan IN :ratePlanIds")
    List<Rate> findRatePlansWithValidRatesByIds(@Param("ratePlanIds") List<Long> ratePlanIds, @Param("currentTime") LocalDateTime currentTime);
    @Query("SELECT r FROM Rate r " +
            "WHERE r.startDate <= :currentTime " +
            "AND (r.endDate IS NULL OR r.endDate >= :currentTime)")
    List<Rate> findRatePlansWithValidRates(@Param("currentTime") LocalDateTime currentTime);
}