package com.telcobright.routesphere.startup;

import com.telcobright.routesphere.tenant.Tenant;
import com.telcobright.routesphere.tenant.TenantHierarchy;
import io.quarkus.runtime.Startup;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Quarkus startup class to initialize tenant hierarchy
 * This runs at application startup and populates the tenant structure
 */
@ApplicationScoped
@Startup
public class TenantHierarchyInitializer {
    
    @Inject
    TenantHierarchyService tenantHierarchyService;
    
    private final AtomicInteger tenantCounter = new AtomicInteger(0);
    
    /**
     * Initialize tenant hierarchy on application startup
     * @param event Quarkus startup event
     */
    void onStart(@Observes StartupEvent event) {
        System.out.println("\n========================================");
        System.out.println(" Initializing Tenant Hierarchy");
        System.out.println("========================================\n");
        
        TenantHierarchy hierarchy = initializeTenantHierarchy();
        tenantHierarchyService.setTenantHierarchy(hierarchy);
        
        System.out.println("\n========================================");
        System.out.println(" Tenant Hierarchy Initialized Successfully");
        System.out.println(" Total Tenants: " + hierarchy.getTenantCount());
        System.out.println("========================================\n");
        
        // Print the hierarchy
        hierarchy.printHierarchy();
    }
    
    /**
     * Custom logic to populate tenant hierarchy
     * In production, this would load from database or configuration
     */
    private TenantHierarchy initializeTenantHierarchy() {
        TenantHierarchy hierarchy = new TenantHierarchy();
        
        // Create ROOT tenant
        Tenant rootTenant = createRootTenant();
        hierarchy.addTenant(rootTenant);
        
        // Create Level 1 Resellers (Major Partners)
        List<Tenant> level1Resellers = createLevel1Resellers(rootTenant.getTenantId());
        level1Resellers.forEach(hierarchy::addTenant);
        
        // Create Level 2 Resellers (Regional Partners)
        for (Tenant l1Reseller : level1Resellers) {
            List<Tenant> level2Resellers = createLevel2Resellers(l1Reseller);
            level2Resellers.forEach(hierarchy::addTenant);
            
            // Create Level 3 Resellers (Local Partners)
            for (Tenant l2Reseller : level2Resellers) {
                List<Tenant> level3Resellers = createLevel3Resellers(l2Reseller);
                level3Resellers.forEach(hierarchy::addTenant);
                
                // Create End Users for Level 3 resellers
                for (Tenant l3Reseller : level3Resellers) {
                    List<Tenant> endUsers = createEndUsers(l3Reseller, 5);
                    endUsers.forEach(hierarchy::addTenant);
                }
            }
            
            // Some Level 2 resellers have direct end users
            if (Math.random() > 0.5) {
                List<Tenant> directEndUsers = createEndUsers(level2Resellers.get(0), 3);
                directEndUsers.forEach(hierarchy::addTenant);
            }
        }
        
        // Some Level 1 resellers have direct end users (enterprise customers)
        Tenant enterpriseReseller = level1Resellers.get(0);
        List<Tenant> enterpriseUsers = createEnterpriseUsers(enterpriseReseller);
        enterpriseUsers.forEach(hierarchy::addTenant);
        
        return hierarchy;
    }
    
    /**
     * Create root tenant
     */
    private Tenant createRootTenant() {
        Tenant root = new Tenant("root", "RouteSphere Master", Tenant.TenantLevel.ROOT);
        root.setStatus(Tenant.TenantStatus.ACTIVE);
        
        // Add root tenant properties
        root.addProperty("max_resellers", "1000");
        root.addProperty("max_end_users", "100000");
        root.addProperty("global_rate_limit", "10000");
        root.addProperty("api_version", "v2");
        
        System.out.println("Created ROOT tenant: " + root.getTenantName());
        return root;
    }
    
