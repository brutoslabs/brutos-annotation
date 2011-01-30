/*
 * Brutos Web MVC http://brutos.sourceforge.net/
 * Copyright (C) 2009 Afonso Brandao. (afonso.rbn@gmail.com)
 *
 * This library is free software. You can redistribute it 
 * and/or modify it under the terms of the GNU General Public
 * License (GPL) version 3.0 or (at your option) any later 
 * version.
 * You may obtain a copy of the License at
 * 
 * http://www.gnu.org/licenses/gpl.html 
 * 
 * Distributed WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied.
 *
 */

package org.brandao.brutos;

import java.util.Map;
import java.util.Properties;
import java.util.Set;
import javax.servlet.ServletContextEvent;
import org.brandao.brutos.ioc.IOCProvider;
import org.brandao.brutos.logger.Logger;
import org.brandao.brutos.logger.LoggerProvider;
import org.brandao.brutos.old.programatic.*;
import org.brandao.brutos.scope.CustomScopeConfigurer;
import org.brandao.brutos.scope.Scope;
import org.brandao.brutos.scope.Scopes;
import org.brandao.brutos.validator.ValidatorProvider;
import org.brandao.brutos.view.ViewProvider;

/**
 * Classe central que permite a configura��o de um aplicativo. Com essa classe
 * � poss�vel:
 * <ul>
 * <li>configurar interceptadores;</li>
 * <li>incluir novos controladores;</li>
 * <li>manipular os controladores;</li>
 * <li>manipular a resolu��o de controladores;</li>
 * <li>manipular a execu��o de a��es;</li>
 * <li>determinar novas regras de valida��o</li>
 * </ul>
 * 
 * @author Afonso Brandao
 */
public abstract class ApplicationContext {

    private static Logger logger = LoggerProvider
        .getCurrentLoggerProvider()
            .getLogger(ApplicationContext.class.getName());

    private IOCManager iocManager;
    private IOCProvider iocProvider;
    private WebFrameManager webFrameManager;
    private InterceptorManager interceptorManager;
    private ControllerManager controllerManager;
    private ViewProvider viewProvider;
    private ValidatorProvider validatorProvider;
    private Invoker invoker;
    private Properties configuration;
    private LoggerProvider loggerProvider;
    private ControllerResolver controllerResolver;
    private ActionResolver actionResolver;
    private MvcResponseFactory responseFactory;
    private MvcRequestFactory requestFactory;

    public ApplicationContext() {
        this.configuration = new Configuration();
    }

    /**
     * @deprecated 
     * @param config
     * @param sce
     */
    public void configure( Configuration config, ServletContextEvent sce ){
    }

    /**
     * Inicia o processo de configura��o da aplica��o.
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
        this.iocManager = new IOCManager();
        this.setIocProvider(IOCProvider.getProvider(config));
        this.getIocProvider().configure(config);
        this.interceptorManager = new InterceptorManager();
        this.webFrameManager = new WebFrameManager( this.interceptorManager, this.iocManager );
        this.controllerResolver = getNewControllerResolver();
        this.actionResolver = getNewMethodResolver();
        this.responseFactory = getMvcResponseFactory();
        this.responseFactory = getMvcResponseFactory();
        this.validatorProvider = ValidatorProvider.getValidatorProvider(this.getConfiguration());
        this.controllerManager = new ControllerManager(this.interceptorManager, this.getValidatorProvider());
        this.invoker = new Invoker(  this.getControllerResolver(), getIocProvider(), controllerManager, this.getActionResolver(), this );
        this.viewProvider = ViewProvider.getProvider(this.getConfiguration());

        if( iocProvider.containsBeanDefinition("customScopes") ){
            CustomScopeConfigurer customScopes =
                    (CustomScopeConfigurer)iocProvider.getInstance("customScopes");
            Map scopes = customScopes.getCustomScopes();
            Set i = scopes.keySet();
            for( Object key: i )
                Scopes.register( (String)key,(Scope)scopes.get(key) );
        }

        this.loadIOCManager(iocManager);
        this.loadInterceptorManager(interceptorManager);
        this.loadController(getControllerManager());
        this.loadWebFrameManager(webFrameManager);
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
            ControllerResolver instance = (ControllerResolver) Class.forName(
                    configuration.getProperty(
                    "org.brandao.brutos.controller.class",
                    DefaultControllerResolver.class.getName()
                ),
                    true,
                    Thread.currentThread().getContextClassLoader()

            ).newInstance();

            return instance;
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
            MvcResponseFactory instance = (MvcResponseFactory) Class.forName(
                    configuration.getProperty(
                    "org.brandao.brutos.controller.response_factory",
                    "org.brandao.brutos.DefaultMvcResponseFactory"
                ),
                    true,
                    Thread.currentThread().getContextClassLoader()

            ).newInstance();

            return instance;
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
            MvcRequestFactory instance = (MvcRequestFactory) Class.forName(
                    configuration.getProperty(
                    "org.brandao.brutos.controller.request_factory",
                    "org.brandao.brutos.DefaultMvcRequestFactory"
                ),
                    true,
                    Thread.currentThread().getContextClassLoader()

            ).newInstance();

            return instance;
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
            ActionResolver instance = (ActionResolver) Class.forName(
                    configuration.getProperty(
                    "org.brandao.brutos.controller.method_resolver",
                    DefaultActionResolver.class.getName()
                ),
                    true,
                    Thread.currentThread().getContextClassLoader()

            ).newInstance();
            return instance;
        }
        catch( Exception e ){
            throw new BrutosException( e );
        }
    }

    /**
     * M�todo invocado quando a aplica��o � finalizada.
     */
    public abstract void destroy();

