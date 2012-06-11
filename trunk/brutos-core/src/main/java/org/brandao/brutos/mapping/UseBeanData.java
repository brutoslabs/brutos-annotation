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

package org.brandao.brutos.mapping;

import org.brandao.brutos.BrutosException;
import org.brandao.brutos.Invoker;
import org.brandao.brutos.ScopeType;
import org.brandao.brutos.scope.Scope;
import org.brandao.brutos.Scopes;
import org.brandao.brutos.type.ArrayType;
import org.brandao.brutos.type.CollectionType;
import org.brandao.brutos.type.Type;
import org.brandao.brutos.validator.Validator;

/**
 *
 * @author Afonso Brandao
 */
public class UseBeanData {
    
    private String nome;
    
    private ScopeType scopeType;

    private Bean mapping;

    private Object staticValue;

    private Type type;

    private Validator validate;

    private boolean nullable;

    public UseBeanData() {
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Bean getMapping() {
        return mapping;
    }

    public void setMapping(Bean mapping) {
        this.mapping = mapping;
    }

    public Object getValue(){

        Object value = null;

        if( !isNullable() ){
            if( mapping != null )
                value = mapping.getValue(nome);
                //value = mapping.getValue();
            else
            if(staticValue!= null)
                value = type.convert( staticValue );
                //value = type.getValue( staticValue );
            else
            if( type instanceof CollectionType || type instanceof ArrayType ){
                value = nome == null? null : getScope().getCollection(nome);
                value = type.convert( value );
                //value = type.getValue( value );
            }
            else{
                value = nome == null? null : getScope().get(nome);
                value = type.convert( value );
                //value = type.getValue( value );
            }
        }

        if( validate != null )
            validate.validate(this, value);

        return value;
    }

    public Class getClassType(){
        //if( type != null )
            return type == null? null : type.getClassType();
        //else
        //if( mapping != null )
        //    return mapping.getClassType();
        //else
        //    return null;
    }
    /*
    public Object getValue( ServletContext context, HttpServletRequest request ) throws InstantiationException, IllegalAccessException, ParseException{
        if( scope == ScopeType.REQUEST ){
            if( mapping == null ){
                return type.getValue( request, context, nome == null? null : getValue( request, nome ) );
            }
            else
                return mapping.getValue( request );
        }
        else
        if( scope == ScopeType.SESSION ){
            if( mapping == null )
                //return type.getValue( request, context, nome == null? null : getValue( request.getSession(), nome ) );
                return getValue( request.getSession(), nome );
            else
                return mapping.getValue( request.getSession() );
        }
        else
        if( scope == ScopeType.APPLICATION ){
            if( mapping == null )
                //return type.getValue( request, context, nome == null? null : getValue( context, nome ) );
                return getValue( context, nome );
            else
                return mapping.getValue( request.getSession() );
        }
        else{
            if( mapping == null )
                return type.getValue( request, context, nome == null? null : getValue( context, nome ) );
            else
                return mapping.getValue( context );
        }
            
    }

    private Object getValue( HttpServletRequest request, String name ){
        if( type instanceof CollectionType || type instanceof ArrayType )
            return ((BrutosRequest)request).getObjects( name );
        else
            return ((BrutosRequest)request).getObject(name);
    }

    private Object getValue( ServletContext context, String name ){
        return context.getAttribute(name);
    }

    private Object getValue( HttpSession session, String name ){
        return session.getAttribute(name);
    }
    */
    
    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Scope getScope() {

        Scopes scopes = Invoker.getApplicationContext().getScopes();
        Scope objectScope = scopes.get( scopeType.toString() );

        if( objectScope == null )
            throw new BrutosException( "scope not allowed in context: " + scopeType );

        return objectScope;
    }

    public void setScopeType(ScopeType scope) {
        this.scopeType = scope;
    }

    public ScopeType getScopeType() {
        return this.scopeType;
    }

    public Validator getValidate() {
        return validate;
    }

    public void setValidate(Validator validate) {
        this.validate = validate;
    }

    public Object getStaticValue() {
        return staticValue;
    }

    public void setStaticValue(Object staticValue) {
        this.staticValue = staticValue;
    }

    public boolean isNullable() {
        return nullable;
    }

    public void setNullable(boolean nullable) {
        this.nullable = nullable;
    }
}