    /**
     * Create Level 1 Resellers (Major Partners)
     */
    private List<Tenant> createLevel1Resellers(String parentId) {
        List<Tenant> resellers = new ArrayList<>();
        
        // Premium Partner - Full features
        Tenant premiumPartner = new Tenant(
            "reseller_premium_" + tenantCounter.incrementAndGet(),
            "Premium Communications Partner",
            Tenant.TenantLevel.RESELLER_L1
        );
        premiumPartner.setParentTenantId(parentId);
        premiumPartner.setStatus(Tenant.TenantStatus.ACTIVE);
        premiumPartner.addProperty("tier", "PREMIUM");
        premiumPartner.addProperty("max_sub_resellers", "50");
        premiumPartner.addProperty("max_end_users", "10000");
        premiumPartner.addProperty("features", "FULL_SUITE");
        premiumPartner.addProperty("sla_level", "GOLD");
        resellers.add(premiumPartner);
        
        // Standard Partner - Standard features
        Tenant standardPartner = new Tenant(
            "reseller_standard_" + tenantCounter.incrementAndGet(),
            "Standard Telecom Partner",
            Tenant.TenantLevel.RESELLER_L1
        );
        standardPartner.setParentTenantId(parentId);
        standardPartner.setStatus(Tenant.TenantStatus.ACTIVE);
        standardPartner.addProperty("tier", "STANDARD");
        standardPartner.addProperty("max_sub_resellers", "20");
        standardPartner.addProperty("max_end_users", "5000");
        standardPartner.addProperty("features", "STANDARD");
        standardPartner.addProperty("sla_level", "SILVER");
        resellers.add(standardPartner);
        
        // Basic Partner - Limited features
        Tenant basicPartner = new Tenant(
            "reseller_basic_" + tenantCounter.incrementAndGet(),
            "Basic Services Partner",
            Tenant.TenantLevel.RESELLER_L1
        );
        basicPartner.setParentTenantId(parentId);
        basicPartner.setStatus(Tenant.TenantStatus.ACTIVE);
        basicPartner.addProperty("tier", "BASIC");
        basicPartner.addProperty("max_sub_resellers", "5");
        basicPartner.addProperty("max_end_users", "1000");
        basicPartner.addProperty("features", "BASIC");
        basicPartner.addProperty("sla_level", "BRONZE");
        resellers.add(basicPartner);
        
        System.out.println("Created " + resellers.size() + " Level 1 Resellers");
        return resellers;
    }
    
    /**
     * Create Level 2 Resellers (Regional Partners)
     */
    private List<Tenant> createLevel2Resellers(Tenant parent) {
        List<Tenant> resellers = new ArrayList<>();
        
        String[] regions = {"North", "South", "East", "West"};
        int numResellers = parent.getProperties().get("tier").equals("PREMIUM") ? 4 : 2;
        
        for (int i = 0; i < numResellers && i < regions.length; i++) {
            Tenant regionalPartner = new Tenant(
                "reseller_l2_" + regions[i].toLowerCase() + "_" + tenantCounter.incrementAndGet(),
                regions[i] + " Regional Partner",
                Tenant.TenantLevel.RESELLER_L2
            );
            regionalPartner.setParentTenantId(parent.getTenantId());
            regionalPartner.setStatus(Tenant.TenantStatus.ACTIVE);
            regionalPartner.addProperty("region", regions[i]);
            regionalPartner.addProperty("max_sub_resellers", "10");
            regionalPartner.addProperty("max_end_users", "1000");
            regionalPartner.addProperty("coverage_area", regions[i] + "_REGION");
            resellers.add(regionalPartner);
        }
        
        System.out.println("Created " + resellers.size() + " Level 2 Resellers under " + parent.getTenantName());
        return resellers;
    }
    
    /**
     * Create Level 3 Resellers (Local Partners)
     */
    private List<Tenant> createLevel3Resellers(Tenant parent) {
        List<Tenant> resellers = new ArrayList<>();
        
        String region = parent.getProperties().get("region");
        String[] cities = getCitiesForRegion(region);
        
        for (int i = 0; i < Math.min(3, cities.length); i++) {
            Tenant localPartner = new Tenant(
                "reseller_l3_" + cities[i].toLowerCase().replace(" ", "_") + "_" + tenantCounter.incrementAndGet(),
                cities[i] + " Local Partner",
                Tenant.TenantLevel.RESELLER_L3
            );
            localPartner.setParentTenantId(parent.getTenantId());
            localPartner.setStatus(Tenant.TenantStatus.ACTIVE);
            localPartner.addProperty("city", cities[i]);
            localPartner.addProperty("max_end_users", "500");
            localPartner.addProperty("service_type", "LOCAL_SERVICES");
            localPartner.addProperty("support_hours", "BUSINESS_HOURS");
            resellers.add(localPartner);
        }
        
        System.out.println("Created " + resellers.size() + " Level 3 Resellers under " + parent.getTenantName());
        return resellers;
    }
    
