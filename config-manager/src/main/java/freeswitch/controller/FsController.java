package freeswitch.controller;


import freeswitch.config.dynamic.ConfigManager;
import freeswitch.config.dynamic.GlobalTenantRegistry;
import com.telcobright.rtc.domainmodel.nonentity.Tenant;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FsController {

    private final ConfigManager configManager;
    private final GlobalTenantRegistry registry;

    public FsController(ConfigManager configManager, GlobalTenantRegistry registry) {
        this.configManager = configManager;
        this.registry = registry;
    }

    @PostMapping("/get-tenant-root")
    public Tenant getEslContext(){
        return configManager.getRootTenant();
    }

    @PostMapping("/get-global-tenant-registry")
    public ResponseEntity<GlobalTenantRegistry> getRegistry(){
        return new ResponseEntity<>(registry, HttpStatus.OK);
    }

}
