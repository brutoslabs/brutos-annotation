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

package org.brandao.brutos;

import java.util.Properties;

/**
 * Provê os objetos da aplicação.
 * 
 * @author Brandão
 */
public interface ObjectFactory {
    
    /**
     * Obtém um objeto a partir de seu nome.
     * @param name Nome que identifica o objeto.
     * @return Objeto.
     */
    Object getBean( String name );
    
    /**
     * Obtém um objeto a partir de seu classe.
     * @param clazz Classe do objeto.
     * @return Objecto.
     */
    Object getBean( Class clazz );

    /**
     * Aplica a configuração da aplicação.
     * @param properties Configuração da aplicação.
     */
    void configure( Properties properties );

    /**
     * Desliga o container  IoC.
     */
    void destroy();
    
}