    /**
     * Create End Users
     */
    private List<Tenant> createEndUsers(Tenant parent, int count) {
        List<Tenant> users = new ArrayList<>();
        
        for (int i = 1; i <= count; i++) {
            String userId = "user_" + parent.getTenantId() + "_" + tenantCounter.incrementAndGet();
            String companyName = generateCompanyName(i);
            
            Tenant endUser = new Tenant(
                userId,
                companyName,
                Tenant.TenantLevel.END_USER
            );
            endUser.setParentTenantId(parent.getTenantId());
            endUser.setStatus(i % 10 == 0 ? Tenant.TenantStatus.SUSPENDED : Tenant.TenantStatus.ACTIVE);
            
            // Add user properties
            endUser.addProperty("company_size", getCompanySize(i));
            endUser.addProperty("industry", getIndustry(i));
            endUser.addProperty("max_concurrent_calls", String.valueOf(10 + i * 5));
            endUser.addProperty("max_channels", String.valueOf(5 + i * 2));
            endUser.addProperty("billing_type", i % 2 == 0 ? "POSTPAID" : "PREPAID");
            endUser.addProperty("credit_limit", String.valueOf(1000 * i));
            
            users.add(endUser);
        }
        
        System.out.println("Created " + users.size() + " End Users under " + parent.getTenantName());
        return users;
    }
    
    /**
     * Create Enterprise Users (Direct under L1)
     */
    private List<Tenant> createEnterpriseUsers(Tenant parent) {
        List<Tenant> users = new ArrayList<>();
        
        String[] enterprises = {
            "Global Bank Corp",
            "International Airlines",
            "Mega Retail Chain",
            "Healthcare Network",
            "Technology Solutions Inc"
        };
        
        for (String enterprise : enterprises) {
            String userId = "enterprise_" + tenantCounter.incrementAndGet();
            
            Tenant enterpriseUser = new Tenant(
                userId,
                enterprise,
                Tenant.TenantLevel.END_USER
            );
            enterpriseUser.setParentTenantId(parent.getTenantId());
            enterpriseUser.setStatus(Tenant.TenantStatus.ACTIVE);
            
            // Enterprise properties
            enterpriseUser.addProperty("type", "ENTERPRISE");
            enterpriseUser.addProperty("company_size", "ENTERPRISE");
            enterpriseUser.addProperty("max_concurrent_calls", "1000");
            enterpriseUser.addProperty("max_channels", "500");
            enterpriseUser.addProperty("billing_type", "CONTRACT");
            enterpriseUser.addProperty("sla_level", "PLATINUM");
            enterpriseUser.addProperty("dedicated_support", "true");
            enterpriseUser.addProperty("custom_features", "true");
            
            users.add(enterpriseUser);
        }
        
        System.out.println("Created " + users.size() + " Enterprise Users under " + parent.getTenantName());
        return users;
    }
    
    /**
     * Helper: Get cities for region
     */
    private String[] getCitiesForRegion(String region) {
        Map<String, String[]> regionCities = new HashMap<>();
        regionCities.put("North", new String[]{"New York", "Boston", "Chicago", "Detroit"});
        regionCities.put("South", new String[]{"Miami", "Atlanta", "Houston", "Dallas"});
        regionCities.put("East", new String[]{"Philadelphia", "Washington DC", "Baltimore"});
        regionCities.put("West", new String[]{"Los Angeles", "San Francisco", "Seattle", "Phoenix"});
        
        return regionCities.getOrDefault(region, new String[]{"City A", "City B", "City C"});
    }
    
    /**
     * Helper: Generate company name
     */
    private String generateCompanyName(int index) {
        String[] prefixes = {"Tech", "Global", "Premier", "Advanced", "Digital", "Smart", "Next", "Future"};
        String[] suffixes = {"Solutions", "Systems", "Services", "Group", "Corp", "Industries", "Enterprises"};
        
        return prefixes[index % prefixes.length] + " " + suffixes[index % suffixes.length] + " " + index;
    }
    
    /**
     * Helper: Get company size
     */
    private String getCompanySize(int index) {
        if (index <= 2) return "SMALL";
        if (index <= 5) return "MEDIUM";
        if (index <= 8) return "LARGE";
        return "ENTERPRISE";
    }
    
    /**
     * Helper: Get industry
     */
    private String getIndustry(int index) {
        String[] industries = {
            "TECHNOLOGY", "FINANCE", "HEALTHCARE", "RETAIL",
            "MANUFACTURING", "EDUCATION", "HOSPITALITY", "LOGISTICS"
        };
        return industries[index % industries.length];
    }
}