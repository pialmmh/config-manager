package com.telcobright.rtc.domainmodel.nonentity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import freeswitch.config.dynamic.core.AllCacheLoader;
import com.telcobright.rtc.domainmodel.mysqlentity.PackageAccount;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@Getter
public final class AllCache {
    private final Map<Long, List<PackageAccount>> partnerIdWisePackageAccounts;
    @JsonIgnore
    private final AllCacheLoader cacheLoader;

    public AllCache(AllCacheLoader cacheLoader) {
        this.cacheLoader = cacheLoader;
        this.partnerIdWisePackageAccounts = Collections.unmodifiableMap(
                cacheLoader.loadPartnerIdWisePackageAccounts()
        );
    }
}
