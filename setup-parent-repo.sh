#!/bin/bash

echo "Setting up parent repository for RouteSphere..."

# Initialize git repo
git init

# Create comprehensive .gitignore
cat > .gitignore << 'EOF'
# Ignore all subdirectories (they have their own git repos)
statemachine/
infinite-scheduler/
partitioned-repo/
chronicle-db-Cache/
RTC-Manager/

# Maven
target/
*.class
*.jar
*.war
*.ear

# IDE
.idea/
*.iml
.vscode/
.settings/
.project
.classpath

# OS
.DS_Store
Thumbs.db

# Logs
*.log

# But ensure we track root-level files
!.gitignore
!pom.xml
!*.sh
!*.md
EOF

# Create a README
cat > README.md << 'EOF'
# RouteSphere - Parent Project

This repository contains the parent POM and build configuration for the RouteSphere multi-module project.

## Structure

Each subdirectory is an independent git repository:
- `statemachine/` - State machine library
- `infinite-scheduler/` - Scheduler library  
- `partitioned-repo/` - Partitioned repository library
- `chronicle-db-Cache/` - Chronicle-based cache
- `RTC-Manager/` - Spring Boot applications

## Building All Libraries

```bash
# Clone all library repositories first
git clone [statemachine-repo-url] statemachine
git clone [scheduler-repo-url] infinite-scheduler
# ... etc

# Build all libraries
./update-libs.sh

# Or use Maven directly
mvn clean install
```

## Note

This parent repository only tracks configuration files. Each subdirectory maintains its own git repository.
EOF

echo "Repository setup complete!"
echo ""
echo "Next steps:"
echo "1. Review .gitignore to ensure it matches your structure"
echo "2. git add ."
echo "3. git commit -m 'Initial parent repository setup'"
echo "4. git remote add origin [your-repo-url]"
echo "5. git push -u origin main"