# Entity Sync Task for RouteSphere Core

## Overview
This Maven profile syncs domain model entities from ConfigManager (Spring Boot) to routesphere-core (Quarkus), automatically removing Spring-specific annotations and cleaning up incompatible dependencies.

## Location
**File:** `/home/mustafa/telcobright-projects/routesphere/routesphere-core/pom.xml`
**Profile:** `sync-entities` (lines 149-237)

## Usage

### From routesphere-core directory:
```bash
cd /home/mustafa/telcobright-projects/routesphere/routesphere-core
mvn clean validate -Psync-entities
```

### What it does:

1. **Syncs entities** from ConfigManager to routesphere-core
2. **Removes Spring annotations** (@Service, @Repository, @Autowired, etc.)
3. **Preserves important annotations**:
   - JPA/Jakarta: @Entity, @Table, @Column, @Id, etc.
   - Lombok: @Data, @Getter, @Setter, etc.
   - Jackson: @JsonIgnore, @JsonProperty, etc.
4. **Cleans up after sync**:
   - Removes nonentity folder (has ConfigManager dependencies)
   - Removes freeswitch.* imports
   - Removes sharedtypes.* imports

## Post-Sync Manual Cleanup

After running the sync, you may need to manually fix:

1. **Methods with undefined dependencies**: Some entity methods reference ConfigManager-specific classes like:
   - `DialplanRepository`
   - `DialplanPrefixRepository`
   - `PackageDto`
   - `PackageItemDTO`

2. **Quick fix command**:
```bash
# Remove problematic imports
cd /home/mustafa/telcobright-projects/routesphere/routesphere-core/src/main/java/com/telcobright/rtc/domainmodel/mysqlentity
sed -i '/import freeswitch\./d' *.java
sed -i '/import sharedtypes\./d' *.java

# Then manually remove or fix methods that use undefined classes
```

## Build After Sync

```bash
# Compile to verify everything works
mvn clean compile
```

## Configuration

The sync task is configured with:
- **Source**: `../RTC-Manager/ConfigManager/src/main/java/com/telcobright/rtc/domainmodel`
- **Destination**: `src/main/java/com/telcobright/rtc/domainmodel`
- **Converter**: `com.telcobright.util.db.conversion.SpringToQuarkusEntityConverter`

## Dependencies Required

routesphere-core must have these dependencies in pom.xml:
- `quarkus-hibernate-orm`
- `quarkus-hibernate-orm-panache`
- `jakarta.persistence-api`
- `lombok`
- `util` (the converter module)

## Troubleshooting

1. **If build fails after sync**: Check for ConfigManager-specific imports or methods
2. **Missing dependencies**: Ensure util module is installed: `cd ../util && mvn clean install`
3. **Compilation errors**: Usually caused by methods referencing ConfigManager classes - remove these methods