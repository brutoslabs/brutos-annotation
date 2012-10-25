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

/**
 * 
 * @author Brandao
 */
public class DispatcherView {

    private DispatcherType dispatcher;

    public DispatcherView(){
        this(null);
    }
    
    public DispatcherView(DispatcherType dispatcher){
        this.dispatcher = dispatcher;
    }

    /**
     * O fluxo é alterado para a visão.
     * @param value Visão.
     */
    public void to( String value ){
        StackRequestElement request = 
                Invoker.getInstance().getStackRequest().getCurrent();
        request.setDispatcherType(dispatcher);
        request.setView(value);
    }

    /**
     * O fluxo é alterado para a visão.
     * @param value Visão.
     */
    public void toRedirectNow( String value ){
        throw new RedirectException( value, dispatcher );
    }

}