    /**
     * @deprecated 
     * @return
     */
    public IOCManager getIocManager() {
        return iocManager;
    }

    /**
     * @deprecated 
     * @param iocManager
     */
    public void setIocManager(IOCManager iocManager) {
        this.iocManager = iocManager;
    }

    /**
     * @deprecated 
     * @return
     */
    public WebFrameManager getWebFrameManager() {
        return webFrameManager;
    }

    /**
     * @deprecated 
     * @param webFrameManager
     */
    public void setWebFrameManager(WebFrameManager webFrameManager) {
        this.webFrameManager = webFrameManager;
    }

    /**
     * Obt�m o gestor de interceptadores.
     * @return Gestor de interceptadores.
     */
    public InterceptorManager getInterceptorManager() {
        return interceptorManager;
    }

    /**
     * Define o gestor de interceptadores.
     * @param interceptorManager Gestor de interceptadores.
     */
    public void setInterceptorManager(InterceptorManager interceptorManager) {
        this.interceptorManager = interceptorManager;
    }

    /**
     * @deprecated 
     * @param iocManager
     */
    protected abstract void loadIOCManager( IOCManager iocManager );

    /**
     * @deprecated 
     * @param webFrameManager
     */
    protected abstract void loadWebFrameManager( WebFrameManager webFrameManager );

    /**
     * Configura os interceptadores.
     * @param interceptorManager Gestor de interceptadores.
     */
    protected abstract void loadInterceptorManager( InterceptorManager interceptorManager );
    
    /**
     * Configura os controladores.
     * @param interceptorManager Gestor dos controladores.
     */
    protected abstract void loadController( ControllerManager controllerManager );

    /**
     * Obt�m a aplica��o corrente.
     * @return Aplica��o.
     */
    public static ApplicationContext getCurrentApplicationContext(){
        Scope requestScope = Scopes.get(ScopeType.REQUEST);

        if( requestScope == null )
            throw new BrutosException( "scope not configured: " + ScopeType.REQUEST.toString() );

        ApplicationContext app = (ApplicationContext)
                        requestScope.get( BrutosConstants.ROOT_APPLICATION_CONTEXT_ATTRIBUTE );

        return app;
    }

    /**
     * Obt�m um determinado controlador.
     * @param controllerClass Classe do controlador
     * @return Controlador.
     */
    public Object getController( Class controllerClass ){
        return null;
    }

    /**
     * Obt�m o gestor de controladores.
     * @return Gestor de controladores.
     */
    public ControllerManager getControllerManager() {
        return controllerManager;
    }

    /**
     * Obt�m o provedor da vis�o.
     * @return Provedor da vis�o.
     */
    public ViewProvider getViewProvider() {
        return viewProvider;
    }

    /**
     * Obt�m o provedor das regras de valida��o.
     * @return Provedor das regras de valida��o
     */
    public ValidatorProvider getValidatorProvider() {
        return validatorProvider;
    }

    /**
     * Obt�m o respons�vel por executar as a��es.
     * @return Respons�vel por executar as a��es.
     */
    public Invoker getInvoker() {
        return invoker;
    }

    /**
     * Define as configura��es da aplica��o.
     * @param config Configura��o.
     */
    protected void setConfiguration( Properties config ){
        this.configuration = config;
    }

    /**
     * Obt�m a configura��o da aplica��o.
     * @return Configura��o da aplica��o.
     */
    public Properties getConfiguration() {
        return configuration;
    }

    /**
     * Obt�m o provedor de log.
     * @return Provedor de log.
     */
    public LoggerProvider getLoggerProvider() {
        return loggerProvider;
    }

    /**
     * Obt�m o provedor do container IOC.
     * @return Provedor do container IOC.
     */
    public IOCProvider getIocProvider() {
        return iocProvider;
    }

    /**
     * Define o provedor do container IOC.
     * @param iocProvider Provedor do container IOC.
     */
    public void setIocProvider(IOCProvider iocProvider) {
        this.iocProvider = iocProvider;
    }

    /**
     * Obt�m o respons�vel por resolver os controladores.
     * @return Respons�vel por resolver os controladores.
     */
    public ControllerResolver getControllerResolver() {
        return controllerResolver;
    }

    /**
     * Obt�m o respons�vel por resolver as a��es.
     * @return Respons�vel por resolver as a��es.
     */
    public ActionResolver getActionResolver() {
        return actionResolver;
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

}
