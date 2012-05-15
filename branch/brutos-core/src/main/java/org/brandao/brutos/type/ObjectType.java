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
import java.io.Serializable;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Afonso Brandao
 */
public class ObjectType implements Type{

    private Type serializableType;
    
    public ObjectType() {
        this.serializableType = Types.getType( Serializable.class );
    }

    @Override
    public Object getValue( HttpServletRequest request, ServletContext context, Object value ) {
        return value;
    }
    
    @Override
    public void setValue( HttpServletResponse response, ServletContext context, Object value ) throws IOException{
        serializableType.setValue(response, context, value);
    }
    
    @Override
    public Class getClassType() {
        return Object.class;
    }
    
}