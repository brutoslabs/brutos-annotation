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

import org.brandao.brutos.ConfigurableApplicationContext;
import org.brandao.brutos.annotation.Controller;
import org.brandao.brutos.annotation.ControllerAlias;
import org.brandao.brutos.annotation.CustomAnnotation;

/**
 *
 * @author Brandao
 */
@CustomAnnotation(target=ControllerAlias.class,executeAfter=Controller.class)
public class ControllerAliasAnnotationConfig extends AbstractAnnotationConfig{

    public boolean isApplicable(Object source) {
        return source instanceof Class && 
               ((Class)source).isAnnotationPresent( ControllerAlias.class );
    }

    public Object applyConfiguration(Object source, Object builder, ConfigurableApplicationContext applicationContext) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
