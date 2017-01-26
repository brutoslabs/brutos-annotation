

package org.brandao.brutos.web;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.brandao.brutos.*;
import org.brandao.brutos.interceptor.ConfigurableInterceptorHandler;
import org.brandao.brutos.mapping.Action;
import org.brandao.brutos.mapping.Controller;
import org.brandao.brutos.scope.Scope;


public class WebControllerResolver implements ControllerResolver{

    private static Map<String,URIMapping> uris = new HashMap<String, URIMapping>();

    public WebControllerResolver() {
    }

    public static URIMapping getURIMapping( String uri ){
        try{
            if( uris.containsKey( uri ) )
                return uris.get( uri );
            else{
                URIMapping map = new URIMapping( uri );
                uris.put( uri , map);
                return map;
            }
        }
        catch( Exception e ){
            throw new BrutosException( e.getMessage(), e );
        }
    }

    public Controller getController(ControllerManager controllerManager, 
            ConfigurableInterceptorHandler handler) {
        String uri = handler.requestId();
        Iterator controllers = controllerManager.getAllControllers();
        Scope paramScope =
                handler.getContext().getScopes().get(WebScopeType.PARAM);
        
        while(controllers.hasNext()){
            Controller controller = (Controller)controllers.next();
            ActionType actionType = controller.getActionType();
            String controllerId = controller.getId();
            
            if(this.matches(uri, paramScope, controllerId, actionType, controller, handler))
            	return controller;
            
            List<String> alias = controller.getAlias();
            
            if(!alias.isEmpty()){
            	for(String aliasURI: alias){
	                if(this.matches(uri, paramScope, aliasURI, actionType, controller, handler))
	                	return controller;
            	}
            	
            }
        }

        return null;
    }
    
    private boolean matches(String uri, Scope scope, String controllerId, 
    		ActionType actionType, 
    		Controller controller,
    		ConfigurableInterceptorHandler handler){
    	
        if(actionType == ActionType.PARAMETER){
        	
        	if(controllerId == null)
        		throw new BrutosException("invalid controller id: " + controllerId);
        	
            URIMapping uriMap = getURIMapping( controllerId );

            if(uriMap.matches(uri)){
                updateRequest(uri, scope, uriMap);
                return true;
            }
        }
        else{
            if(actionType == ActionType.HIERARCHY){
            	
            	if(controllerId == null)
            		throw new BrutosException("invalid controller id: " + controllerId);
            	
                URIMapping uriMap = getURIMapping( controllerId );
                
                if(uriMap.matches(uri)){
	                updateRequest(uri, scope, uriMap);
	                return true;
                }
            }
            
            Action action = this.getAction(controller, actionType, uri, scope);

            if(action != null){
                updateHandler(action, handler);
                return true;
            }
        }
    	
        return false;
    }
    
    private Action getAction(Controller controller, ActionType actionType, String uri, Scope paramScope){
        Map<String,Action> actions = controller.getActions();
        Iterator<String> actionsId = actions.keySet().iterator();
        
        while(actionsId.hasNext()){
            String actionId = (String) actionsId.next();
            String fullActionId =
                    actionType == ActionType.HIERARCHY? 
                        controller.getId() + actionId :
                        actionId;

            URIMapping uriMap = getURIMapping( fullActionId );

            if(uriMap.matches(uri)){
                updateRequest(uri, paramScope, uriMap);
                return actions.get(actionId);
            }
            
        }
        
        return null;
    }
    
    private void updateHandler(Action action, ConfigurableInterceptorHandler handler){
        ConfigurableApplicationContext context =
            (ConfigurableApplicationContext)handler.getContext();

        ActionResolver actionResolver =
                context.getActionResolver();

        ResourceAction resourceAction = 
                actionResolver.getResourceAction(action);

        handler.setResourceAction(resourceAction);
    }
    
    private void updateRequest(String uri, Scope paramScope, URIMapping uriMap){
        Map<String,List<String>> params = uriMap.getParameters(uri);
        for(String key: params.keySet() ){
        	for(String value: params.get(key)){
        		paramScope.put(key, value);
        	}
        }
    }
    
    public Controller getController(ControllerManager controllerManager, Class controllerClass) {
        Controller controller =
                controllerManager.getController(controllerClass);

        if(controller == null)
            throw new BrutosException(
                "controller not found: " + controllerClass.getName() );
        return controller;
    }

}