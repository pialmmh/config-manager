package freeswitch.config.dynamic.core;

import freeswitch.config.dynamic.ConfigManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


@Service
public class ConfigurationReloader {

    private final ConfigManager configManager;

    public ConfigurationReloader(ConfigManager configManager) {
        this.configManager = configManager;
    }

    @Scheduled(cron = "0 0 0 * * *")//reloads at 12:00am everyday
    public void reloadConfigurations() {
        System.out.println("🔥 [SCHEDULED] Reloading configurations at: " + LocalDateTime.now());
        configManager.loadConfigurations(); // call your actual reload method
    }
}
