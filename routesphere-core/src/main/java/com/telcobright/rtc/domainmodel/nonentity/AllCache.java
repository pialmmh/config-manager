package com.telcobright.rtc.domainmodel.nonentity;

import com.telcobright.rtc.domainmodel.mysqlentity.PackageAccount;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

@Data
@NoArgsConstructor
public class AllCache {
    private Map<Long, List<PackageAccount>> partnerIdWisePackageAccounts = new HashMap<>();
}