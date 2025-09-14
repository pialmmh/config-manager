#!/bin/bash

# ============================================
# Claude Bridge - Complete Setup Script
# ============================================
# This script sets up the Claude-React bridge automatically
# Usage: ./setup-claude-bridge.sh [path-to-react-app]

set -e  # Exit on error

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Get React app path from argument or use current directory
REACT_APP_PATH="${1:-.}"
REACT_APP_PATH=$(cd "$REACT_APP_PATH" && pwd)

echo -e "${BLUE}================================================${NC}"
echo -e "${BLUE}     Claude Bridge - Automatic Setup Script     ${NC}"
echo -e "${BLUE}================================================${NC}\n"

# Check if we're in a React app
if [ ! -f "$REACT_APP_PATH/package.json" ]; then
    echo -e "${RED}❌ Error: No package.json found in $REACT_APP_PATH${NC}"
    echo -e "${YELLOW}Please run this script in your React app directory or provide the path as an argument${NC}"
    echo -e "${YELLOW}Usage: ./setup-claude-bridge.sh /path/to/your/react-app${NC}"
    exit 1
fi

echo -e "${GREEN}✓ Found React app at: $REACT_APP_PATH${NC}\n"

# Step 1: Create bridge server directory
echo -e "${YELLOW}Step 1: Creating bridge server directory...${NC}"
BRIDGE_PATH="$REACT_APP_PATH/claude-bridge"
mkdir -p "$BRIDGE_PATH"
cd "$BRIDGE_PATH"
echo -e "${GREEN}✓ Created $BRIDGE_PATH${NC}\n"

# Step 2: Initialize npm and install dependencies
echo -e "${YELLOW}Step 2: Installing bridge server dependencies...${NC}"
cat > package.json << 'EOF'
{
  "name": "claude-bridge",
  "version": "1.0.0",
  "description": "Bridge server for Claude Code and React app communication",
  "main": "bridge-server.js",
  "scripts": {
    "start": "node bridge-server.js",
    "dev": "node bridge-server.js"
  },
  "dependencies": {
    "express": "^4.18.2",
    "cors": "^2.8.5"
  }
}
EOF

npm install --silent
echo -e "${GREEN}✓ Dependencies installed${NC}\n"

# Step 3: Create the bridge server
echo -e "${YELLOW}Step 3: Creating bridge server...${NC}"
cat > bridge-server.js << 'EOF'
const express = require('express');
const cors = require('cors');
const fs = require('fs').promises;
const path = require('path');

const app = express();
app.use(cors());
app.use(express.json({ limit: '10mb' }));

// Store for app states
let currentState = {};
let stateHistory = [];
const MAX_HISTORY = 50;

// Endpoint for React app to send state
app.post('/api/state', (req, res) => {
  currentState = {
    ...req.body,
    timestamp: new Date().toISOString(),
    id: Date.now()
  };
  
  stateHistory.unshift(currentState);
  if (stateHistory.length > MAX_HISTORY) {
    stateHistory = stateHistory.slice(0, MAX_HISTORY);
  }
  
  const components = Object.keys(req.body.components || {});
  const errors = (req.body.errors || []).length;
  const time = new Date().toLocaleTimeString();
  
  console.log(`📥 [${time}] State received - Components: ${components.length}, Errors: ${errors}`);
  
  res.json({ success: true, id: currentState.id });
});

// Endpoints for Claude Code
app.get('/api/state', (req, res) => res.json(currentState));
app.get('/api/history', (req, res) => res.json(stateHistory));
app.get('/api/components', (req, res) => res.json(currentState.components || {}));
app.get('/api/console', (req, res) => res.json(currentState.console || []));
app.get('/api/storage', (req, res) => res.json(currentState.storage || {}));
app.get('/api/network', (req, res) => res.json(currentState.network || []));
app.get('/api/errors', (req, res) => res.json(currentState.errors || []));

// Save state to file
app.post('/api/save', async (req, res) => {
  const filename = `state-${Date.now()}.json`;
  await fs.writeFile(filename, JSON.stringify(currentState, null, 2));
  console.log(`💾 State saved to ${filename}`);
  res.json({ filename });
});

// Health check
app.get('/health', (req, res) => {
  res.json({ 
    status: 'running',
    hasState: Object.keys(currentState).length > 0,
    historyCount: stateHistory.length,
    uptime: process.uptime()
  });
});

