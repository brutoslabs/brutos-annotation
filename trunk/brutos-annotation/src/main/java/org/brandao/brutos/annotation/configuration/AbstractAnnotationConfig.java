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
import org.brandao.brutos.annotation.AnnotationConfig;

/**
 *
 * @author Brandao
 */
public abstract class AbstractAnnotationConfig implements AnnotationConfig{

    private AnnotationConfigEntry annotation;
    
    public void setConfiguration(AnnotationConfigEntry annotation){
        this.annotation = annotation;
    }
    
    public Object applyInternalConfiguration(Object source, Object builder, 
            ConfigurableApplicationContext applicationContext) {

        List<AnnotationConfigEntry> list = annotation.getNextAnnotationConfig();

        for(int i=0;i<list.size();i++){
            AnnotationConfigEntry next = list.get(i);
            if(next.getAnnotationConfig().isApplicable(source))
                builder = next.getAnnotationConfig()
                        .applyConfiguration(source, builder, applicationContext);
        }
        
        return builder;
    }

}
