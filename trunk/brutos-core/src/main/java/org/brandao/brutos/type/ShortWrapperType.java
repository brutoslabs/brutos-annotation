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

package org.brandao.brutos.type;

import java.io.IOException;
import org.brandao.brutos.ConfigurableApplicationContext;
import org.brandao.brutos.Invoker;
import org.brandao.brutos.MvcResponse;

/**
 *
 * @author Afonso Brandao
 */
public class ShortWrapperType implements Type{

    public ShortWrapperType() {
    }
    
    public Class getClassType() {
        return Short.class;
    }

    public Object getValue(Object value) {
        if( value instanceof Double )
            return value;
        else
        if( value instanceof String )
            return Short.valueOf((String)value);
        else
        if( value == null )
            return null;
        else
            throw new UnknownTypeException();
    }

    public void setValue(Object value) throws IOException {
        ConfigurableApplicationContext app =
                (ConfigurableApplicationContext)Invoker.getApplicationContext();
        MvcResponse response = app.getMvcResponse();
        response.process(value);
    }
    
}