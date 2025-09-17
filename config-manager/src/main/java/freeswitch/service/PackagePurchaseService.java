package freeswitch.service;

import com.telcobright.rtc.domainmodel.mysqlentity.PackagePurchase;
import freeswitch.repository.mysqlrepository.PackagePurchaseRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class PackagePurchaseService {
    private final PackagePurchaseRepository packagePurchaseRepo;

    public PackagePurchaseService(PackagePurchaseRepository packagePurchaseRepo) {
        this.packagePurchaseRepo = packagePurchaseRepo;
    }

    public HashMap<Long, Map<Long, PackagePurchase>> getIdVsPackagePurchaseMap() {
        HashMap<Long, Map<Long, PackagePurchase>> idPartnerVsPackageMap = new HashMap<>();

        packagePurchaseRepo.findAllEntities().forEach(packagePurchase -> {
            Long idPartner = packagePurchase.getIdPartner();
            idPartnerVsPackageMap
                    .computeIfAbsent(idPartner, k -> new HashMap<>())
                    .put(packagePurchase.getId(), packagePurchase);
        });
        return idPartnerVsPackageMap;
    }
}
