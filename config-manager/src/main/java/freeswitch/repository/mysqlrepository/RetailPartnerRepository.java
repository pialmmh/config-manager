package freeswitch.repository.mysqlrepository;

import com.telcobright.rtc.domainmodel.mysqlentity.RetailPartner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RetailPartnerRepository extends JpaRepository<RetailPartner, Integer> {

    @Query("Select rp from RetailPartner rp " +
    "Left join fetch rp.partner")
    List<RetailPartner> findAllEntities();

    @Query("Select rp from RetailPartner rp")
    List<RetailPartner> findAllRetailPartners();
}
