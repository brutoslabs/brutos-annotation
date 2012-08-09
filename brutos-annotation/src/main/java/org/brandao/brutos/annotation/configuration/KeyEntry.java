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

import org.brandao.brutos.BrutosConstants;
import org.brandao.brutos.EnumerationType;
import org.brandao.brutos.ScopeType;
import org.brandao.brutos.annotation.KeyCollection;
import org.brandao.brutos.mapping.StringUtil;

/**
 *
 * @author Brandao
 */
public class KeyEntry implements BeanEntry{
    
    private String name;
    
    private ScopeType scope;
    
    private boolean useBean;
    
    private Class<?> classType;
    
    private Class<?> target;
    
    private EnumerationType enumerated;
    
    private String temporal;
    
    private Class<? extends org.brandao.brutos.type.Type> type;
    
    public KeyEntry(){
        this(null, null);
    }

    public KeyEntry(Class<?> classType, KeyCollection definition){
        this.classType = classType;
        if(definition != null){
            this.name = StringUtil.adjust(definition.bean());
            this.scope = 
                StringUtil.isEmpty(definition.scope())? 
                    null : 
                    ScopeType.valueOf(definition.scope());

            this.useBean = definition.useMapping();
            this.target = 
                    definition.target() == void.class?
                        null :
                        definition.target();

            this.enumerated = 
                StringUtil.isEmpty(definition.enumerated())? 
                    null : 
                    EnumerationType.valueOf(definition.scope());

            this.temporal = 
                    StringUtil.isEmpty(definition.temporal())?
                    null :
                    StringUtil.adjust(definition.temporal());

            this.type = definition.type() == org.brandao.brutos.type.Type.class?
                    null :
                    definition.type();
        }
        
    }

    public String getName() {
        return this.name == null? BrutosConstants.DEFAULT_KEY_NAME : name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ScopeType getScope() {
        return scope;
    }

    public void setScope(ScopeType scope) {
        this.scope = scope;
    }

    public boolean isUseBean() {
        return useBean;
    }

    public void setUseBean(boolean useBean) {
        this.useBean = useBean;
    }

    public Class<?> getClassType() {
        return this.target == null? classType : target;
    }

    public void setClassType(Class<?> classType) {
        this.classType = classType;
    }

    public Class<?> getTarget() {
        return target;
    }

    public void setTarget(Class<?> target) {
        this.target = target;
    }

    public EnumerationType getEnumerated() {
        return enumerated;
    }

    public void setEnumerated(EnumerationType enumerated) {
        this.enumerated = enumerated;
    }

    public String getTemporal() {
        return temporal;
    }

    public void setTemporal(String temporal) {
        this.temporal = temporal;
    }

    public Class<? extends org.brandao.brutos.type.Type> getType() {
        return type;
    }

    public void setType(Class<? extends org.brandao.brutos.type.Type> type) {
        this.type = type;
    }

    public Class getBeanType() {
        return this.getClassType();
    }
    
}