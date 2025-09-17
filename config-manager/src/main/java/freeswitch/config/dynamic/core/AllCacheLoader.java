package freeswitch.config.dynamic.core;

import com.telcobright.rtc.domainmodel.mysqlentity.PackageAccount;
import com.telcobright.rtc.domainmodel.mysqlentity.PackagePurchase;
import freeswitch.repository.mysqlrepository.PackagePurchaseRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AllCacheLoader {
    private final PackagePurchaseRepository packagePurchaseRepository;
    private final Map<Long, List<PackageAccount>> partnerIdWisePackageAccounts = new HashMap<>();

    public AllCacheLoader(PackagePurchaseRepository packagePurchaseRepository) {
        this.packagePurchaseRepository = packagePurchaseRepository;
    }

    public Map<Long, List<PackageAccount>> loadPartnerIdWisePackageAccounts() {
        List<PackagePurchase> purchases = packagePurchaseRepository.findPkgPurchase();
        return buildPartnerAccountMap(purchases);
    }

    private Comparator<PackagePurchase> comparePurchases() {
        return (p1, p2) -> {
            // --- Compare onSelectPriority ---
            int prio1;
            if (p1.getOnSelectPriority() != null && p1.getOnSelectPriority() > 0) {
                prio1 = p1.getOnSelectPriority();
            } else {
                prio1 = Integer.MAX_VALUE;
            }

            int prio2;
            if (p2.getOnSelectPriority() != null && p2.getOnSelectPriority() > 0) {
                prio2 = p2.getOnSelectPriority();
            } else {
                prio2 = Integer.MAX_VALUE;
            }

            if (prio1 != prio2) {
                return Integer.compare(prio1, prio2);
            }

            // --- Compare expireDate ---
            if (!p1.getExpireDate().equals(p2.getExpireDate())) {
                return p1.getExpireDate().compareTo(p2.getExpireDate());
            }

            // --- Compare purchaseDate (descending) ---
            return p2.getPurchaseDate().compareTo(p1.getPurchaseDate());
        };
    }

    public Map<Long, List<PackageAccount>> buildPartnerAccountMap(List<PackagePurchase> purchases) {
        LocalDateTime now = LocalDateTime.now();
        Map<Long, List<PackageAccount>> partnerIdVsPackageAccountsMapCache = purchases.stream()
                .filter(p -> p.getExpireDate().isAfter(now))
                .sorted(comparePurchases())
                .flatMap(purchase -> purchase.getPackageAccounts().stream()
                        .map(account -> new AbstractMap.SimpleEntry<>(purchase.getIdPartner(), account)))
                .collect(Collectors.groupingBy(
                        Map.Entry::getKey,
                        LinkedHashMap::new,
                        Collectors.mapping(Map.Entry::getValue, Collectors.toList())
                ));
        return partnerIdVsPackageAccountsMapCache;
    }

    public Map<Long, List<PackageAccount>> getPartnerIdWisePackageAccounts() {
        return partnerIdWisePackageAccounts;
    }
}
