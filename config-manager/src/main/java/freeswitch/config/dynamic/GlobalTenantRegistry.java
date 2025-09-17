package freeswitch.config.dynamic;

import com.telcobright.rtc.domainmodel.mysqlentity.Partner;
import lombok.Data;
import org.springframework.stereotype.Component;
import com.telcobright.rtc.domainmodel.nonentity.DynamicContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Data
@Component
public class GlobalTenantRegistry {
    private final Map<Integer, String> partnerIds = new ConcurrentHashMap<>();
    private final Map<String, Integer> partnerNames = new ConcurrentHashMap<>();
    private final Map<String, Integer> routeIps = new ConcurrentHashMap<>();
    private final Map<String, Integer> retailPartnerUsernames = new ConcurrentHashMap<>();
    private final Map<String, Partner> sipAccountWisePartners = new ConcurrentHashMap<>();
    private final Map<String, Partner> routeWisePartners = new ConcurrentHashMap<>();

    public void setSipAccountWisePartners(DynamicContext context){
        sipAccountWisePartners.putAll(context.getSipAccountWisePartners());
    }
    public void setRouteWisePartners(DynamicContext context){
        routeWisePartners.putAll(context.getRouteWisePartners());
    }
}