const PORT = process.env.PORT || 9999;
app.listen(PORT, () => {
  console.log(`
🌉 Claude Bridge Server Running
================================
Server URL: http://localhost:${PORT}
Health Check: http://localhost:${PORT}/health

Endpoints for Claude Code:
--------------------------
GET  http://localhost:${PORT}/api/state      - Full current state
GET  http://localhost:${PORT}/api/components - React components
GET  http://localhost:${PORT}/api/console    - Console logs
GET  http://localhost:${PORT}/api/errors     - Errors
GET  http://localhost:${PORT}/api/network    - Network requests
GET  http://localhost:${PORT}/api/storage    - Browser storage
GET  http://localhost:${PORT}/api/history    - State history
POST http://localhost:${PORT}/api/save       - Save state to file

Waiting for React app to connect...
  `);
});
EOF
echo -e "${GREEN}✓ Bridge server created${NC}\n"

# Step 4: Create the React hook
echo -e "${YELLOW}Step 4: Creating React hook...${NC}"
HOOKS_PATH="$REACT_APP_PATH/src/hooks"
mkdir -p "$HOOKS_PATH"

cat > "$HOOKS_PATH/useClaudeBridge.js" << 'EOF'
import { useEffect, useRef } from 'react';

export const useClaudeBridge = (enabled = true) => {
  const consoleRef = useRef([]);
  const networkRef = useRef([]);
  const errorsRef = useRef([]);
  const bridgeStatusRef = useRef('disconnected');

  useEffect(() => {
    if (!enabled) return;

    console.log('🌉 Claude Bridge: Initializing...');

    // Store original console methods
    const originalLog = console.log;
    const originalError = console.error;
    const originalWarn = console.warn;
    const originalFetch = window.fetch;
    
    // Capture console
    console.log = (...args) => {
      consoleRef.current.push({
        type: 'log',
        message: args.map(arg => 
          typeof arg === 'object' ? JSON.stringify(arg) : String(arg)
        ).join(' '),
        timestamp: new Date().toISOString()
      });
      if (consoleRef.current.length > 100) {
        consoleRef.current = consoleRef.current.slice(-100);
      }
      originalLog(...args);
    };
    
    console.error = (...args) => {
      const entry = {
        type: 'error',
        message: args.map(arg => 
          typeof arg === 'object' ? JSON.stringify(arg) : String(arg)
        ).join(' '),
        timestamp: new Date().toISOString()
      };
      consoleRef.current.push(entry);
      errorsRef.current.push(entry);
      if (errorsRef.current.length > 50) {
        errorsRef.current = errorsRef.current.slice(-50);
      }
      originalError(...args);
    };

    console.warn = (...args) => {
      consoleRef.current.push({
        type: 'warn',
        message: args.map(arg => 
          typeof arg === 'object' ? JSON.stringify(arg) : String(arg)
        ).join(' '),
        timestamp: new Date().toISOString()
      });
      originalWarn(...args);
    };

    // Capture network
    window.fetch = async (...args) => {
      const start = Date.now();
      const url = typeof args[0] === 'string' ? args[0] : args[0].url;
      const options = args[1] || {};
      
      if (url.includes('localhost:9999')) {
        return originalFetch(...args);
      }
      
      try {
        const response = await originalFetch(...args);
        networkRef.current.push({
          url,
          method: options.method || 'GET',
          status: response.status,
          duration: Date.now() - start,
          timestamp: new Date().toISOString()
        });
        if (networkRef.current.length > 100) {
          networkRef.current = networkRef.current.slice(-100);
        }
        return response;
      } catch (error) {
        networkRef.current.push({
          url,
          method: options.method || 'GET',
          error: error.message,
          duration: Date.now() - start,
          timestamp: new Date().toISOString()
        });
        throw error;
      }
    };

    // Collect React components
    const collectComponents = () => {
      const components = {};
      const processedFibers = new WeakSet();
      
      document.querySelectorAll('*').forEach(element => {
        const keys = Object.keys(element);
        const fiberKey = keys.find(key => 
          key.startsWith('__reactInternalInstance$') || 
          key.startsWith('__reactFiber$')
        );
        
        if (fiberKey && element[fiberKey]) {
          const fiber = element[fiberKey];
          if (fiber && !processedFibers.has(fiber)) {
            processedFibers.add(fiber);
            
            if (fiber.elementType && typeof fiber.elementType === 'function') {
              const name = fiber.elementType.name || 'Anonymous';
              if (!components[name]) {
                components[name] = [];
              }
              components[name].push({
                props: fiber.memoizedProps || {},
                state: fiber.memoizedState,
                key: fiber.key
              });
            }
          }
        }
      });
      
      return components;
    };

    // Collect storage
    const collectStorage = () => {
      const storage = {
        localStorage: {},
        sessionStorage: {},
        cookies: {}
      };
      
      try {
        for (let i = 0; i < localStorage.length; i++) {
          const key = localStorage.key(i);
          storage.localStorage[key] = localStorage.getItem(key);
        }
        for (let i = 0; i < sessionStorage.length; i++) {
          const key = sessionStorage.key(i);
          storage.sessionStorage[key] = sessionStorage.getItem(key);
        }
        document.cookie.split(';').forEach(cookie => {
          const [key, value] = cookie.trim().split('=');
          if (key) storage.cookies[key] = value;
        });
      } catch (e) {}
      
      return storage;
    };

    // Send state to bridge
    const sendState = async () => {
      try {
        const state = {
          url: window.location.href,
          title: document.title,
          components: collectComponents(),
          storage: collectStorage(),
          console: consoleRef.current,
          network: networkRef.current,
          errors: errorsRef.current,
          timestamp: new Date().toISOString()
        };

        const response = await originalFetch('http://localhost:9999/api/state', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(state)
        });

        if (response.ok && bridgeStatusRef.current !== 'connected') {
          bridgeStatusRef.current = 'connected';
          console.log('🌉 Claude Bridge: Connected to server');
        }
      } catch (error) {
        if (bridgeStatusRef.current !== 'disconnected') {
          bridgeStatusRef.current = 'disconnected';
          console.warn('🌉 Claude Bridge: Server not reachable. Make sure bridge server is running.');
        }
      }
    };

    // Send state periodically
    setTimeout(sendState, 1000);
    const interval = setInterval(sendState, 2000);

    // Cleanup
    return () => {
      clearInterval(interval);
      console.log = originalLog;
      console.error = originalError;
      console.warn = originalWarn;
      window.fetch = originalFetch;
      console.log('🌉 Claude Bridge: Disconnected');
    };
  }, [enabled]);

  return { isConnected: bridgeStatusRef.current === 'connected' };
};
EOF
echo -e "${GREEN}✓ React hook created${NC}\n"

