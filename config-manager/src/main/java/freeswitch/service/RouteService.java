package freeswitch.service;

import com.telcobright.rtc.domainmodel.mysqlentity.Route;
import freeswitch.repository.mysqlrepository.RouteRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RouteService {

    private final RouteRepository routeRepository;

    public RouteService(RouteRepository routeRepository) {
        this.routeRepository = routeRepository;
    }

    public List<Route> getRouteEntities() {
        return routeRepository.findAllRouteEntity();
    }
}
