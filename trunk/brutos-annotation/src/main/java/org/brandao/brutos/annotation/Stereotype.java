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

import java.lang.annotation.*;
import java.lang.annotation.Target;
import org.brandao.brutos.annotation.configuration.Converter;

/**
 * Usada para integrar um novo recurso. 
 * A classe que possui essa anotação deve implementar a interface
 * <a href="AnnotationConfig.html">org.brandao.brutos.annotation.AnnotationConfig</a>.
 * 
 * @author Brandao
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Stereotype {
    
    /**
     * Anotação alvo.
     */
    Class<? extends Annotation> target();
    
    /**
     * Define que esse recurso seja configurado após a 
     * configuração de um outro recurso em específico.
     */
    Class<? extends Annotation>[] executeAfter() default {};
    
    /**
     * Versão principal.
     */
    int majorVersion() default 1;
    
    /**
     * Versão secundária.
     */
    int minorVersion() default 0;
    
    /**
     * Converte as informações em um tipo desejado.
     */
    Class<? extends Converter> sourceConverter() default Converter.class;
    
}
