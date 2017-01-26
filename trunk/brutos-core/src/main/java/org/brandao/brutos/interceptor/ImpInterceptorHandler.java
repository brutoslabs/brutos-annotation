

package org.brandao.brutos.interceptor;

import java.text.ParseException;
import java.util.List;
import org.brandao.brutos.ApplicationContext;
import org.brandao.brutos.RequestInstrument;
import org.brandao.brutos.ResourceAction;
import org.brandao.brutos.StackRequestElement;
import org.brandao.brutos.mapping.Action;
import org.brandao.brutos.mapping.ParameterAction;
import org.brandao.brutos.mapping.UseBeanData;


public class ImpInterceptorHandler implements ConfigurableInterceptorHandler{
    
    private String URI;

    private String requestId;

    private ResourceAction resourceAction;

    private ApplicationContext context;
    
    private Object resource;

    private Object[] parameters;
    
    private Object result;
    
    private RequestInstrument requestInstrument;
    
    private StackRequestElement stackRequestElement;
    
    public ImpInterceptorHandler() {
    }

    public String getURI() {
        return URI;
    }

    public void setURI(String URI) {
        this.URI = URI;
        this.setRequestId(URI);
    }

    public ResourceAction getResourceAction() {
        return resourceAction;
    }

    public void setResourceAction(ResourceAction resourceAction) {
        this.resourceAction = resourceAction;
    }

    public Object getResource() {
        return resource;
    }

    public void setResource(Object resource) {
        this.resource = resource;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String requestId() {
        return this.requestId;
    }

    public ApplicationContext getContext(){
        return context;
    }

    public void setContext(ApplicationContext context) {
        this.context = context;
    }

    public void setParameters(Object[] value){
        this.parameters = value;
    }
    
    public Object[] getParameters() throws InstantiationException, 
        IllegalAccessException, ParseException{
        
        if(this.parameters == null){
            if(stackRequestElement.getParameters() == null){
                this.parameters = 
                        stackRequestElement
                                .getAction().getMethodForm()
                                .getParameterValues(this.resource);
            }
            else{
                this.parameters = 
                        stackRequestElement
                                .getAction().getMethodForm()
                                .getParameterValues(
                                        this.resource, 
                                        stackRequestElement.getParameters());
            }
        }
        
        return this.parameters;
    }

    public void setResult(Object value){
        this.result = value;
    }
    
    public Object getResult() {
        return this.result;
    }

    public void setRequestInstrument(RequestInstrument requestInstrument) {
        this.requestInstrument = requestInstrument;
    }

    public RequestInstrument getRequestInstrument() {
        return this.requestInstrument;
    }

    public void setStackRequestElement(StackRequestElement stackRequestElement) {
        this.stackRequestElement = stackRequestElement;
    }

    public StackRequestElement getStackRequestElement() {
        return this.stackRequestElement;
    }

}
