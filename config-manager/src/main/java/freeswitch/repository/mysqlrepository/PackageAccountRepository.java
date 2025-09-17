package freeswitch.repository.mysqlrepository;

import com.telcobright.rtc.domainmodel.mysqlentity.PackageAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PackageAccountRepository extends JpaRepository<PackageAccount, Long> {
    @Query(value = """
            SELECT * FROM (
                packageaccount
                Left JOIN packagepurchase
                ON packageaccount.id_PackagePurchase = packagepurchase.id)\s
                 
              left join packageitem
                on packageitem.id_package = packagepurchase.id_package and packageitem.id_UOM = packageaccount.uom
              where id_partner = :partnerId
                  and status = 'ACTIVE'
                  and expireDate > Now()              
              order by category, expireDate, priority desc ;
            """, nativeQuery = true)
    List<Object[]> findPurchasedPackages(@Param("partnerId") int partnerId);



    @Query("select r from PackageAccount r where r.idpackagePurchase = :purchaseId")
    List<PackageAccount> findByPurchaseId(@Param("purchaseId") Long purchaseId);
}
