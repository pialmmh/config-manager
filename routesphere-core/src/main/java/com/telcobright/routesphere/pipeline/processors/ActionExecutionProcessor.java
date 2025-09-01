package com.telcobright.routesphere.pipeline.processors;

import com.telcobright.routesphere.pipeline.PipelineProcessor;
import com.telcobright.routesphere.pipeline.RoutingContext;
import com.telcobright.routesphere.pipeline.RoutingResponse;
import com.telcobright.routesphere.protocols.Protocol;

/**
 * Executes the final routing action based on business rules
 * Can trigger state machine, flow execution, or direct response
 */
public class ActionExecutionProcessor implements PipelineProcessor {
    
    @Override
    public boolean process(RoutingContext context) {
        context.moveToStage(RoutingContext.PipelineStage.ACTION_EXECUTION);
        
        // Get routing preference from business rules
        String routingPreference = (String) context.getBusinessRuleOutput("routing_preference");
        
        System.out.println("Executing action with routing preference: " + routingPreference);
        
        // Determine action type based on protocol and business rules
        ActionType actionType = determineActionType(context);
        
        switch (actionType) {
            case STATE_MACHINE:
                return executeStateMachine(context);
                
            case FLOW_EXECUTION:
                return executeFlow(context);
                
            case DIRECT_ROUTE:
                return executeDirectRoute(context);
                
            case PROXY:
                return executeProxy(context);
                
            case REDIRECT:
                return executeRedirect(context);
                
            default:
                context.getResponse()
                    .withType(RoutingResponse.ResponseType.ERROR)
                    .withStatus(500, "Unknown action type");
                return false;
        }
    }
    
    private ActionType determineActionType(RoutingContext context) {
        Protocol protocol = context.getRequest().getProtocol();
        
        // Determine based on protocol and context
        switch (protocol) {
            case SIP_UDP:
            case SIP_TCP:
            case SIP_TLS:
                // SIP typically uses state machine for call control
                return ActionType.STATE_MACHINE;
                
            case HTTP:
            case HTTPS:
                // HTTP might use flow execution for API orchestration
                return ActionType.FLOW_EXECUTION;
                
            case ESL:
                // FreeSWITCH ESL might trigger dialplan or script
                return ActionType.FLOW_EXECUTION;
                
            case SMS:
                // SMS might use direct routing
                return ActionType.DIRECT_ROUTE;
                
            default:
                return ActionType.PROXY;
        }
    }
    
    private boolean executeStateMachine(RoutingContext context) {
        System.out.println("Triggering state machine for request: " + 
            context.getRequest().getRequestId());
        
        // In real implementation, would integrate with statemachine module
        // For demo, simulate state machine execution
        
        context.getResponse()
            .withType(RoutingResponse.ResponseType.STATE_MACHINE)
            .withStatus(200, "State machine triggered")
            .addHeader("X-State-Machine-Id", "sm-" + System.currentTimeMillis())
            .addHeader("X-Initial-State", "IDLE");
        
        // Store state machine reference in context
        context.setAttribute("state_machine_id", "sm-" + System.currentTimeMillis());
        
        return true;
    }
    
    private boolean executeFlow(RoutingContext context) {
        System.out.println("Executing flow for request: " + 
            context.getRequest().getRequestId());
        
        // In real implementation, would trigger flow engine
        // For demo, simulate flow execution
        
        context.getResponse()
            .withType(RoutingResponse.ResponseType.FLOW)
            .withStatus(200, "Flow executed")
            .addHeader("X-Flow-Id", "flow-" + System.currentTimeMillis())
            .addHeader("X-Flow-Type", "routing-flow")
            .setPayload("{\"flow_result\": \"success\", \"next_action\": \"route_to_destination\"}");
        
        return true;
    }
    
    private boolean executeDirectRoute(RoutingContext context) {
        System.out.println("Direct routing for request: " + 
            context.getRequest().getRequestId());
        
        // Direct route to destination
        String destination = determineDestination(context);
        
        context.getResponse()
            .withType(RoutingResponse.ResponseType.ROUTE)
            .withStatus(200, "Routed successfully")
            .addHeader("X-Route-Destination", destination)
            .addHeader("X-Route-Method", "DIRECT");
        
        return true;
    }
    
    private boolean executeProxy(RoutingContext context) {
        System.out.println("Proxying request: " + 
            context.getRequest().getRequestId());
        
        // Proxy to upstream server
        String upstream = selectUpstream(context);
        
        context.getResponse()
            .withType(RoutingResponse.ResponseType.PROXY)
            .withStatus(200, "Proxied to upstream")
            .addHeader("X-Upstream-Server", upstream)
            .addHeader("X-Proxy-Mode", "TRANSPARENT");
        
        return true;
    }
    
    private boolean executeRedirect(RoutingContext context) {
        System.out.println("Redirecting request: " + 
            context.getRequest().getRequestId());
        
        // Redirect to another endpoint
        String redirectTarget = determineRedirectTarget(context);
        
        context.getResponse()
            .withType(RoutingResponse.ResponseType.REDIRECT)
            .withStatus(302, "Redirected")
            .addHeader("Location", redirectTarget);
        
        return true;
    }
    
    private String determineDestination(RoutingContext context) {
        // In real implementation, would use routing tables and business rules
        // For demo, return sample destination
        Protocol protocol = context.getRequest().getProtocol();
        
        switch (protocol) {
            case SIP_UDP:
            case SIP_TCP:
                return "sip:gateway.routesphere.com:5060";
            case HTTP:
            case HTTPS:
                return "https://api.backend.routesphere.com";
            case ESL:
                return "freeswitch-node-1.routesphere.com";
            default:
                return "default.routesphere.com";
        }
    }
    
    private String selectUpstream(RoutingContext context) {
        // In real implementation, would use load balancing algorithm
        // For demo, return sample upstream
        return "upstream-" + (System.currentTimeMillis() % 3 + 1) + ".routesphere.com";
    }
    
    private String determineRedirectTarget(RoutingContext context) {
        // In real implementation, would determine based on business rules
        // For demo, return sample redirect
        return "https://redirect.routesphere.com/handle/" + context.getRequest().getRequestId();
    }
    
    @Override
    public String getName() {
        return "ActionExecution";
    }
    
    @Override
    public int getOrder() {
        return 400;
    }
    
    /**
     * Types of actions that can be executed
     */
    private enum ActionType {
        STATE_MACHINE,    // Trigger state machine for stateful processing
        FLOW_EXECUTION,   // Execute a predefined flow
        DIRECT_ROUTE,     // Direct routing to destination
        PROXY,           // Proxy to upstream server
        REDIRECT,        // HTTP redirect
        RESPONSE         // Direct response
    }
}