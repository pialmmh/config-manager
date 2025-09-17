package freeswitch.repository.mysqlrepository;

import com.telcobright.rtc.domainmodel.mysqlentity.Route;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RouteRepository extends JpaRepository<Route, Integer> {

    @Query("SELECT r FROM Route r " +
            " LEFT JOIN FETCH r.routeMetaData" )
    List<Route> findAllRouteEntity();
}
