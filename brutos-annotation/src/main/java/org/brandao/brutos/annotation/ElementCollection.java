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

/**
 *
 * @author Brandao
 */
@Target({ElementType.METHOD,ElementType.PARAMETER,ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ElementCollection {
    
    Identify identify() default @Identify;
    
    Enumerated enumerated() default @Enumerated(EnumerationType.ORDINAL);
    
    Temporal temporal() default @Temporal("dd/MM/yyyy");
    
    Type type() default @Type(org.brandao.brutos.type.Type.class);
    
    org.brandao.brutos.annotation.Target target() default @org.brandao.brutos.annotation.Target(void.class);
    
    /*
    String bean() default "";
    
    String scope() default "param";

    boolean useMapping() default false;
    */
}
