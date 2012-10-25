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

import org.brandao.brutos.interceptor.ConfigurableInterceptorHandler;
import org.brandao.brutos.mapping.Controller;

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
    Controller getController( ControllerManager controllerManager, ConfigurableInterceptorHandler handler );

    Controller getController( ControllerManager controllerManager, Class controllerClass );
}
