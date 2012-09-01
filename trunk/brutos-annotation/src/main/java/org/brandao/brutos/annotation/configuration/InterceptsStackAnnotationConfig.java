/*
 * Brutos Web MVC http://www.brutosframework.com.br/
 * Copyright (C) 2009 Afonso Brandao. (afonso.rbn@gmail.com)
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

package org.brandao.brutos.annotation.configuration;

import java.util.List;
import org.brandao.brutos.ConfigurableApplicationContext;
import org.brandao.brutos.InterceptorManager;
import org.brandao.brutos.InterceptorStackBuilder;
import org.brandao.brutos.annotation.InterceptsStack;
import org.brandao.brutos.annotation.Param;
import org.brandao.brutos.annotation.Stereotype;
import org.brandao.brutos.annotation.configuration.converters.InterceptorStackConverter.InterceptorStackItem;
import org.brandao.brutos.mapping.Interceptor;
import org.brandao.brutos.mapping.StringUtil;

/**
 *
 * @author Brandao
 */
@Stereotype(target=InterceptsStack.class)
public class InterceptsStackAnnotationConfig extends AbstractAnnotationConfig{

    public boolean isApplicable(Object source) {
        return source instanceof InterceptorStackEntry;
    }

    public Object applyConfiguration(Object source, Object builder, 
            ConfigurableApplicationContext applicationContext) {
        
        InterceptorStackEntry stack = (InterceptorStackEntry)source;
        
        InterceptorManager interceporManager = 
                applicationContext.getInterceptorManager();
        
        String name = StringUtil.adjust(stack.getName());
        List<InterceptorStackItem> interceptors = stack.getInterceptors();
        
        InterceptorStackBuilder newBuilder = 
                interceporManager.addInterceptorStack(name, stack.isDefault());
        
        for(InterceptorStackItem i: interceptors){
            Interceptor in = interceporManager.getInterceptor(i.getType());
            newBuilder.addInterceptor(in.getName());
            Param[] params = i.getInfo().params();
            for(Param p: params){
                newBuilder.addParameter(StringUtil.adjust(p.name()), StringUtil.adjust(p.value()));
            }
        }
        super.applyInternalConfiguration(source, newBuilder, applicationContext);
        return builder;
    }
    
}
