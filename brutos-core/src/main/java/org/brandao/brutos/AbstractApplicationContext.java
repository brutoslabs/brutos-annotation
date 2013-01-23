/*
 * Brutos Web MVC http://www.brutosframework.com.br/
 * Copyright (C) 2009-2012 Afonso Brandao. (afonso.rbn@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.brandao.brutos;

import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import org.brandao.brutos.codegenerator.CodeGeneratorProvider;
import org.brandao.brutos.io.DefaultResourceLoader;
import org.brandao.brutos.ioc.IOCProvider;
import org.brandao.brutos.logger.Logger;
import org.brandao.brutos.logger.LoggerProvider;
import org.brandao.brutos.mapping.Controller;
import org.brandao.brutos.old.programatic.IOCManager;
import org.brandao.brutos.old.programatic.WebFrameManager;
import org.brandao.brutos.proxy.ProxyFactory;
import org.brandao.brutos.scope.CustomScopeConfigurer;
import org.brandao.brutos.scope.Scope;
import org.brandao.brutos.scope.SingletonScope;
import org.brandao.brutos.scope.ThreadScope;
import org.brandao.brutos.validator.ValidatorProvider;
import org.brandao.brutos.view.ViewProvider;

/**
 * 
 * @author Afonso Brandao
 */
