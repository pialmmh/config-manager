package freeswitch.service.configloader;

import com.telcobright.rtc.domainmodel.mysqlentity.CallSrc;
import freeswitch.repository.mysqlrepository.CallSrcRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CallSrcService {
    private final CallSrcRepository callSrcRepository;

    public CallSrcService(CallSrcRepository callSrcRepository) {
        this.callSrcRepository = callSrcRepository;
    }


    @Transactional
    public List<CallSrc> getCallSrcEntities() {
        try{
            //return callSrcRepository.findAllWithDialPlanPrefix();
            return callSrcRepository.findAll(); // replace with actual query to fetch data from database with JOINs if needed.
        }catch (Exception e){
            System.out.println(e.getMessage());
            return null;
        }
    }
}
