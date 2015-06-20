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

package org.brandao.brutos.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.brandao.brutos.BrutosConstants;

/**
 * Especifica os elementos de uma coleção de "beans".
 * 
 * <pre>
 * Ex1:
 * public class MyController{
 * 
 *    &#064;ElementCollection(bean="myElement", useMapping=true)
 *    private Map&lt;String,BeanConstructorTest&gt; property;
 * 
 *    ...
 * 
 * }
 * 
 * Ex2:
 * public class MyController{
 * 
 *    &#064;ElementCollection(bean="myElement")
 *    private List&lt;Integer&gt; property;
 * 
 *    ...
 * 
 * }
 * 
 * </pre>
 * 
 * @author Brandao
 */
@Target({ElementType.METHOD,ElementType.PARAMETER,ElementType.FIELD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ElementCollection {
    
    /**
     * Identificação do "bean" que será injetado.
     * Caso seja omitido, será usado "element".
     * 
     */
    String bean() default "element";
    
    /**
     * Escopo do valor a ser injetado. Os escopos estão 
     * descritos em {@link ScopeType}.
     * 
     */
    String scope() default "param";

    /**
     * Define o tipo de mapeamento do bean.
     */
    MappingTypes mappingType() default MappingTypes.AUTO;

    /**
     * Classe alvo do mapeamento.
     */
    Class<?> target() default void.class;
    
    /**
     * Usado em tipos enum. Os valores estão 
     * descritos em {@link EnumerationType}.
     */
    EnumerationType enumerated() default EnumerationType.ORDINAL;
    
    /**
     * Usado em tipos {@link java.util.Date} e {@link java.util.Calendar}.
     * Deve seguir o padrão definido em {@link java.text.SimpleDateFormat}.
     */
    String temporal() default BrutosConstants.DEFAULT_TEMPORALPROPERTY;
    
    /**
     * Permite definir múltiplos tipos de elementos.
     */
    Any any() default @Any(metaBean = @Basic);
    
    /**
     * Define o uso de um tipo específico de dados.
     */
    Class<? extends org.brandao.brutos.type.Type> type() default org.brandao.brutos.type.Type.class;
    
}
