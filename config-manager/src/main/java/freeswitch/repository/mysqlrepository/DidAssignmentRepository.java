package freeswitch.repository.mysqlrepository;

import com.telcobright.rtc.domainmodel.mysqlentity.DidAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DidAssignmentRepository extends JpaRepository<DidAssignment, Integer> {


    Optional<DidAssignment> findByDidNumberId(String didNumberId);

//    DidAssignment[] findByDidPoolId(Integer id);
}
