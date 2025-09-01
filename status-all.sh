#!/bin/bash

# Script to check git status of all submodules and parent

echo "========================================="
echo "Git Status for All Repositories"
echo "========================================="

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Check parent repo
echo ""
echo "📦 PARENT REPOSITORY"
echo "-------------------"
if [ -d .git ]; then
    # Check for uncommitted changes
    if ! git diff-index --quiet HEAD -- 2>/dev/null || [ -n "$(git ls-files --others --exclude-standard)" ]; then
        echo -e "${YELLOW}⚠️  Uncommitted changes${NC}"
        git status --short
    else
        echo -e "${GREEN}✅ Clean${NC}"
    fi
    
    # Check for unpushed commits
    BRANCH=$(git branch --show-current)
    if [ -n "$(git log origin/$BRANCH..HEAD 2>/dev/null)" ]; then
        UNPUSHED=$(git log origin/$BRANCH..HEAD --oneline 2>/dev/null | wc -l)
        echo -e "${YELLOW}📤 $UNPUSHED unpushed commit(s)${NC}"
    fi
else
    echo -e "${RED}❌ Not a git repository${NC}"
fi

# Check each submodule
for submodule in statemachine infinite-scheduler partitioned-repo chronicle-db-Cache RTC-Manager; do
    echo ""
    echo "📦 $submodule"
    echo "-------------------"
    
    if [ -d "$submodule/.git" ]; then
        cd "$submodule"
        
        # Current branch
        BRANCH=$(git branch --show-current)
        echo "Branch: $BRANCH"
        
        # Check for uncommitted changes
        if ! git diff-index --quiet HEAD -- 2>/dev/null || [ -n "$(git ls-files --others --exclude-standard)" ]; then
            echo -e "${YELLOW}⚠️  Uncommitted changes${NC}"
            git status --short
        else
            echo -e "${GREEN}✅ Clean${NC}"
        fi
        
        # Check for unpushed commits
        if [ -n "$(git log origin/$BRANCH..HEAD 2>/dev/null)" ]; then
            UNPUSHED=$(git log origin/$BRANCH..HEAD --oneline 2>/dev/null | wc -l)
            echo -e "${YELLOW}📤 $UNPUSHED unpushed commit(s)${NC}"
            git log origin/$BRANCH..HEAD --oneline 2>/dev/null | head -3
        fi
        
        # Check if behind remote
        if [ -n "$(git log HEAD..origin/$BRANCH 2>/dev/null)" ]; then
            BEHIND=$(git log HEAD..origin/$BRANCH --oneline 2>/dev/null | wc -l)
            echo -e "${YELLOW}📥 $BEHIND commit(s) behind origin/$BRANCH${NC}"
        fi
        
        cd ..
    else
        echo -e "${RED}❌ Not found or not a git repository${NC}"
    fi
done

echo ""
echo "========================================="
echo "Summary complete!"
echo "========================================="