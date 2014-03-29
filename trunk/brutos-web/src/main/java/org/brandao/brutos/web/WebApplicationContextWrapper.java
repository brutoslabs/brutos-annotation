/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.brandao.brutos.web;

import java.util.Properties;
import javax.servlet.ServletContext;
import org.brandao.brutos.ActionResolver;
import org.brandao.brutos.ComponentRegistry;
import org.brandao.brutos.ControllerManager;
import org.brandao.brutos.ControllerResolver;
import org.brandao.brutos.InterceptorManager;
import org.brandao.brutos.Invoker;
import org.brandao.brutos.MvcRequest;
import org.brandao.brutos.MvcRequestFactory;
import org.brandao.brutos.MvcResponse;
import org.brandao.brutos.MvcResponseFactory;
import org.brandao.brutos.Scopes;
import org.brandao.brutos.codegenerator.CodeGeneratorProvider;
import org.brandao.brutos.ioc.IOCProvider;
import org.brandao.brutos.validator.ValidatorProvider;
import org.brandao.brutos.view.ViewProvider;

/**
 *
 * @author Brandao
 */
public class WebApplicationContextWrapper 
        extends AbstractWebApplicationContext{

    protected ConfigurableWebApplicationContext applicationContext;

    public WebApplicationContextWrapper(ConfigurableWebApplicationContext app){
        this.applicationContext = app;
    }

    public void destroy(){
        this.applicationContext.destroy();
    }

    public Properties getConfiguration(){
        return this.applicationContext.getConfiguration();
    }

    public MvcResponse getMvcResponse() {
        return this.applicationContext.getMvcResponse();
    }

    public MvcRequest getMvcRequest() {
        return this.applicationContext.getMvcRequest();
    }

    public Scopes getScopes() {
        return this.applicationContext.getScopes();
    }

    public ServletContext getContext(){
        return this.applicationContext.getContext();
    }

    public void setServletContext(ServletContext servletContext){
        
        if( applicationContext instanceof ConfigurableWebApplicationContext )
            ((ConfigurableWebApplicationContext)this.applicationContext).
                    setServletContext(servletContext);

    }

    public MvcRequestFactory getRequestFactory(){
        return applicationContext.getRequestFactory();
    }

    public MvcResponseFactory getResponseFactory(){
        return applicationContext.getResponseFactory();
    }

    public void setInterceptorManager(InterceptorManager interceptorManager){
        applicationContext.setInterceptorManager(interceptorManager);
    }

    public ViewProvider getViewProvider(){
        return applicationContext.getViewProvider();
    }

    public ValidatorProvider getValidatorProvider(){
        return applicationContext.getValidatorProvider();
    }

    public Invoker getInvoker(){
        return applicationContext.getInvoker();
    }

    public void setInvoker(Invoker value){
        applicationContext.setInvoker(value);
    }

    public void setConfiguration( Properties config ){
        applicationContext.setConfiguration(config);
    }

    public void setIocProvider(IOCProvider iocProvider){
        applicationContext.setIocProvider(iocProvider);
    }


    public InterceptorManager getInterceptorManager(){
        return applicationContext.getInterceptorManager();
    }

    public ControllerManager getControllerManager(){
        return applicationContext.getControllerManager();
    }

    public IOCProvider getIocProvider(){
        return applicationContext.getIocProvider();
    }

    public ControllerResolver getControllerResolver(){
        return applicationContext.getControllerResolver();
    }

    public ActionResolver getActionResolver(){
        return applicationContext.getActionResolver();
    }

    public CodeGeneratorProvider getCodeGeneratorProvider(){
        return applicationContext.getCodeGeneratorProvider();
    }

    public void setCodeGeneratorProvider(CodeGeneratorProvider codeGeneratorProvider){
        applicationContext.setCodeGeneratorProvider(codeGeneratorProvider);
    }

    @Override
    protected void loadDefinitions(ComponentRegistry registry) {
        throw new UnsupportedOperationException();
    }

}
