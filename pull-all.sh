#!/bin/bash

# Script to pull latest changes for all submodules

echo "========================================="
echo "Pulling latest changes for all repositories"
echo "========================================="

# Pull parent repo first
echo ""
echo "📦 Pulling parent repository..."
if git pull origin $(git branch --show-current) 2>/dev/null; then
    echo "  ✅ Parent updated successfully"
else
    echo "  ⚠️  Could not pull parent (might not have remote)"
fi

# Update all submodules
echo ""
echo "📦 Updating all submodules..."

# Method 1: Update all submodules to their latest commits
git submodule update --remote --merge

# Alternative: Pull each submodule individually for more control
for submodule in statemachine infinite-scheduler partitioned-repo chronicle-db-Cache RTC-Manager; do
    if [ -d "$submodule/.git" ]; then
        echo ""
        echo "  Updating $submodule..."
        cd "$submodule"
        
        # Get current branch
        BRANCH=$(git branch --show-current)
        
        # Pull latest changes
        if git pull origin $BRANCH; then
            echo "    ✅ $submodule updated successfully"
        else
            echo "    ❌ Failed to update $submodule"
        fi
        
        cd ..
    fi
done

echo ""
echo "========================================="
echo "✅ Update complete!"
echo "========================================="
echo ""
echo "Tip: Run 'git status' to see if submodule references need to be committed"