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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.brandao.brutos.mapping.MethodForm;

/**
 * Representa uma a��o.
 *
 * @author Afonso Brandao
 */
public interface ResourceAction {

    /**
     * Invoca o m�todo associado a a��o.
     * @param source Objeto a ter o m�todo invocado.
     * @param args Argumentos necess�rios para invocar o m�todo.
     * @return Resultado obtido da invoca��o do m�todo.
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     */
    public Object invoke( Object source, Object[] args )
            throws IllegalAccessException, IllegalArgumentException,
                InvocationTargetException;

    /**
     * Obt�m o mapeamento da a��o.
     * @return M�todo.
     */
    public MethodForm getMethodForm();

    /**
     * Obt�m o m�todo associado a a��o.
     * @return M�todo.
     */
    public Method getMethod();

    /**
     * Obt�m o tipo de retorno da a��o.
     * @return Tipo.
     */
    public Class returnType();

    /**
     * Obt�m os argumentos necess�rios para invocar o m�todo.
     * @return Matriz de tipos.
     */
    public Class[] getParametersType();

    /**
     * Obt�m a classe propriet�ria do m�todo associado a a��o.
     * @return Classe.
     */
    public Class getResourceClass();

    /**
     * Verifica se � uma a��o abstrata.
     * @return Verdadeiro se existe um m�todo associado a a��o, caso
     * contr�rio falso.
     */
    public boolean isAbstract();
}
