//package freeswitch.service.configloader;
//
//import freeswitch.config.dynamic.core.DataLoader;
//import sharedentity.other.DynamicContext;
//import freeswitch.repository.mysqlrepository.PartnerRepository;
//import freeswitch.service.database.DynamicDatabaseService;
//import jakarta.persistence.EntityManager;
//import jakarta.persistence.PersistenceContext;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//
//@Service
//public class DynamicContextService {
//    @PersistenceContext
//    private EntityManager entityManager;
//    private final DataLoader dataLoader;
//    private final DynamicDatabaseService databaseService;
//
//    public DynamicContextService(DataLoader dataLoader, DynamicDatabaseService databaseService) {
//        this.dataLoader = dataLoader;
//        this.databaseService = databaseService;
//    }
//
//    public DynamicContext loadAll(List<String> resellerDbNames) {
//        DynamicContext rootContext = new DynamicContext(entityManager, databaseService, dataLoader);
//        rootContext.loadAll(resellerDbNames);
//        return rootContext;
//    }
//}