

package org.brandao.brutos;

import java.io.IOException;
import org.brandao.brutos.mapping.Action;
import org.brandao.brutos.mapping.ThrowableSafeData;
import org.brandao.brutos.scope.Scope;
import org.brandao.brutos.type.Type;


public abstract class AbstractRenderView implements RenderView{
    
    
    protected abstract void show( RequestInstrument requestInstrument,
            String view, DispatcherType dispatcherType )
                throws IOException;
 
    
    private void showView( RequestInstrument requestInstrument,
            String view, DispatcherType dispatcherType )
                throws IOException{
        requestInstrument.setHasViewProcessed(true);
        show(requestInstrument,view,dispatcherType);
    }

    
    private void showView( RequestInstrument requestInstrument,
            StackRequestElement stackRequestElement, Type type )
                throws IOException{
        requestInstrument.setHasViewProcessed(true);
        type
            .show(
                stackRequestElement.getHandler().getContext().getMvcResponse(), 
                stackRequestElement.getResultAction());
    }
    
    
    public void show( RequestInstrument requestInstrument,
            StackRequestElement stackRequestElement ) throws IOException{

        if( requestInstrument.isHasViewProcessed() )
            return;

        Scopes scopes = requestInstrument.getContext().getScopes();
        Scope requestScope = scopes.get(ScopeType.REQUEST.toString());
        
        Action method = 
                stackRequestElement.getAction() == null? 
                    null : 
                    stackRequestElement.getAction().getMethodForm();

        ThrowableSafeData throwableSafeData = 
                stackRequestElement.getThrowableSafeData();
        
        Object objectThrow = stackRequestElement.getObjectThrow();
        
        if( throwableSafeData != null ){
            if( throwableSafeData.getParameterName() != null )
                requestScope.put(
                    throwableSafeData.getParameterName(),
                    objectThrow);

            if( throwableSafeData.getView() != null ){
                this.showView(
                    requestInstrument,
                    throwableSafeData.getView(),
                    throwableSafeData.getDispatcher());
                return;
            }
        }

        if( stackRequestElement.getView() != null ){
            this.showView(requestInstrument, stackRequestElement.getView(),
                stackRequestElement.getDispatcherType());
            return;
        }

        if( method != null ){
            
            if( method.getReturnClass() != void.class ){
                String var =
                    method.getReturnIn() == null?
                        BrutosConstants.DEFAULT_RETURN_NAME :
                        method.getReturnIn();
                requestScope.put(var, stackRequestElement.getResultAction());
                
                if(method.isReturnRendered() || method.getReturnType().isAlwaysRender()){
                    this.showView(requestInstrument, stackRequestElement, method.getReturnType());
                    return;
                }
            }

            if( method.getView() != null ){
                this.showView(requestInstrument, method.getView(),
                        method.getDispatcherType());
                return;
            }
        }

        if( stackRequestElement.getController().getView() != null ){
            this.showView(requestInstrument,
                    stackRequestElement.getController().getView(),
                    stackRequestElement.getController().getDispatcherType());
        }
        else
        if( method != null && method.getReturnType() != null )
            this.showView(requestInstrument, stackRequestElement, method.getReturnType());

    }
    
}
