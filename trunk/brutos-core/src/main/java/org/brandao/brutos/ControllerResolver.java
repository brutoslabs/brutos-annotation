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
import org.brandao.brutos.mapping.Controller;
import org.brandao.brutos.mapping.MethodForm;

/**
 * Interface usada na resolução de controladores.
 *
 * @author Afonso Brandao
 */
public interface ControllerResolver {

    /*
     * @deprecated
     * @param webFrameManager
     * @param request
     * @return .
     */
    //public Form getController( WebFrameManager webFrameManager, HttpServletRequest request );

    /**
     * Obtém um controlador de acordo com a requisição.
     * @param controllerManager Gestor dos controladores.
     * @param handler Manipulador da requisição.
     * @return Controlador.
     */
    Controller getController( ControllerManager controllerManager, InterceptorHandler handler );

    /**
     * Obtém a identificação do controlador.
     * @param controller Controlador
     * @return Identificação.
     */
    String getControllerId(Controller controller, MethodForm action);
    
}