public abstract class AbstractApplicationContext
        extends DefaultResourceLoader 
        implements ConfigurableApplicationContext{

    private Logger logger = LoggerProvider
        .getCurrentLoggerProvider()
            .getLogger(AbstractApplicationContext.class.getName());

    protected IOCManager iocManager;
    
    protected IOCProvider iocProvider;
    
    protected WebFrameManager webFrameManager;
    
    protected InterceptorManager interceptorManager;
    
    protected ControllerManager controllerManager;
    
    protected ViewProvider viewProvider;
    
    protected ValidatorProvider validatorProvider;
    
    protected Invoker invoker;
    
    protected Properties configuration;
    
    protected LoggerProvider loggerProvider;
    
    protected ControllerResolver controllerResolver;
    
    protected ActionResolver actionResolver;
    
    protected MvcResponseFactory responseFactory;
    
    protected MvcRequestFactory requestFactory;
    private Scopes scopes;
    
    private ViewResolver viewResolver;
    
    protected CodeGeneratorProvider codeGeneratorProvider;
    
    private ApplicationContext parent;

    public AbstractApplicationContext() {
        this(null);
    }

    public AbstractApplicationContext( ApplicationContext parent ) {
        
        this.parent = parent;
        
        if(parent == null)
            this.configuration = new Configuration();
        else
            this.configuration = new Configuration(parent.getConfiguration());
            
        this.scopes = new Scopes();
    }

    /**
     * Inicia o processo de configuração da aplicaçao.
     */
    public void configure(){
        configure( this.configuration );
    }

    /**
     * Inicia o processo de configura��o da aplica��o.
     *
     * @param config Configura��o.
     */
    public void configure( Properties config ){
        this.configuration = config;
        this.iocManager    = new IOCManager();
        this.iocProvider   = IOCProvider.getProvider(config);
        
        this.iocProvider.configure(config);
        
        this.interceptorManager    = getNewInterceptorManager();
        this.webFrameManager       = new WebFrameManager( this.interceptorManager, this.iocManager );
        this.controllerResolver    = getNewControllerResolver();
        this.actionResolver        = getNewMethodResolver();
        this.requestFactory        = getMvcRequestFactory();
        this.responseFactory       = getMvcResponseFactory();
        this.validatorProvider     = ValidatorProvider.getValidatorProvider(this.getConfiguration());
        this.viewResolver          = getNewViewResolver();
        this.controllerManager     = getNewControllerManager();
        this.viewProvider          = ViewProvider.getProvider(this.getConfiguration());
        this.codeGeneratorProvider = CodeGeneratorProvider.getProvider(this.getConfiguration());
        this.invoker               = 
                                     createInvoker(
                                         controllerResolver,
                                         iocProvider,
                                         controllerManager,
                                         actionResolver,
                                         this,
                                         viewProvider);
        

        loadScopes();
        
        if( iocProvider.containsBeanDefinition("customScopes") ){
            CustomScopeConfigurer customScopesConfigurer =
                    (CustomScopeConfigurer)iocProvider.getBean("customScopes");
            Map customScopes = customScopesConfigurer.getCustomScopes();
            Set set = customScopes.keySet();
            Iterator i = set.iterator();
            while( i.hasNext() ){
                Object key = i.next();
            //for( Object key: i )
                scopes.register( (String)key,(Scope)customScopes.get(key) );
            }
        }

        this.loadIOCManager(iocManager);
        this.loadInterceptorManager(interceptorManager);
        this.loadController(controllerManager);
        this.loadWebFrameManager(webFrameManager);
    }

    protected void loadScopes(){
        getScopes()
            .register(
                ScopeType.SINGLETON.toString(),
                new SingletonScope());

        getScopes()
            .register(
                ScopeType.THREAD.toString(),
                new ThreadScope());

        getScopes()
            .register(
                ScopeType.PARAM.toString(),
                getScopes().get(ScopeType.THREAD));

        getScopes()
            .register(
                ScopeType.REQUEST.toString(),
                getScopes().get(ScopeType.THREAD));

    }
    /**
     * Define o respons�vel por resolver os controladores.
     * @param controllerResolver Respons�vel por resolver os controladores
     */
    protected void setControllerResolver( ControllerResolver controllerResolver ){
        this.controllerResolver = controllerResolver;
    }
    
    /**
     * Obt�m o respons�vel por resolver os controladores.
     * @return Respons�vel por resolver os controladores
     */
    protected ControllerResolver getNewControllerResolver(){
        try{
            Class clazz = ClassUtil.get(configuration.getProperty(
                    BrutosConstants.CONTROLLER_RESOLVER_CLASS,
                    DefaultControllerResolver.class.getName()));
            
            return (ControllerResolver)ClassUtil.getInstance(clazz);
            /*
            ControllerResolver instance = (ControllerResolver) Class.forName(
                    configuration.getProperty(
                    "org.brandao.brutos.controller.class",
                    DefaultControllerResolver.class.getName()
                ),
                    true,
                    Thread.currentThread().getContextClassLoader()

            ).newInstance();
            
            return instance;
            */
        }
        catch( Exception e ){
            throw new BrutosException( e );
        }
    }

    /**
     * Obt�m a f�brica de respostas.
     * @return F�brica de respostas.
     */
    protected MvcResponseFactory getMvcResponseFactory(){
        try{
            Class clazz = ClassUtil.get(configuration.getProperty(
                    BrutosConstants.RESPONSE_FACTORY,
                    DefaultMvcResponseFactory.class.getName()));
            
            return (MvcResponseFactory)ClassUtil.getInstance(clazz);
            /*
            MvcResponseFactory instance = (MvcResponseFactory) Class.forName(
                    configuration.getProperty(
                    "org.brandao.brutos.controller.response_factory",
                    DefaultMvcResponseFactory.class.getName()
                ),
                    true,
                    Thread.currentThread().getContextClassLoader()

            ).newInstance();

            return instance;
            */
        }
        catch( Exception e ){
            throw new BrutosException( e );
        }
    }

    /**
     * Obt�m a f�brica de requisi��es.
     * @return F�brica de requisi��es.
     */
    protected MvcRequestFactory getMvcRequestFactory(){
        try{
            Class clazz = ClassUtil.get(configuration.getProperty(
                    BrutosConstants.REQUEST_FACTORY,
                    DefaultMvcRequestFactory.class.getName()));
            
            return (MvcRequestFactory)ClassUtil.getInstance(clazz);
            /*
            MvcRequestFactory instance = (MvcRequestFactory) Class.forName(
                    configuration.getProperty(
                    "org.brandao.brutos.controller.request_factory",
                    DefaultMvcRequestFactory.class.getName()
                ),
                    true,
                    Thread.currentThread().getContextClassLoader()

            ).newInstance();

            return instance;
            */
        }
        catch( Exception e ){
            throw new BrutosException( e );
        }
    }

    /**
     * Obt�m o respons�vel por resolver as a��es.
     * @return Respons�vel por resolver as a��es.
     */
    protected ActionResolver getNewMethodResolver(){
        try{
            Class clazz = ClassUtil.get(configuration.getProperty(
                    BrutosConstants.ACTION_RESOLVER,
                    DefaultActionResolver.class.getName()));
            
            return (ActionResolver)ClassUtil.getInstance(clazz);
            /*
            ActionResolver instance = (ActionResolver) Class.forName(
                    configuration.getProperty(
                    "org.brandao.brutos.controller.action_resolver",
                    DefaultActionResolver.class.getName()
                ),
                    true,
                    Thread.currentThread().getContextClassLoader()

            ).newInstance();
            
            return instance;
            */
        }
        catch( Exception e ){
            throw new BrutosException( e );
        }
    }

    protected ControllerManager getNewControllerManager(){
        try{
            String className =
                configuration.getProperty(
                    BrutosConstants.CONTROLLER_MANAGER_CLASS,
                    ControllerManager.class.getName());
            
            Class clazz = 
                    ClassUtil.get(className);
            
            ControllerManager instance = 
                (ControllerManager)ClassUtil.getInstance(
                    clazz, 
                    new Class[]{
                        InterceptorManager.class, 
                        ValidatorProvider.class, 
                        ControllerManager.class,
                        ConfigurableApplicationContext.class},
                    new Object[]{
                        interceptorManager, 
                        validatorProvider, 
                        this.parent == null? 
                            null : 
                            ((ConfigurableApplicationContext)parent).getControllerManager(),
                        this });
            
            return instance;
        }
        catch( Exception e ){
            throw new BrutosException( e );
        }
    }

    protected InterceptorManager getNewInterceptorManager(){
        try{
            String className =
                configuration.getProperty(
                    BrutosConstants.INTERCEPTOR_MANAGER_CLASS,
                    InterceptorManager.class.getName());
            
            Class clazz = 
                    ClassUtil.get(className);
            
            InterceptorManager instance = 
                (InterceptorManager)ClassUtil.getInstance(
                    clazz, 
                    new Class[]{
                        InterceptorManager.class},
                    new Object[]{
                        this.parent == null? 
                        null : 
                        ((ConfigurableApplicationContext)parent).getInterceptorManager()
                        });
            
            return instance;
        }
        catch( Exception e ){
            throw new BrutosException( e );
        }
    }

    protected Invoker createInvoker( 
            ControllerResolver controllerResolver, IOCProvider iocProvider, 
            ControllerManager controllerManager, ActionResolver actionResolver, 
            ConfigurableApplicationContext applicationContext, ViewProvider viewProvider ){
        try{
            String className =
                configuration.getProperty(
                    BrutosConstants.INVOKER_CLASS,
                    BrutosConstants.DEFAULT_INVOKER_CLASS);
            
            Class clazz = 
                    ClassUtil.get(className);
            
            Invoker instance = 
                (Invoker)ClassUtil.getInstance(
                    clazz, 
                    new Class[]{
                        ControllerResolver.class, 
                        IOCProvider.class, 
                        ControllerManager.class, 
                        ActionResolver.class, 
                        ConfigurableApplicationContext.class, 
                        ViewProvider.class},
                    new Object[]{
                        controllerResolver, 
                        iocProvider, 
                        controllerManager, 
                        actionResolver, 
                        applicationContext, 
                        viewProvider
                        });
            
            return instance;
        }
        catch( Exception e ){
            throw new BrutosException( e );
        }
    }
    
    protected ViewResolver getNewViewResolver(){
        try{
            String className = 
                configuration.getProperty(
                    "org.brandao.brutos.view.resolver",
                    DefaultViewResolver.class.getName());
            
            return (ViewResolver)ClassUtil.getInstance(ClassUtil.get(className));
        }
        catch( Exception e ){
            throw new BrutosException( e );
        }
    }
    
    /**
     * M�todo invocado quando a aplica��o � finalizada.
     */
    public void destroy(){
        this.actionResolver = null;
        this.codeGeneratorProvider = null;
        this.configuration = null;
        this.controllerManager = null;
        this.controllerResolver = null;
        this.interceptorManager = null;
        this.invoker = null;
        this.iocManager = null;
        this.iocProvider = null;
        this.loggerProvider = null;
        this.requestFactory = null;
        this.responseFactory = null;
        this.scopes.clear();
        this.validatorProvider = null;
        this.viewProvider = null;
        this.viewProvider = null;
        this.webFrameManager = null;
        this.viewResolver = null;
    }

    /**
     * @deprecated 
     * @param iocManager
     */
    protected void loadIOCManager( IOCManager iocManager ){
    }

    /**
     * @deprecated 
     * @param webFrameManager
     */
    protected void loadWebFrameManager( WebFrameManager webFrameManager ){
    }

    /**
     * Configura os interceptadores.
     * @param interceptorManager Gestor de interceptadores.
     */
    protected void loadInterceptorManager( InterceptorManager interceptorManager ){
    }
    
    /**
     * Configura os controladores.
     * @param controllerManager Gestor dos controladores.
     */
    protected void loadController( ControllerManager controllerManager ){
    }

    /**
     * Obt�m a configura��o da aplica��o.
     * @return Configura��o da aplica��o.
     */
    public Properties getConfiguration(){
        return configuration;
    }

    /**
     * Obt�m o provedor de log.
     * @return Provedor de log.
     */
    public LoggerProvider getLoggerProvider(){
        return loggerProvider;
    }

    /**
     * Obt�m o objeto respons�vel por enviar a resposta ao cliente.
     * @return Resposta.
     */
    public MvcResponse getMvcResponse() {
        return this.responseFactory.getCurrentResponse();
    }

    /**
     * Obt�m o objeto respons�vel por receber a requisi��o do cliente.
     * @return Requisi��o.
     */
    public MvcRequest getMvcRequest() {
        return this.requestFactory.getCurrentRequest();
    }

    public Scopes getScopes() {
        return scopes;
    }


    public MvcRequestFactory getRequestFactory() {
        return this.requestFactory;
    }

    public MvcResponseFactory getResponseFactory() {
        return this.responseFactory;
    }

    public void setIocManager(IOCManager iocManager) {
        this.iocManager = iocManager;
    }

    public void setWebFrameManager(WebFrameManager webFrameManager) {
        this.webFrameManager = webFrameManager;
    }

    public void setInterceptorManager(InterceptorManager interceptorManager) {
        this.interceptorManager = interceptorManager;
    }

    public ViewProvider getViewProvider() {
        return this.viewProvider;
    }

    public ValidatorProvider getValidatorProvider() {
        return this.validatorProvider;
    }

    public Invoker getInvoker() {
        return this.invoker;
    }

    public void setInvoker( Invoker value) {
        this.invoker = value;
    }

    public void setConfiguration(Properties config) {
        this.configuration = config;
    }

    public void setIocProvider(IOCProvider iocProvider) {
        this.iocProvider = iocProvider;
    }

    public InterceptorManager getInterceptorManager() {
        return this.interceptorManager;
    }

    public ControllerManager getControllerManager() {
        return this.controllerManager;
    }

    public IOCManager getIocManager() {
        return this.iocManager;
    }

    public WebFrameManager getWebFrameManager() {
        return this.webFrameManager;
    }

    public IOCProvider getIocProvider() {
        return this.iocProvider;
    }

    public ControllerResolver getControllerResolver() {
        return this.controllerResolver;
    }

    public ActionResolver getActionResolver() {
        return this.actionResolver;
    }

    public CodeGeneratorProvider getCodeGeneratorProvider() {
        return this.codeGeneratorProvider;
    }

    public void setCodeGeneratorProvider(CodeGeneratorProvider codeGeneratorProvider) {
        this.codeGeneratorProvider = codeGeneratorProvider;
    }

    public ViewResolver getViewResolver() {
        return viewResolver;
    }

    public void setViewResolver(ViewResolver viewResolver) {
        this.viewResolver = viewResolver;
    }
    
    public Object getController(Class clazz){

        Controller controller =
            controllerResolver
                .getController(
                    controllerManager,
                    clazz);

        if( controller == null )
            throw new BrutosException(
                String.format(
                    "controller not configured: %s",
                    new Object[]{clazz.getName()} ));

        Object resource = controller.getInstance(iocProvider);
                //iocProvider.getBean(controller.getId());

        ProxyFactory proxyFactory =
                codeGeneratorProvider
                    .getProxyFactory(controller.getClassType());

        Object proxy =
                proxyFactory
                    .getNewProxy(
                        resource,
                        controller,
                        this,
                        invoker);

        return proxy;
    }
    
    public void setParent(ApplicationContext applicationContext){
        
        if(!(applicationContext instanceof ConfigurableApplicationContext)){
            throw new IllegalArgumentException("expected: instance of " + 
                    ConfigurableApplicationContext.class.getName());
        }
        
        this.parent = applicationContext;
        
        this.controllerManager.setParent(
            ((ConfigurableApplicationContext)applicationContext)
                .getControllerManager());
        
        this.interceptorManager.setParent(
            ((ConfigurableApplicationContext)applicationContext)
                .getInterceptorManager());
    }

    public ApplicationContext getParent(){
        return this.parent;
    }

    public Object getBean(Class clazz){
        return this.getIocProvider().getBean(clazz);
    }

    public Object getBean(String name){
        return this.getIocProvider().getBean(name);
    }
    
}
