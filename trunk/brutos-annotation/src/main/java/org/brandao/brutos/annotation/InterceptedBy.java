/*
 * Brutos Web MVC http://www.brutosframework.com.br/
 * Copyright (C) 2009-2017 Afonso Brandao. (afonso.rbn@gmail.com)
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
 * Define os interceptadores de um controlador.
 * <pre>
 * Ex:
 * &#064;InterceptedBy(
 *    &#064;Intercept(
 *       interceptor=MyInterceptor.class,
 *       params={
 *          &#064;Param(name="name1",value="value1"),
 *          &#064;Param(name="name2",value="value2")
 *       }
 *    )
 * )
 * public class MyController{
 * 
 *    ...
 * 
 * }
 * </pre>
 * @author Afonso Brandao
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface InterceptedBy {

    /**
     * Coleção de interceptadores.
     */
    Intercept[] value();
    
}
