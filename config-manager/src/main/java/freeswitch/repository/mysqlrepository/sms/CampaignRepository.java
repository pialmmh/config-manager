package freeswitch.repository.mysqlrepository.sms;


import com.telcobright.rtc.domainmodel.mysqlentity.sms.Campaign;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface CampaignRepository extends JpaRepository<Campaign, Integer> {

    @Query("SELECT c FROM Campaign c " +
            "LEFT JOIN FETCH c.policy p " +
            "LEFT JOIN FETCH p.timeBands " +
            "LEFT JOIN FETCH p.retryIntervals " +
            "LEFT JOIN FETCH p.retryCauseCodes where c.status = 6 "
    )
    List<Campaign> findAllWithNestedEntities();

////    @Query("SELECT c FROM Campaign c " +
////            "WHERE "+
////            "c.schedules is not null AND c.expireAt IS NOT NULL"+
////            "CAST(c.schedules AS long) <= :currentTimestamp " +
////            "AND CAST(c.expireAt AS long) > :currentTimestamp " +
////            "AND c.scheduleStatus = 'enabled' ")
////    Page<Campaign> getActiveCampaigns(@Param("currentTimestamp") long currentTimestamp, Pageable pageable);
//
//    @Query(value = "SELECT * FROM campaign c " +
//            "WHERE schedules IS NOT NULL " +
//            "AND expire_at IS NOT NULL " +
//            "AND CAST(schedules AS UNSIGNED) <= :currentTimestamp " +
//            "AND CAST(expire_at AS UNSIGNED) > :currentTimestamp " +
//            "AND schedule_status = 'enabled'",
//            nativeQuery = true)
//    Page<Campaign> getActiveCampaigns(@Param("currentTimestamp") long currentTimestamp, Pageable pageable);
}
