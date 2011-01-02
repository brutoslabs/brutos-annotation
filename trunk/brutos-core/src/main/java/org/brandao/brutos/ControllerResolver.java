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

import javax.servlet.http.HttpServletRequest;
import org.brandao.brutos.interceptor.InterceptorHandler;
import org.brandao.brutos.mapping.Form;
import org.brandao.brutos.old.programatic.WebFrameManager;

/**
 * Interface usada na resolu��o de controladores.
 *
 * @author Afonso Brandao
 */
public interface ControllerResolver {

    /**
     * @deprecated
     * @param webFrameManager
     * @param request
     * @return
     */
    public Form getController( WebFrameManager webFrameManager, HttpServletRequest request );

    /**
     * Obt�m um controlador dependendo da requisi��o.
     * @param controllerManager Gestor dos controladores.
     * @param handler Mnipulador da requisi��o.
     * @return Controlador.
     */
    public Form getController( ControllerManager controllerManager, InterceptorHandler handler );
    
}
