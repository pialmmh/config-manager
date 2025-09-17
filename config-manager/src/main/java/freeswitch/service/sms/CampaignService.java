package freeswitch.service.sms;

import com.telcobright.rtc.domainmodel.mysqlentity.sms.Campaign;
import freeswitch.repository.mysqlrepository.sms.CampaignRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CampaignService {

    private final CampaignRepository campaignRepository;

    public CampaignService(CampaignRepository campaignRepository) {
        this.campaignRepository = campaignRepository;
    }

    public List<Campaign> getCampaignEntities() {

//        return new ArrayList<>();
        return campaignRepository.findAllWithNestedEntities();
    }
}
