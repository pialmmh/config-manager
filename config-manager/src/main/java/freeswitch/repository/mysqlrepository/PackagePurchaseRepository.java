package freeswitch.repository.mysqlrepository;

import com.telcobright.rtc.domainmodel.mysqlentity.PackagePurchase;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PackagePurchaseRepository extends JpaRepository<PackagePurchase, Long> {

    @Query("Select pp from PackagePurchase pp " +
            "Left join fetch pp.packageAccounts pa " +
            "Left join fetch pp.pkg pkg " +
            "Left join fetch pkg.packageItems"
    )
    List<PackagePurchase> findAllEntities();
    @EntityGraph(attributePaths = "packageAccounts")
//    @Query("SELECT pp FROM PackagePurchase pp WHERE pp.expireDate > CURRENT_TIMESTAMP")
    @Query("SELECT pp FROM PackagePurchase pp")
    List<PackagePurchase> findPkgPurchase();
}

