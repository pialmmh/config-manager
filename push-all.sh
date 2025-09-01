#!/bin/bash

# Script to push changes in all submodules and parent repo

echo "========================================="
echo "Pushing all changes (submodules + parent)"
echo "========================================="

# Default commit message
COMMIT_MSG="${1:-Update submodules and parent}"

# Track status
CHANGES_FOUND=false
FAILED_PUSHES=""

# Check and push each submodule
for submodule in statemachine infinite-scheduler partitioned-repo chronicle-db-Cache RTC-Manager; do
    if [ -d "$submodule/.git" ]; then
        echo ""
        echo "📦 Checking $submodule..."
        cd "$submodule"
        
        # Check if there are changes
        if ! git diff-index --quiet HEAD -- || [ -n "$(git ls-files --others --exclude-standard)" ]; then
            CHANGES_FOUND=true
            echo "  Changes found in $submodule"
            
            # Add all changes
            git add -A
            
            # Commit
            git commit -m "$COMMIT_MSG"
            
            # Push
            if git push origin $(git branch --show-current); then
                echo "  ✅ Pushed $submodule successfully"
            else
                echo "  ❌ Failed to push $submodule"
                FAILED_PUSHES="$FAILED_PUSHES $submodule"
            fi
        else
            # Check if there are unpushed commits
            if [ -n "$(git log origin/$(git branch --show-current)..HEAD 2>/dev/null)" ]; then
                echo "  Unpushed commits found in $submodule"
                if git push origin $(git branch --show-current); then
                    echo "  ✅ Pushed $submodule successfully"
                else
                    echo "  ❌ Failed to push $submodule"
                    FAILED_PUSHES="$FAILED_PUSHES $submodule"
                fi
            else
                echo "  No changes in $submodule"
            fi
        fi
        
        cd ..
    fi
done

# Now handle parent repo
echo ""
echo "📦 Checking parent repository..."

# Update submodule references in parent
git add -A

# Check if there are changes in parent
if ! git diff-index --quiet HEAD -- || [ -n "$(git ls-files --others --exclude-standard)" ]; then
    CHANGES_FOUND=true
    echo "  Changes found in parent"
    
    # Commit parent changes
    git commit -m "$COMMIT_MSG"
    
    # Push parent
    if git push origin $(git branch --show-current) 2>/dev/null; then
        echo "  ✅ Pushed parent successfully"
    else
        echo "  ⚠️  Parent repo might not have a remote set up yet"
        echo "  Run: git remote add origin [your-repo-url]"
    fi
else
    # Check for unpushed commits
    if [ -n "$(git log origin/$(git branch --show-current)..HEAD 2>/dev/null)" ]; then
        echo "  Unpushed commits found in parent"
        if git push origin $(git branch --show-current); then
            echo "  ✅ Pushed parent successfully"
        else
            echo "  ⚠️  Could not push parent"
        fi
    else
        echo "  No changes in parent"
    fi
fi

echo ""
echo "========================================="
if [ -n "$FAILED_PUSHES" ]; then
    echo "⚠️  Some pushes failed:$FAILED_PUSHES"
    echo "Please check these repositories manually"
else
    if [ "$CHANGES_FOUND" = true ]; then
        echo "✅ All changes pushed successfully!"
    else
        echo "ℹ️  No changes to push"
    fi
fi
echo "========================================="