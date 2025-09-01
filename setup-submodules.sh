#!/bin/bash

echo "========================================="
echo "Setting up RouteSphere with Git Submodules"
echo "========================================="

# First, backup existing directories
echo "Step 1: Creating backup of existing directories..."
for dir in statemachine infinite-scheduler partitioned-repo chronicle-db-Cache RTC-Manager; do
    if [ -d "$dir" ]; then
        echo "  Backing up $dir to ${dir}_backup"
        mv "$dir" "${dir}_backup"
    fi
done

# Initialize parent repo if not already
if [ ! -d .git ]; then
    echo ""
    echo "Step 2: Initializing parent repository..."
    git init
else
    echo ""
    echo "Step 2: Parent repository already initialized"
fi

# Create .gitignore for parent repo
echo ""
echo "Step 3: Creating .gitignore..."
cat > .gitignore << 'EOF'
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

# Temp files
*~
*.swp
EOF

# Add submodules
echo ""
echo "Step 4: Adding submodules..."

echo "  Adding statemachine..."
git submodule add git@github.com:pialmmh/statemachine.git statemachine

echo "  Adding infinite-scheduler..."
git submodule add git@github.com:pialmmh/infinite-scheduler.git infinite-scheduler

echo "  Adding partitioned-repo..."
git submodule add git@github.com:pialmmh/partitioned-repo.git partitioned-repo

echo "  Adding chronicle-db-Cache..."
git submodule add git@github.com:Bishal16/chronicle-db-Cache.git chronicle-db-Cache

echo "  Adding RTC-Manager..."
git submodule add git@github.com:telcobright/RTC-Manager.git RTC-Manager

# Initialize and update submodules
echo ""
echo "Step 5: Initializing and updating submodules..."
git submodule update --init --recursive

# Add parent files
echo ""
echo "Step 6: Adding parent repository files..."
git add pom.xml update-libs.sh LIBRARY_INTEGRATION.md .gitignore *.sh *.md

# Create README
echo ""
echo "Step 7: Creating README..."
cat > README.md << 'EOF'
# RouteSphere - Multi-Module Project

This is the parent repository for the RouteSphere project, managing multiple library modules as git submodules.

## Structure

- `statemachine/` - State machine library
- `infinite-scheduler/` - Infinite scheduler library  
- `partitioned-repo/` - Partitioned repository library
- `chronicle-db-Cache/` - Chronicle-based cache implementation
- `RTC-Manager/` - Spring Boot/Quarkus applications

## Initial Clone

When cloning this repository for the first time:

```bash
# Clone with submodules
git clone --recurse-submodules [parent-repo-url]

# Or if already cloned without submodules
git submodule update --init --recursive
```

## Working with Submodules

### Pull Latest Changes for All Submodules
```bash
git pull --recurse-submodules
# Or
git submodule update --remote --merge
```

### Pull Changes for Specific Submodule
```bash
cd statemachine
git pull origin main
cd ..
```

### Push Changes in Submodule
```bash
cd statemachine
git add .
git commit -m "Your changes"
git push origin main
cd ..

# Update parent repo to track new submodule commit
git add statemachine
git commit -m "Updated statemachine submodule"
git push
```

### Push All Changes (Parent + All Submodules)
```bash
# Use the provided script
./push-all.sh "Your commit message"
```

## Helper Scripts

- `update-libs.sh` - Build and install all libraries to local Maven repository
- `push-all.sh` - Push changes in all submodules and parent
- `pull-all.sh` - Pull latest changes for all submodules
- `status-all.sh` - Check git status of all submodules

## Building

```bash
# Build all modules
mvn clean install

# Or use the convenience script
./update-libs.sh
```
EOF

git add README.md

echo ""
echo "========================================="
echo "✅ Submodule setup complete!"
echo "========================================="
echo ""
echo "Next steps:"
echo "1. Review the changes: git status"
echo "2. Commit: git commit -m 'Initial setup with submodules'"
echo "3. Add remote: git remote add origin [your-parent-repo-url]"
echo "4. Push: git push -u origin main"
echo ""
echo "Note: Your original directories are backed up as *_backup"
echo "After verifying everything works, you can remove the backups."