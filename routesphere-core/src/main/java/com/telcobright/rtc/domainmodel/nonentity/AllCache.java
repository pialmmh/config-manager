package com.telcobright.rtc.domainmodel.nonentity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.telcobright.rtc.domainmodel.mysqlentity.PackageAccount;
import lombok.Getter;

import java.util.*;

@Getter
public final class AllCache {
    private final Map<Long, List<PackageAccount>> partnerIdWisePackageAccounts;

    public AllCache() {
        this.partnerIdWisePackageAccounts = Collections.unmodifiableMap(new HashMap<>());
    }
}