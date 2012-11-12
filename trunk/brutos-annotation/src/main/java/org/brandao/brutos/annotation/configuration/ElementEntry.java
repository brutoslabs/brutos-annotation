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

import java.lang.reflect.Type;
import org.brandao.brutos.BrutosConstants;
import org.brandao.brutos.EnumerationType;
import org.brandao.brutos.ScopeType;
import org.brandao.brutos.annotation.ElementCollection;
import org.brandao.brutos.mapping.StringUtil;

/**
 *
 * @author Brandao
 */
public class ElementEntry implements BeanEntry{
    
    private String name;
    
    private ScopeType scopeType;
    
    private Boolean useBean;
    
    private Class<?> classType;
    
    private Class<?> target;
    
    private Type genericType;
    
    private EnumerationType enumerated;
    
    private String temporal;
    
    private Class<? extends org.brandao.brutos.type.Type> type;
    
    public ElementEntry(){
        this(null, null, null);
    }

    public ElementEntry(Class<?> classType, Type genericType, ElementCollection definition){
        this.classType = classType;
        this.genericType = genericType;
        if(definition != null){
            this.name = StringUtil.adjust(definition.bean());
            this.scopeType = 
                StringUtil.isEmpty(definition.scope())? 
                    null : 
                    ScopeType.valueOf(definition.scope());

            this.useBean = definition.useMapping();
            this.target = 
                    definition.target() == void.class?
                        null :
                        definition.target();

            this.enumerated = 
                    EnumerationType.valueOf(definition.enumerated().name().toLowerCase());

            this.temporal = 
                    StringUtil.isEmpty(definition.temporal())?
                    BrutosConstants.DEFAULT_TEMPORALPROPERTY :
                    StringUtil.adjust(definition.temporal());

            this.type = definition.type() == org.brandao.brutos.type.Type.class?
                    null :
                    definition.type();
        }
        else{
            this.name = null;
            this.scopeType = BrutosConstants.DEFAULT_SCOPETYPE;
            this.useBean = null;
            this.target = null;
            this.enumerated = 
                    EnumerationType.valueOf(BrutosConstants.DEFAULT_ENUMERATION_TYPE);

            this.temporal = BrutosConstants.DEFAULT_TEMPORALPROPERTY;
            this.type = null;
        }
        
    }

    public String getName() {
        return this.name == null? BrutosConstants.DEFAULT_ELEMENT_NAME : name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ScopeType getScopeType() {
        return scopeType;
    }

    public void setScopeType(ScopeType scopeType) {
        this.scopeType = scopeType;
    }

    public Boolean isUseBean() {
        return useBean;
    }

    public void setUseBean(boolean useBean) {
        this.useBean = useBean;
    }

    public Class<?> getClassType() {
        return classType;
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

    public Type getGenericType() {
        return genericType;
    }

    public void setGenericType(Type genericType) {
        this.genericType = genericType;
    }
    
}
