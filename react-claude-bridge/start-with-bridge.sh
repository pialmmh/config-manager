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
