# RouteSphere Git Submodules Guide

## What This Setup Gives You

With git submodules, you get exactly what you wanted:

### ✅ Push from Parent
- Push all changes (parent + all submodules) with one command
- Each submodule's changes go to its own repository
- Parent tracks which commit each submodule is at

### ✅ Push Individual Modules
- Work in any submodule independently
- Push changes directly from the submodule directory
- Other modules remain unaffected

### ✅ Clone/Pull Everything
- `git clone --recurse-submodules [parent-url]` gets everything
- All submodules come at their tracked versions
- One command to get the entire project

### ✅ Pull Individual Modules
- Update specific modules when needed
- Each module can be on different branches
- Independent version control

## Initial Setup (One Time)

```bash
# Run the setup script
./setup-submodules.sh

# After setup, commit and push parent
git commit -m "Initial setup with submodules"
git remote add origin [your-parent-repo-url]
git push -u origin main
```

## Daily Workflows

### 1. Starting Fresh (Clone Everything)
```bash
# Clone parent with all submodules
git clone --recurse-submodules git@github.com:yourorg/routesphere.git

# Or if already cloned without submodules
cd routesphere
git submodule update --init --recursive
```

### 2. Working on a Specific Module
```bash
# Navigate to the module
cd statemachine

# Make your changes
edit files...

# Commit and push
git add .
git commit -m "Fixed bug in state transitions"
git push origin main

# Go back to parent
cd ..

# Update parent to track new submodule commit
git add statemachine
git commit -m "Updated statemachine: fixed state transitions"
git push
```

### 3. Pushing Everything at Once
```bash
# After making changes in multiple modules
./push-all.sh "Implemented new features across modules"

# This will:
# - Push changes in each modified submodule
# - Update parent repo with new submodule references
# - Push parent repo
```

### 4. Pulling Latest Changes
```bash
# Get latest for everything
./pull-all.sh

# Or manually
git pull --recurse-submodules
git submodule update --remote --merge
```

### 5. Checking Status
```bash
# See status of all repos
./status-all.sh

# This shows:
# - Uncommitted changes
# - Unpushed commits
# - Branches
# - Whether you're behind origin
```

## Common Scenarios

### Scenario 1: Team Member Wants Latest Code
```bash
git clone --recurse-submodules git@github.com:yourorg/routesphere.git
cd routesphere
./update-libs.sh  # Build everything
```

### Scenario 2: You Changed Multiple Modules
```bash
# Work in statemachine
cd statemachine
# make changes
git add . && git commit -m "Update state logic"
cd ..

# Work in RTC-Manager
cd RTC-Manager/ConfigManager
# make changes
cd ../..
git add . && git commit -m "Update config"
cd ..

# Push everything
./push-all.sh "Updated state logic and config"
```

### Scenario 3: Update Only One Module
```bash
cd infinite-scheduler
git pull origin main
cd ..
git add infinite-scheduler
git commit -m "Updated infinite-scheduler to latest"
git push
```

### Scenario 4: Someone Else Updated Modules
```bash
# Get their changes
git pull
git submodule update --init --recursive

# Or use the script
./pull-all.sh
```

## Important Commands

### Submodule-Specific Commands
```bash
# See submodule status
git submodule status

# Update submodules to exact commits tracked by parent
git submodule update

# Update submodules to latest from their remotes
git submodule update --remote

# Clone with submodules
git clone --recurse-submodules [url]

# Pull with submodule updates
git pull --recurse-submodules
```

### Working with Branches in Submodules
```bash
cd statemachine
git checkout -b feature/new-feature
# make changes
git push origin feature/new-feature
cd ..
```

## Tips

1. **Always commit submodule changes first**, then update parent
2. **Use `./status-all.sh`** frequently to see what needs attention
3. **The parent repo tracks specific commits** of submodules, not branches
4. **Each submodule is a full git repo** - you can branch, tag, etc.
5. **If you see "modified" submodules** in parent, it means the submodule is at a different commit

## Troubleshooting

### Submodule shows as modified but no changes
```bash
# This happens when submodule HEAD differs from parent's tracked commit
git submodule update  # Reset to parent's tracked version
# OR
git add [submodule] && git commit -m "Update submodule reference"
```

### Can't push parent repo
```bash
# Make sure parent has a remote
git remote add origin [parent-repo-url]
git push -u origin main
```

### Submodule not downloading
```bash
git submodule update --init --recursive
```

### Want to remove local changes in submodule
```bash
cd [submodule]
git reset --hard HEAD
cd ..
```

## Architecture Benefits

This setup gives you:
- **Centralized entry point** (parent repo)
- **Decentralized development** (independent submodules)
- **Version locking** (parent tracks specific commits)
- **Flexible workflows** (work on all or individual modules)
- **Team friendly** (easy onboarding with one clone command)