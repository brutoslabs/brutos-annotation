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

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

/**
 * Representa uma requisi��o.
 * 
 * @author Afonso Brandao
 */
public interface MvcRequest {

    /**
     * Obt�m um valor.
     * @param name Identifica��o
     * @return Valor.
     */
    public Object getValue( String name );

    /**
     * Obt�m uma propriedade.
     * @param name Identifica��o.
     * @return Propriedade.
     */
    public Object getProperty( String name );

    /**
     * Obt�m o stream da requisi��o.
     * @return Stream.
     * @throws IOException Lan�ado caso ocorra algum problema ao obter o stream.
     */
    public InputStream getStream() throws IOException;

    /**
     * Tipo da requisi��o. Normalmente usado em aplica��es web.
     * @return Tipo.
     */
    public String getType();

    /**
     * Obt�m o tamanho da requisi��o. Normalmente usado em aplica��es web.
     * @return Tamanho.
     */
    public int getLength();

    /**
     * Obt�m a codificado da requisi��o. Normalmente usado
     * em aplica��es web.
     * @return Codifica��o.
     */
    public String getCharacterEncoding();

    /**
     * Obt�m a localidade. Normalmente usado em aplica��es web.
     * @return Localidade.
     */
    public Locale getLocale();

}
