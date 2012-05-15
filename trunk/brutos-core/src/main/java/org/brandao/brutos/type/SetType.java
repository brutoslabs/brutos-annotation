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
import java.util.Set;
import org.brandao.brutos.BrutosException;
import org.brandao.brutos.ConfigurableApplicationContext;
import org.brandao.brutos.Invoker;
import org.brandao.brutos.web.http.ParameterList;

/**
 * Allows the creation of type Set.
 * <p>The object type is determined by the org.brandao.brutos.type.set,
 * if not informed the type is java.util.HashSet.</p>
 * <p>If the value parameter of the method SetType.getValue() is an
 * instance of the org.brandao.brutos.http.ParameterList then their
 * values should be converted, otherwise there is no need for conversion.</p>
 * 
 * @author Afonso Brandao
 */
public class SetType implements CollectionType{

    private Class listType;
    private Class type;
    private Type primitiveType;
    private Type serializableType;

    public SetType(){
    }
    
    public void setGenericType(Object classType) {
        Class collectionType = Types.getCollectionType(classType);
        if( collectionType != null ){
            this.type = collectionType;
            this.primitiveType = Types.getType( this.type );
            if( this.primitiveType == null )
                throw new UnknownTypeException( classType.toString() );
        }
        else
            throw new UnknownTypeException( "is not allowed the use the Set or Set<?>" );
    }

    public Object getGenericType(){
        return this.type;
    }

    /*
    @Override
    public Object getValue(HttpServletRequest request, ServletContext context, Object value) {
        //Se value for instancia de ParameterList significa que
        //os dados ainda nao foram processados.
        if( value instanceof ParameterList )
            return getList(request, context, value);
            
        else
            return value;
    }

    private Set getList(HttpServletRequest request, ServletContext context, Object value){
        try{
            Set objList = this.listType.newInstance();
            
            for( Object o: (ParameterList)value )
                objList.add( this.primitiveType.getValue(request, context, o) );

            return objList;
        }
        catch( Exception e ){
            throw new BrutosException( e );
        }
    }

    @Override
    public void setValue(HttpServletResponse response, ServletContext context, Object value) throws IOException {
        this.serializableType.setValue( response, context, value );
    }
    */
    
    public Class getClassType() {
        return Set.class;
    }

    public Object getValue(Object value) {
        if( value instanceof ParameterList )
            return getList(value);

        else
            return value;
    }

    public void setValue(Object value) throws IOException {
        this.serializableType.setValue( value );
    }

    private Class getListType(){

        if( this.listType != null )
            return this.listType;

        ConfigurableApplicationContext context =
                (ConfigurableApplicationContext)Invoker.getApplicationContext();

        String className = context
                .getConfiguration()
                    .getProperty( "org.brandao.brutos.type.set",
                                  "java.util.HashSet" );

        try{
            this.listType = (Class)
                    Class.forName( className, true,
                                Thread.currentThread().getContextClassLoader());
        }
        catch( Exception e ){
            throw new BrutosException( e );
        }

        this.serializableType = Types.getType( Serializable.class );

        return this.listType;
    }

    private Set getList(Object value){
        try{
            Set objList = (Set) this.getListType().newInstance();

            ParameterList list = (ParameterList)value;
            int size = list.size();
            //for( Object o: (ParameterList)value )
            for( int i=0;i<size;i++ ){
                Object o = list.get(i);
                objList.add( this.primitiveType.getValue(o) );
            }
            return objList;
        }
        catch( Exception e ){
            throw new BrutosException( e );
        }
    }

}