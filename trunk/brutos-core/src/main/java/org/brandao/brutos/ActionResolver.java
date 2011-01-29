/*
 * Brutos Web MVC http://brutos.sourceforge.net/
 * Copyright (C) 2009 Afonso Brandao. (afonso.rbn@gmail.com)
 *
 * This library is free software. You can redistribute it 
 * and/or modify it under the terms of the GNU General Public
 * License (GPL) version 3.0 or (at your option) any later 
 * version.
 * You may obtain a copy of the License at
 * 
 * http://www.gnu.org/licenses/gpl.html 
 * 
 * Distributed WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied.
 *
 */

package org.brandao.brutos;

import org.brandao.brutos.interceptor.InterceptorHandler;
import org.brandao.brutos.mapping.Form;

/**
 * Essa interface � usada para identificar a a��o a ser executada em um
 * determinado controlador. Toda classe que tem essa fun��o deve implementar
 * essa interface.
 * 
 * @author Afonso Brandao
 */
public interface ActionResolver {

    /**
     * Obt�m a a��o a ser executada.
     * @param controller Controlador.
     * @param handler Mnipulador da requisi��o.
     * @return A��o a ser executada.
     */
    public ResourceAction getResourceAction( Form controller, InterceptorHandler handler );

}
