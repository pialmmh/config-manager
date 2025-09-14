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
