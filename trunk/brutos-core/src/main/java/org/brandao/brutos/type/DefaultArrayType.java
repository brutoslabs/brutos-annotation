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
import java.lang.reflect.Array;
import org.brandao.brutos.BrutosException;
import org.brandao.brutos.ConfigurableApplicationContext;
import org.brandao.brutos.Invoker;
import org.brandao.brutos.MvcResponse;
import org.brandao.brutos.web.http.ParameterList;

/**
 *
 * @author Afonso Brandao
 */
public class DefaultArrayType implements ArrayType{

    private org.brandao.brutos.type.Type componentType;
    private org.brandao.brutos.type.Type serializableType;
    private Class classType;
    private Class arrayComponentType;

    public DefaultArrayType(){
        this.serializableType = Types.getType( Serializable.class );
    }

    public void setContentType(Class type) {
        this.arrayComponentType = type;
        this.componentType = Types.getType( type );
   }

    public Class getClassType() {
        return this.classType;
    }

    private Object getList(Object value){
        try{
            ParameterList param = (ParameterList)value;
            Object objList = Array.newInstance( arrayComponentType , param.size() );

            for( int i=0;i<param.size();i++ )
                Array.set(
                    objList,
                    i,
                    componentType.getValue( param.get( i ) )
                    //componentType.getValue(request, context, param.get( i ) )
                );

            return objList;
        }
        catch( Exception e ){
            throw new BrutosException( e );
        }
    }

    public void setClassType(Class classType) {
        this.classType = classType;
    }

    public Object getValue(Object value) {
        if( value instanceof ParameterList )
            return getList(value);

        else
            return value;
    }

    public void setValue(Object value) throws IOException {
        ConfigurableApplicationContext app =
                (ConfigurableApplicationContext)Invoker.getApplicationContext();
        MvcResponse response = app.getMvcResponse();
        response.process(value);
    }

}