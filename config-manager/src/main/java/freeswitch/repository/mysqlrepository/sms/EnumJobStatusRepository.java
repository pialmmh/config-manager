package freeswitch.repository.mysqlrepository.sms;


import com.telcobright.rtc.domainmodel.mysqlentity.sms.EnumJobStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EnumJobStatusRepository extends JpaRepository<EnumJobStatus, Integer> {
}
