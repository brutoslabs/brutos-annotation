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


package org.brandao.brutos.annotation.configuration;

import org.brandao.brutos.BrutosException;
import org.brandao.brutos.ClassUtil;
import org.brandao.brutos.ComponentRegistry;
import org.brandao.brutos.annotation.ExtendedScope;
import org.brandao.brutos.annotation.Stereotype;
import org.brandao.brutos.mapping.StringUtil;
import org.brandao.brutos.scope.Scope;

/**
 *
 * @author Brandao
 */
@Stereotype(target=ExtendedScope.class)
public class ExtendedScopeAnnotationConfig extends AbstractAnnotationConfig{

    public boolean isApplicable(Object source) {
        return source instanceof Class && AnnotationUtil.isScope((Class)source);
    }

    public Object applyConfiguration(Object source, Object builder, ComponentRegistry componentRegistry) {
        try{
            return this.applyConfiguration0(source, builder, componentRegistry);
        }
        catch(Exception e){
            throw 
                new BrutosException(
                        "can't create new scope",
                        e );
        }
    }

    public Object applyConfiguration0(Object source, Object builder, ComponentRegistry componentRegistry) {
        
        Class sourceClass = (Class)source;
        ExtendedScope extendedScope = 
            (ExtendedScope) sourceClass.getAnnotation(ExtendedScope.class);
        
        String name = extendedScope.value();
        name = 
            name == null? 
                StringUtil.toCamelCase(sourceClass.getSimpleName().replaceAll("Scope$", "")) :
                name;
        if(!Scope.class.isAssignableFrom(sourceClass))
            throw new BrutosException(sourceClass.getName() + " must implement " + Scope.class.getName());
        
        try{
            Scope scope = (Scope) ClassUtil.getInstance(sourceClass);
            componentRegistry.registerScope(name, scope);
            return scope;
        }
        catch( Exception e ){
            throw new BrutosException( e );
        }
        
    }
            
}
