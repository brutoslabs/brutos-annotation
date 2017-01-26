

package org.brandao.brutos;

import java.lang.reflect.Method;
import java.util.*;
import org.brandao.brutos.logger.Logger;
import org.brandao.brutos.logger.LoggerProvider;
import org.brandao.brutos.mapping.ActionListener;
import org.brandao.brutos.mapping.Controller;
import org.brandao.brutos.mapping.StringUtil;


public class ControllerManagerImp implements ControllerManager{

    protected Map<String,Controller> mappedControllers;
    protected Map<Class<?>,Controller> classMappedControllers;
    protected ValidatorFactory validatorFactory;
    protected ControllerBuilder current;
    protected ConfigurableApplicationContext applicationContext;
    protected InterceptorManager interceptorManager;
    protected ControllerManager parent;
    protected InternalUpdate internalUpdate;
    
    public ControllerManagerImp() {
        this.mappedControllers      = new HashMap<String,Controller>();
        this.classMappedControllers = new HashMap<Class<?>,Controller>();
        this.internalUpdate         = new InternalUpdateImp(this);
    }

    
    public ControllerBuilder addController( Class<?> classtype ){
        return addController( null, null, 
                !"true".equals(applicationContext.getConfiguration()
                .getProperty(BrutosConstants.VIEW_RESOLVER_AUTO)), null, classtype, "invoke" );
    }

    
    public ControllerBuilder addController( String id, Class<?> classType ){
        return addController( id, null, 
                !"true".equals(applicationContext.getConfiguration()
                .getProperty(BrutosConstants.VIEW_RESOLVER_AUTO)), null, classType, "invoke" );
    }
    
    
    public ControllerBuilder addController( String id, String view, 
            boolean resolvedView, Class<?> classType ){
        return addController( id, view, resolvedView, null, classType, "invoke" );
    }
    
    
    public ControllerBuilder addController( String id, String view,
           boolean resolvedView, String name, Class<?> classType, String actionId ){
        return addController( id, view, resolvedView, 
                DispatcherType.FORWARD, name, classType, actionId );
    }

    
    public ControllerBuilder addController( String id, String view, 
            boolean resolvedView, DispatcherType dispatcherType,
            String name, Class<?> classType, String actionId ){
            return addController( id, view, resolvedView,
                    dispatcherType, name, classType, actionId, 
                    ActionType.PARAMETER);
    }

    public ControllerBuilder addController( String id, String view, 
            boolean resolvedView, DispatcherType dispatcherType,
            String name, Class<?> classType, String actionId, ActionType actionType ){
        return this.addController(id, view, 
                dispatcherType, resolvedView, name, classType, actionId, actionType);
    }
    
    public ControllerBuilder addController( String id, String view, DispatcherType dispatcherType,
            boolean resolvedView, String name, Class<?> classType, String actionId, ActionType actionType ){

        if(classType == null)
            throw new IllegalArgumentException("invalid class type: " + classType);
        
        this.getLogger().info(
                String.format(
                    "adding controller %s",
                    new Object[]{classType.getSimpleName()}));
    	
        id       = StringUtil.adjust(id);
        view     = StringUtil.adjust(view);
        actionId = StringUtil.adjust(actionId);
        name     = StringUtil.adjust(name);
        
        if(actionType == null){
            Properties config = applicationContext.getConfiguration();
            String strategyName =
                    config.getProperty(
                        BrutosConstants.ACTION_TYPE,
                        BrutosConstants.DEFAULT_ACTION_TYPE_NAME);
            actionType = ActionType.valueOf(strategyName.toUpperCase());
        }
        
        if( StringUtil.isEmpty(actionId) )
            actionId = BrutosConstants.DEFAULT_ACTION_ID;
        
        if(ActionType.PARAMETER.equals(actionType) || ActionType.HIERARCHY.equals(actionType)){
            if(StringUtil.isEmpty(id))
                throw new IllegalArgumentException("controller id is required: " + classType.getName() );
        }
        else
        if(!ActionType.DETACHED.equals(actionType))
            throw new IllegalArgumentException("invalid class type: " + classType);
            
        Controller controller = new Controller(this.applicationContext);
        
        
        controller.setClassType(classType);
        
        //Action
        ActionListener ac = new ActionListener();
        ac.setPreAction( getMethodAction( "preAction", controller.getClassType() ) );
        ac.setPostAction( getMethodAction( "postAction", controller.getClassType() ) );
        controller.setActionListener( ac );
        
        controller.setDefaultInterceptorList( interceptorManager.getDefaultInterceptors() );

        this.current = new ControllerBuilder( controller, this, 
                interceptorManager, validatorFactory, applicationContext, internalUpdate );
        
        this.current
        	.setId(id)
        	.setName(name)
        	.setView(view, resolvedView)
        	.setActionId(actionId)
        	.setDispatcherType(dispatcherType)
        	.setActionType(actionType);
        
        addController( controller.getId(), controller );
        
        return this.getCurrent();
    }
    
