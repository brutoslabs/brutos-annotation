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

import java.util.*;
import org.brandao.brutos.logger.Logger;
import org.brandao.brutos.logger.LoggerProvider;
import org.brandao.brutos.mapping.Interceptor;
import org.brandao.brutos.mapping.InterceptorStack;

/**
 * Classe usada para criar interceptadores ou pilhas de interceptadores.
 * O uso de interceptadores é necessário quando precisamos executar tarefas
 * antes e/ou depois do controlador ser executado, normalmente usado para
 * controle de acesso, validação de dados, controle de transação e geração
 * de log.
 * <p>É possível trabalhar com um ou mais interceptadores, podendo definir
 * quais recursos serão interceptados, a ordem com que serão executados e os
 * parãmetros necessários para a sua configuração.</p>
 * <p>Sua instância é controlada pelo container IOC, podendo assim, receber a
 * injeção por construtor ou método.</p>
 *
 * <pre>
 * Ex:
 *
 * public class MyInterceptor extends AbstractInterceptor{
 *
 *     public void intercepted( InterceptorStack stack,
 *        InterceptorHandler handler ) throws InterceptedException{
 *         ...
 *     }
 *
 * }
 * interceptorManager
 *     .addInterceptor( "myInterceptorName", MyInterceptor.class, false );
 * </pre>
 * 
 * <pre>
 * Ex2:
 * 
 * interceptorManager
 *     .addInterceptor( "myInterceptorName", MyInterceptor.class, false );
 * interceptorManager
 *     .addInterceptorStack( "myStack", false )
 *     .addInterceptor( "myInterceptorName" );
 * </pre>
 *
 * @author Afonso Brandao
 */
public class InterceptorManager {
    
    private Map interceptors;
    private Map reverseInterceptors;
    private List defaultInterceptors;
    private InterceptorManager parent;
    
    public InterceptorManager() {
        this(null);
    }

    public InterceptorManager(InterceptorManager parent) {
        this.interceptors = new HashMap();
        this.reverseInterceptors = new HashMap();
        this.defaultInterceptors = new ArrayList();
        this.parent = parent;
    }
    
    /**
     * Cria uma pilha de interceptadores com uma determinada identificação.
     * @param name Identificação da pilha de interceptadores.
     * @param isDefault Se verdadeiro, todos os controladores e suas ações serão
     * interceptadas.
     * @return Construtor da pilha de interceptadores.
     */
    public InterceptorStackBuilder addInterceptorStack( String name, boolean isDefault ){
        Interceptor in;
        
        name = name == null || name.length() == 0? null : name;
        
        if( interceptors.containsKey( name ) )
            throw new BrutosException( "conflict interceptor name: " + name );
        
        if( name == null )
            throw new BrutosException( "interceptor name is required!" );
        
        in = new InterceptorStack();
        
        if( isDefault )
            defaultInterceptors.add( in );
        
        in.setName( name );
        in.setDefault( isDefault );
        in.setProperties( new HashMap() );
        interceptors.put( name, in );
        
        return new InterceptorStackBuilder( in, this );
    }
    
    /**
     * Cria um interceptador com uma determinada identificação.
     * @param name Identificação do interceptador.
     * @param isDefault Se verdadeiro, todos os controladores e suas ações serão
     * interceptadas.
     * @return Construtor do interceptador.
     */
    public InterceptorBuilder addInterceptor( String name, Class interceptor, boolean isDefault ){
        Interceptor in;
        
        name = name == null || name.length() == 0? null : name;
        
        if( interceptors.containsKey( name ) )
            throw new BrutosException( "conflict interceptor name: " + name );
        
        if( interceptor == null )
            throw new BrutosException( "interceptor class is required!" );
        
        if( name == null )
            throw new BrutosException( "interceptor name is required!" );
        
        if( !org.brandao.brutos.interceptor.Interceptor.class.isAssignableFrom( interceptor ) )
            throw new BrutosException( "is not a interceptor: " + interceptor.getName() );
        
        in = new Interceptor();
        
        if( isDefault )
            defaultInterceptors.add( in );
        
        in.setType( interceptor );
        in.setName( name );
        in.setProperties( new HashMap() );
        in.setDefault( isDefault );
        interceptors.put( name, in );
        reverseInterceptors.put(interceptor, in);
        
        getLogger().info("created interceptor: " + interceptor.getName());
        return new InterceptorBuilder( in, this );
        
    }

    /**
     * Obtém um interceptador a partir do nome.
     * @param name Identificação do interceptador.
     * @return Mapeamento.
     */
    public Interceptor getInterceptor( String name ){
        if( !interceptors.containsKey( name ) ){
            if(parent != null)
                return (Interceptor) parent.getInterceptor( name );
            else
                throw new BrutosException( "interceptor not found: " + name );
        }
        else
            return (Interceptor) interceptors.get( name );
    }

    /**
     * Obtém um interceptador a partir de sua classe.
     * @param clazz Classe do interceptador.
     * @return Mapeamento.
     */
    public Interceptor getInterceptor( Class clazz ){
        if( !reverseInterceptors.containsKey( clazz ) ){
            if(parent != null)
                return (Interceptor) parent.getInterceptor( clazz );
            else
                throw new BrutosException( "interceptor not found: " + clazz.getName() );
        }
        else
            return (Interceptor) reverseInterceptors.get( clazz );
    }
    
    /**
     * Obtém os interceptadores globais.
     * @return Interceptadores globais.
     */
    public List getDefaultInterceptors(){
        List tmp;
        
        if(parent != null){
            tmp = new ArrayList(parent.defaultInterceptors);
            tmp.addAll(this.defaultInterceptors);
        }
        else
            tmp = this.defaultInterceptors;
            
        return Collections.unmodifiableList(tmp);
    }
    
    /**
     * Define o gestor de interceptador associado ao atual.
     * @param parent Gestor de interceptador.
     */
    public void setParent(InterceptorManager parent){
        this.parent = parent;
    }
    
    /**
     * Obtém o gestor de interceptador associado ao atual.
     * @result Gestor de interceptador.
     */
    public InterceptorManager getParent(){
        return this.parent;
    }
    
    protected Logger getLogger(){
        return LoggerProvider.getCurrentLoggerProvider()
                .getLogger(InterceptorManager.class);
    }
    
}