# Step 5: Check if App.js exists and prepare integration code
echo -e "${YELLOW}Step 5: Preparing App.js integration...${NC}"
APP_FILE=""
if [ -f "$REACT_APP_PATH/src/App.js" ]; then
    APP_FILE="$REACT_APP_PATH/src/App.js"
elif [ -f "$REACT_APP_PATH/src/App.jsx" ]; then
    APP_FILE="$REACT_APP_PATH/src/App.jsx"
elif [ -f "$REACT_APP_PATH/src/App.tsx" ]; then
    APP_FILE="$REACT_APP_PATH/src/App.tsx"
fi

if [ -n "$APP_FILE" ]; then
    echo -e "${GREEN}✓ Found App file: $APP_FILE${NC}"
    
    # Create a backup
    cp "$APP_FILE" "$APP_FILE.backup.$(date +%Y%m%d_%H%M%S)"
    echo -e "${GREEN}✓ Created backup of App file${NC}"
    
    # Check if hook is already imported
    if grep -q "useClaudeBridge" "$APP_FILE"; then
        echo -e "${YELLOW}⚠ Hook already appears to be integrated${NC}"
    else
        echo -e "${YELLOW}📝 Add these lines to your App file:${NC}\n"
        echo -e "${BLUE}// Add this import at the top:${NC}"
        echo -e "import { useClaudeBridge } from './hooks/useClaudeBridge';\n"
        echo -e "${BLUE}// Add this line inside your App component:${NC}"
        echo -e "useClaudeBridge(process.env.NODE_ENV === 'development');\n"
    fi
else
    echo -e "${YELLOW}⚠ Could not find App.js/jsx/tsx${NC}"
    echo -e "${YELLOW}Please manually add the hook to your main App component${NC}"
fi

# Step 6: Create start scripts
echo -e "${YELLOW}Step 6: Creating start scripts...${NC}"

# Create combined start script
cat > "$REACT_APP_PATH/start-with-bridge.sh" << 'EOF'
#!/bin/bash

# Colors
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