    private Method getMethodAction( String methodName, Class<?> classe ){
        try{
            Method method = classe.getDeclaredMethod( methodName, new Class[]{} );
            return method;
        }
        catch( Exception e ){
            return null;
        }
    }

    
    public boolean contains( String id ){
        boolean result = this.mappedControllers.containsKey( id );
        return !result && parent != null? parent.contains( id ) : result;
    }
    
    
    public Controller getController( String id ){
        Controller controller = (Controller)mappedControllers.get( id );
        
        if(controller == null && parent != null)
            return parent.getController(id);
        else
            return controller;
        
    }

    
    public Controller getController( Class<?> controllerClass ){
        Controller controller = (Controller)classMappedControllers.get( controllerClass );
        
        if(controller == null && parent != null)
            return parent.getController(controllerClass);
        else
            return controller;
    }

    
    public List<Controller> getControllers() {
        List<Controller> tmp;
        
        tmp = new LinkedList<Controller>(classMappedControllers.values());
        
        if(parent != null)
            tmp.addAll(parent.getControllers());
            
        return Collections.unmodifiableList( tmp );
    }

    public Iterator<Controller> getAllControllers(){
        return new Iterator<Controller>(){
            
            private Iterator<Controller> currentIterator;
            private Iterator<Controller> parentIterator;
            private int index;
            private int maxSize;
            {
                index = 0;
                currentIterator = 
                        classMappedControllers.values().iterator();
                parentIterator = 
                        parent != null? 
                            parent.getControllers().iterator() : 
                            null;
                maxSize = 
                        classMappedControllers.size();
                
            }
            
            public boolean hasNext() {
                if(index < maxSize)
                    return currentIterator.hasNext();
                else
                    return parentIterator != null? parentIterator.hasNext() : false;
            }

            public Controller next() {
                try{
                    if(index < maxSize)
                        return currentIterator.next();
                    else
                        return parentIterator != null? parentIterator.next() : null;
                }
                finally{
                    index++;
                }
            }

            public void remove() {
                if(index < maxSize)
                    currentIterator.remove();
                else
                if(parentIterator != null)
                    parentIterator.remove();
                
                index--;
            }
            
        };
    }
    
    private synchronized void addController( String id, Controller controller ) {
        
        if( id != null){
             if(contains(id))
                throw new BrutosException( String.format("duplicate controller: %s", new Object[]{id} ) );
            else
                mappedControllers.put(id, controller);
        }
        
        classMappedControllers.put( controller.getClassType(), controller);
    }

    private synchronized void removeController( String id, Controller controller ) {
        
        if( id != null){
             if(!contains(id))
                throw new BrutosException( String.format("controller not found: %s", new Object[]{id} ) );
             else
                mappedControllers.remove(id);
        }
        
        if(id == null || id.equals(controller.getId()))
            classMappedControllers.remove( controller.getClassType());
    }
    
    public ControllerBuilder getCurrent() {
        return current;
    }
    
    public void setParent( ControllerManager parent ){
        this.parent = parent;
    }
    
    public ControllerManager getParent(){
        return this.parent;
    }
    
    public Logger getLogger(){
        return LoggerProvider.getCurrentLoggerProvider()
                .getLogger(ControllerBuilder.class);
    }

    public InterceptorManager getInterceptorManager() {
        return this.interceptorManager;
    }

    public void setInterceptorManager(InterceptorManager interceptorManager) {
        this.interceptorManager = interceptorManager;
    }

    public ValidatorFactory getValidatorFactory() {
        return this.validatorFactory;
    }

    public void setValidatorFactory(ValidatorFactory validatorFactory) {
        this.validatorFactory = validatorFactory;
    }

    public ConfigurableApplicationContext getApplicationContext() {
        return this.applicationContext;
    }

    public void setApplicationContext(ConfigurableApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public synchronized void removeController(Class<?> clazz) {
        Controller controller = (Controller) classMappedControllers.get(clazz);
        
        if(controller != null){
            classMappedControllers.remove(clazz);
            
            if(!StringUtil.isEmpty(controller.getId()))
                mappedControllers.remove(controller.getId());
        }
    }

    public synchronized void removeController(String name) {
        Controller controller = (Controller) mappedControllers.get(name);
        
        if(controller != null){
            mappedControllers.remove(name);
            classMappedControllers.remove(controller.getClassType());
        }
        
    }

    public static class InternalUpdateImp implements InternalUpdate{
        
        private ControllerManagerImp manager;
        
        public InternalUpdateImp(ControllerManagerImp manager){
            this.manager = manager;
        }
        
        public void addControllerAlias(Controller controller, String alias ){
            manager.addController(alias, controller);
        }

        public void removeControllerAlias(Controller controller, String alias) {
            manager.removeController(alias, controller);
        }

    }
}