echo -e "${GREEN}Starting Claude Bridge and React App...${NC}\n"

# Function to kill background processes on exit
cleanup() {
    echo -e "\n${YELLOW}Shutting down...${NC}"
    kill $BRIDGE_PID $APP_PID 2>/dev/null
    exit
}
trap cleanup EXIT INT TERM

# Start bridge server
echo -e "${GREEN}Starting bridge server...${NC}"
cd claude-bridge && npm start &
BRIDGE_PID=$!

# Wait for bridge to start
sleep 2

# Start React app
echo -e "${GREEN}Starting React app...${NC}"
npm start &
APP_PID=$!

echo -e "\n${GREEN}================================================${NC}"
echo -e "${GREEN}  Both servers are running!${NC}"
echo -e "${GREEN}  Bridge: http://localhost:9999${NC}"
echo -e "${GREEN}  React:  http://localhost:3000${NC}"
echo -e "${GREEN}  Press Ctrl+C to stop both servers${NC}"
echo -e "${GREEN}================================================${NC}\n"

# Keep script running
wait
EOF

chmod +x "$REACT_APP_PATH/start-with-bridge.sh"
echo -e "${GREEN}✓ Created start-with-bridge.sh${NC}\n"

# Create helper commands script
cat > "$REACT_APP_PATH/claude-bridge-commands.txt" << 'EOF'
================================================
Claude Bridge - Example Commands for Claude Code
================================================

1. Check current state:
   "Get the React app state from http://localhost:9999/api/state"

2. Debug components:
   "Check http://localhost:9999/api/components and tell me what components are rendered"
   "Why is MenuBar not in the component tree at http://localhost:9999/api/components?"

3. Analyze errors:
   "Look at http://localhost:9999/api/errors and help me fix them"
   "Check the console at http://localhost:9999/api/console for any warnings"

4. Check network:
   "Analyze the API calls at http://localhost:9999/api/network"
   "Are there any failed requests in http://localhost:9999/api/network?"

5. Inspect storage:
   "Check localStorage at http://localhost:9999/api/storage"
   "Why isn't user data persisting? Check http://localhost:9999/api/storage"

6. Save state for analysis:
   curl -X POST http://localhost:9999/api/save
   "Analyze the state file and identify performance issues"

7. Health check:
   curl http://localhost:9999/health

================================================
Quick test commands:
================================================

# Test if bridge is working:
curl http://localhost:9999/health | jq '.'

# Get current components:
curl http://localhost:9999/api/components | jq '.'

# Get recent errors:
curl http://localhost:9999/api/errors | jq '.'

# Get console logs:
curl http://localhost:9999/api/console | jq '.'
EOF

echo -e "${GREEN}✓ Created claude-bridge-commands.txt with example commands${NC}\n"

# Final summary
echo -e "${GREEN}================================================${NC}"
echo -e "${GREEN}     ✅ Claude Bridge Setup Complete!${NC}"
echo -e "${GREEN}================================================${NC}\n"

echo -e "${BLUE}📁 Structure created:${NC}"
echo -e "   $REACT_APP_PATH/"
echo -e "   ├── claude-bridge/"
echo -e "   │   ├── package.json"
echo -e "   │   ├── bridge-server.js"
echo -e "   │   └── node_modules/"
echo -e "   ├── src/"
echo -e "   │   └── hooks/"
echo -e "   │       └── useClaudeBridge.js"
echo -e "   ├── start-with-bridge.sh"
echo -e "   └── claude-bridge-commands.txt\n"

echo -e "${BLUE}🚀 To start using:${NC}\n"
echo -e "1. Add to your App.js/jsx:"
echo -e "   ${YELLOW}import { useClaudeBridge } from './hooks/useClaudeBridge';${NC}"
echo -e "   ${YELLOW}// Inside App component: useClaudeBridge(process.env.NODE_ENV === 'development');${NC}\n"

echo -e "2. Start both servers:"
echo -e "   ${YELLOW}./start-with-bridge.sh${NC}\n"

echo -e "3. Or start separately:"
echo -e "   ${YELLOW}Terminal 1: cd claude-bridge && npm start${NC}"
echo -e "   ${YELLOW}Terminal 2: npm start${NC}\n"

echo -e "4. Use with Claude Code:"
echo -e "   ${YELLOW}See claude-bridge-commands.txt for examples${NC}\n"

echo -e "${GREEN}Happy debugging! 🎉${NC}"
