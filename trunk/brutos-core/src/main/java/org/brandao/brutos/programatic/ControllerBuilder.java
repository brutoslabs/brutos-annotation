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

package org.brandao.brutos.programatic;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.brandao.brutos.BrutosException;
import org.brandao.brutos.Configuration;
import org.brandao.brutos.DispatcherType;
import org.brandao.brutos.EnumerationType;
import org.brandao.brutos.ScopeType;
import org.brandao.brutos.bean.BeanInstance;
import org.brandao.brutos.mapping.CollectionMapping;
import org.brandao.brutos.mapping.FieldForm;
import org.brandao.brutos.mapping.Form;
import org.brandao.brutos.mapping.Interceptor;
import org.brandao.brutos.mapping.InterceptorStack;
import org.brandao.brutos.mapping.MapMapping;
import org.brandao.brutos.mapping.MappingBean;
import org.brandao.brutos.mapping.MethodForm;
import org.brandao.brutos.mapping.ThrowableSafeData;
import org.brandao.brutos.mapping.UseBeanData;
import org.brandao.brutos.type.*;
import org.brandao.brutos.validator.ValidatorProvider;

/**
 * Classe usada para contruir um controlador.
 * Com essa classe � poss�vel incluir a��es, atribuir multiplas identifica��es
 * ao controlador, capturar exce��es e processa-las, definir uma a��o padr�o, determinar
 * os interceptadores do controlador, criar mapeamentos de beans, configurar as
 * propriedades do controlador e obter a classe que representa o controlador.
 *
 * <p>A inclus�o de uma a��o � feita usando o m�todo addAction(...). � possivel
 * definir sua identifica��o, o m�todo respons�vel pelo seu processamento,
 * identifica��o do objeto resultante da a��o. Al�m de ser poss�vel definir a vis�o
 * e como seu fluxo ser� direcionado para a vis�o.</p>
 *
 * Ex:
 *
 * <pre>
 * public class Controller{
 *
 *     public String savePerson( Person o ){
 *         ...
 *         this.person = o;
 *         return o.isPresist()? "OK" :  "ERROR";
 *     }
 * }
 *
 * controllerBuilder.addAction( "addPerson", "result", "showPerson.jsp",
 *     DispatcherType.FORWARD, "savePerson" );
 *
 * ...
 * </pre>
 *
 * <p>Para atribuir novas identifi��es ao controlador � usado o m�todo addAlias(...).
 * No exemplo abaixo o controlador estar� associado a duas identifica��es.
 * Essas Identifica��es s�o "/index.jbrs" e "/default.jbrs".</p>
 *
 * Ex:
 * <pre>
 * ...
 *   controllerBuilder.addAlias( "/index.jbrs" );
 *   controllerBuilder.addAlias( "/default.jbrs" );
 * ...
 * </pre>
 *
 * A captura de exce��es que podem ser lan�adas ao executar alguma a��o � configurada
 * pelo m�todo addThrowable(...). No exemplo asseguir, se for lan�ada uma exce��o
 * LoginException, o fluxo ser� alterado para o controlador "/login.jbrs" e
 * possuir� a identifica��o loginException.
 *
 * Ex:
 * 
 * <pre>
 * controlerBuilder
 *   .addThrowable( LoginException.class, "/login.jbrs", "loginException", DispatcherType.FORWARD );
 * </pre>
 *
 * <p>A a��o padr�o � definida usando o m�todo setDefaultAction(...). Essa a��o �
 * executada se o controlador for chamado sem que seja determinada a execu��o de alguma a��o.
 * A a��o deve ser previamente definida.</p>
 *
 * Ex;
 * <pre>
 * controllerBuilder.addAction( "show", "result", "show.jsp",
 *     DispatcherType.INCLUDE, "showPerson" );
 * controlerBuilder.setDefaultAction( "show" );
 * </pre>
 *
 * <p>Para definir os interceptadores do controlador � usando o m�todo addInterceptor(...).
 * Al�m de determinar os interceptadores � poss�vel fazer uma configura��o espec�fica
 * para um determinado controlador.</p>
 *
 * Ex:
 * 
 * <pre>
 * controllerBuilder.addInterceptor( "loginInterceptor" )
 *  .addParameter( "excludeMethods", "index" );
 * </pre>
 *
 * <p>O mapeamento de beans permite injetar os dados obtidos da requisi��o, sess�o,
 * contexto ou de qualquer outro escopo nas propriedades do bean. A configura��o do
 * mapeamento � feita usando o m�todo buildMappingBean(...).</p>
 *
 * Ex:
 * 
 * <pre>
 * BeanBuilder beanBuilder = controllerBuilder.buildMappingBean( "myBeanMapping", MyBean.class );
 * beanBuilder
 *   .addProperty( "name-client", "name", ScopeType.REQUEST )
 *   .addProperty( "country-client", "country", ScopeType.REQUEST )
 *   .addProperty( "currentUser", "currentUser", ScopeType.SESSION );
 * </pre>
 *
 * <p>As propriedades do controlador s�o configuradas pelo m�todo addProperty(...).</p>
 *
 * Ex:
 * <pre>
 * controllerBuilder.addProperty( "user", "user", ScopeType.SESSION );
 * </pre>
 * 
 * @author Afonso Brandao
 */
public class ControllerBuilder {
    
    private Form controller;
    private ControllerManager controllerManager;
    private InterceptorManager interceptorManager;
    private ValidatorProvider validatorProvider;

    /**
     * Constroi um novo controlador.
     * 
     * @param controller 
     * @param controllerManager
     * @param interceptorManager
     * @param validatorProvider
     */
    public ControllerBuilder( Form controller, ControllerManager controllerManager, 
            InterceptorManager interceptorManager, ValidatorProvider validatorProvider ) {
        this.controller = controller;
        this.controllerManager  = controllerManager;
        this.interceptorManager = interceptorManager;
        this.validatorProvider  = validatorProvider;
    }

    /**
     * Atribui uma nova identifica��o ao controlador.
     * 
     * @param id Nova identifica��o.
     * @return Construtor do controlador.
     */
    public ControllerBuilder addAlias( String id ){

        id = id == null || id.replace( " ", "" ).length() == 0? null : id;

        if( id == null )
            throw new NullPointerException();

        controller.addAlias(id);
        controllerManager.addForm(id, controller);
        return this;
    }

    /**
     * Intercepta e atribui uma identifica��o a uma determinada exce��o.
     *
     * @param target Exce��o alvo.
     * @param id Identifica��o.
     * @return Contrutos do controlador.
     */
    public ControllerBuilder addThrowable( Class target, String id ){
        return addThrowable( target, null, id, DispatcherType.FORWARD );
    }

    /**
     * Intercepta e atribui uma identifica��o a uma determinada exce��o. O
     * objeto resultante da exce��o pode ser usando na vis�o.
     *
     * @param target Exce��o alvo.
     * @param view Vis�o. Se omitido, ser� usado a vis�o do controlador.
     * @param id Identifica��o.
     * @param dispatcher Modo como ser� direcionado o fluxo para a vis�o.
     * @return Contrutor do controlador.
     */
    public ControllerBuilder addThrowable( Class target, String view, String id, DispatcherType dispatcher ){

        view =
            view == null || view.replace( " ", "" ).length() == 0?
                null :
                view;
        
        id =
            id == null || id.replace( " ", "" ).length() == 0?
                null :
                id;

        if( target == null )
            throw new BrutosException( "target is required: " + controller.getClassType().getName() );

        if( !Throwable.class.isAssignableFrom( target ) )
            throw new BrutosException( "target is not allowed: " +target.getName() );

        ThrowableSafeData thr = new ThrowableSafeData();
        thr.setParameterName(id);
        thr.setTarget(target);
        thr.setUri(view);
        thr.setRedirect( false );
        thr.setDispatcher( dispatcher );
        controller.setThrowsSafe(thr);
        return this;
    }

    /**
     * Define um a��o a ser executado caso o controlador seja chamado sem que seja 
     * determinada a execu��o de alguma a��o.
     * 
     * @param id Identifica��o da ��o.
     * @return Contrutor do controlador.
     */
    public ControllerBuilder setDefaultAction( String id ){

        id =
            id == null || id.replace( " ", "" ).length() == 0?
                null :
                id;
        
        if( id != null ){
            /*
             * Agora � permitido que exista uma acao sem um metodo
            if( !action.getMethods().containsKey( name ) )
                throw new BrutosException( "method " + name + " not found: " +
                        webFrame.getClassType().getName() );
            else
             */
                controller.setDefaultMethodName( id );
        }
        return this;
    }

    /**
     * Gera o mapeamento de um bean. Esse mapeamento � usado para converter e injetar os
     * dado da requisi��o ou de qualquer outro escopo nas propriedades do bean.
     * 
     * @param name Nome do mapeamento.
     * @param target Alvo do mapeamento.
     * @return Construtor do mapeamento.
     * @throws java.lang.NullPointerException Lan�ado se target for igual a null.
     * @throws org.brandao.brutos.BrutosException Lan�ado se o target for uma
     * cole��o ou um mapeamento.
     */
    public BeanBuilder buildMappingBean( String name, Class target ){

        if( target == null )
            throw new NullPointerException();

        name =
            name == null || name.replace( " ", "" ).length() == 0?
                null :
                name;
        
        if( name == null )
            throw new BrutosException( "name is required: " +
                    controller.getClassType().getName() );
            
        if( target == null )
            throw new BrutosException( "target is required: " +
                    controller.getClassType().getName() );
        
        if( controller.getMappingBeans().containsKey( name ) )
            throw new BrutosException( "duplicate mapping name " + name + " in the " + controller.getClassType().getName() );

        /*
        if( Map.class.isAssignableFrom( target ) ||
            Collection.class.isAssignableFrom( target ) )
            throw new BrutosException( "target is not allowed: " + target.getName() );
        */
        
        MappingBean mappingBean;

        if( Map.class.isAssignableFrom(target) )
            mappingBean = new MapMapping(controller);
        else
        if( List.class.isAssignableFrom(target) )
            mappingBean = new CollectionMapping(controller);
        else
            mappingBean = new MappingBean(controller);
        
        mappingBean.setClassType( target );
        mappingBean.setName( name );
        controller.getMappingBeans().put( name, mappingBean );
        BeanBuilder mb = new BeanBuilder( mappingBean, controller, this, validatorProvider );
        return mb;
    }

    /**
     * Adiciona uma nova a��o ao controlador.
     *
     * @param id Identifica��o da a��o.
     * @return Contrutor da a��o.
     */
    public ActionBuilder addAction( String id ){
        return addAction( id, null, null, DispatcherType.FORWARD, null );
    }

    /**
     * Adiciona uma nova a��o ao controlador.
     *
     * @param id Identifica��o da a��o.
     * @param executor Nome do m�todo que processa a a��o.
     * @return Contrutor da a��o.
     */
    public ActionBuilder addAction( String id, String executor ){
        return addAction( id, null, null, DispatcherType.FORWARD, executor );
    }

    /**
     * Adiciona uma nova a��o ao controlador.
     *
     * @param id Identifica��o da a��o.
     * @param executor Nome do m�todo que processa a a��o.
     * @param view Vis�o. Se omitido, ser� usado a vis�o do controldor.
     * @return Contrutor da a��o.
     */
    public ActionBuilder addAction( String id, String executor, String view ){
        return addAction( id, null, view, DispatcherType.FORWARD, executor );
    }
    
    /**
     * Adiciona uma nova a��o ao controlador.
     *
     * @param id Identifica��o da a��o.
     * @param executor Nome do m�todo que processa a a��o.
     * @param view Vis�o. Se omitido, ser� usado a vis�o do controldor.
     * @param resultId Identifica��o do resultado da a��o. Essa identifica��o
     * serve para se obter o resultado na vis�o.
     * @return Contrutor da a��o.
     */
    public ActionBuilder addAction( String id, String resultId, String view, String executor ){
        return addAction( id, resultId, view, DispatcherType.FORWARD, executor );
    }
    /**
     * Adiciona uma nova a��o ao controlador.
     *
     * @param id Identifica��o da a��o.
     * @param executor Nome do m�todo que processa a a��o.
     * @param view Vis�o. Se omitido, ser� usado a vis�o do controldor.
     * @param dispatcher Modo como ser� alterado o fluxo para a vis�o.
     * @param resultId Identifica��o do resultado da a��o. Essa identifica��o
     * serve para se obter o resultado na vis�o.
     * @return Contrutor da a��o.
     */
    public ActionBuilder addAction( String id, String resultId, String view, DispatcherType dispatcher, String executor ){
        
        id =
            id == null || id.replace( " ", "" ).length() == 0?
                null :
                id;
        resultId =
            resultId == null || resultId.replace( " ", "" ).length() == 0?
                null :
                resultId;

        view =
            view == null || view.replace( " ", "" ).length() == 0?
                null :
                view;

        executor =
            executor == null || executor.replace( " ", "" ).length() == 0?
                null :
                executor;
        
        if( controller.getMethods().containsKey( id ) )
            throw new BrutosException( "duplicate action " + id + ": " +
                controller.getClassType().getName() );
     
        MethodForm mp = new MethodForm();
        mp.setName( id );
        mp.setRedirect(false);
        mp.setDispatcherType(dispatcher);
        mp.setReturnPage(view);
        mp.setMethodName(executor);
        mp.setReturnIn( resultId == null? "result" : resultId );
        /*
        try{
            Class<?> classType = controller.getClassType();
            Method method = classType.getMethod( methodName, parametersType );
            mp.setParametersType( Arrays.asList( method.getParameterTypes() ) );

            Class<?> returnType = method.getReturnType();
            if( viewResult != null ){
                mp.setReturnPage( viewResult );
                mp.setReturnIn( resultId == null? "result" : resultId );
            }
            else
            if( returnType != void.class )
                mp.setReturnType( Types.getType( returnType ) );
            
            mp.setMethod( method );
            mp.setReturnClass( returnType );
        }
        catch( BrutosException e ){
            throw e;
        }
        catch( Exception e ){
            throw new BrutosException( e );
        }
        */
        
        mp.setForm( controller );
        controller.getMethods().put( id, mp );
        return new ActionBuilder( mp, controller, validatorProvider );
    }

    /**
     * Adiciona um novo interceptador ao controlador. Se o interceptador for
     * definido como "default" ser� lan�ada uma exce��o. O interceptador dever�
     * ser previamente criado.
     * @param name Nome do interceptador. Se n�o encontrado, ser� lan�ada uma exce��o.
     * @return Construtor do interceptador.
     */
    public InterceptorBuilder addInterceptor( String name ){
        Interceptor parent = interceptorManager.getInterceptor( name );
        Interceptor it = null;
        
        if( parent instanceof InterceptorStack )
            it = new InterceptorStack( (InterceptorStack) parent );
        else
            it = new Interceptor( parent );
        
        it.setProperties( new HashMap() );
        
        Set<String> keys = parent.getProperties().keySet();
        
        for( String key: keys ){
            Object value = parent.getProperties().get( key );
            it.getProperties().put( /*parent.getName() + "." +*/ key, value );
        }
        
        controller.addInterceptor( new Interceptor[]{it} );
        return new InterceptorBuilder( it, interceptorManager );
    }


    /**
     * Configura uma propriedade do controlador.
     *
     * @param propertyName Nome da propriedade no controlador.
     * @param id Identifica��o da propriedade.
     * @param scope Escopo do volor da propriedade.
     * @param enumProperty Usado na configura��o de propriedades do tipo enum.
     * @return Contrutor da propriedade.
     */
    public PropertyBuilder addProperty( String propertyName, String id, ScopeType scope, EnumerationType enumProperty ){
        return addProperty( propertyName, id, scope, enumProperty, null, null, null );
    }

    /**
     * Configura uma propriedade do controlador.
     *
     * @param propertyName Nome da propriedade no controlador.
     * @param id Identifica��o da propriedade.
     * @param scope Escopo do volor da propriedade.
     * @param temporalProperty Usado na configura��o de datas.
     * @return Contrutor da propriedade.
     */
    public PropertyBuilder addProperty( String propertyName, String id, ScopeType scope, String temporalProperty ){
        return addProperty( propertyName, id, scope, EnumerationType.ORDINAL, temporalProperty, null, null );
    }

    /**
     * Configura uma propriedade do controlador.
     *
     * @param propertyName Nome da propriedade no controlador.
     * @param id Identifica��o da propriedade.
     * @param scope Escopo do volor da propriedade.
     * @param type Faz o processamento do valor da propriedade.
     * @return Contrutor da propriedade.
     */
    public PropertyBuilder addProperty( String propertyName, String id, ScopeType scope, Type type ){
        return addProperty( propertyName, id, scope, EnumerationType.ORDINAL, "dd/MM/yyyy",
                null, type );
    }

    /**
     * Configura uma propriedade do controlador.
     *
     * @param propertyName Nome da propriedade no controlador.
     * @param id Identifica��o da propriedade.
     * @param enumProperty Usado na configura��o de propriedades do tipo enum.
     * @return Contrutor da propriedade.
     */
    public PropertyBuilder addProperty( String propertyName, String id, EnumerationType enumProperty ){
        return addProperty( propertyName, id, ScopeType.REQUEST, enumProperty, null, null, null );
    }

    /**
     * Configura uma propriedade do controlador.
     *
     * @param propertyName Nome da propriedade no controlador.
     * @param id Identifica��o da propriedade.
     * @param scope Escopo do volor da propriedade.
     * @return Contrutor da propriedade.
     */
    public PropertyBuilder addProperty( String propertyName, String id, ScopeType scope ){
        return addProperty( propertyName, id, scope, EnumerationType.ORDINAL, "dd/MM/yyyy",
                null, null );
    }

    /**
     * Configura uma propriedade do controlador.
     *
     * @param propertyName Nome da propriedade no controlador.
     * @param id Identifica��o da propriedade.
     * @param temporalProperty Usado na configura��o de datas.
     * @return Contrutor da propriedade.
     */
    public PropertyBuilder addProperty( String propertyName, String id, String temporalProperty ){
        return addProperty( propertyName, id, ScopeType.REQUEST, EnumerationType.ORDINAL, temporalProperty, null, null );
    }

    /**
     * Configura uma propriedade do controlador.
     *
     * @param propertyName Nome da propriedade no controlador.
     * @param id Identifica��o da propriedade.
     * @param type Faz o processamento do valor da propriedade.
     * @return Contrutor da propriedade.
     */
    public PropertyBuilder addProperty( String propertyName, String id, Type type ){
        return addProperty( propertyName, id, ScopeType.REQUEST, EnumerationType.ORDINAL, "dd/MM/yyyy",
                null, type );
    }

    /**
     * Configura uma propriedade do controlador.
     *
     * @param propertyName Nome da propriedade no controlador.
     * @param mapping nome do mapeamento do valor da propriedade. Esse mapemanto
     * deve ser previamente criado com o m�todo buildMappingBean(...).
     * @return Contrutor da propriedade.
     */
    public PropertyBuilder addPropertyMapping( String propertyName, String mapping ){
        return addProperty( propertyName, null, ScopeType.REQUEST, EnumerationType.ORDINAL, "dd/MM/yyyy",
                mapping, null );
    }

    /**
     * Configura uma propriedade do controlador.
     *
     * @param propertyName Nome da propriedade no controlador.
     * @param id Identifica��o da propriedade.
     * @param mapping nome do mapeamento do valor da propriedade. Esse mapemanto
     * deve ser previamente criado com o m�todo buildMappingBean(...).
     * @return Contrutor da propriedade.
     */
    public PropertyBuilder addPropertyMapping( String propertyName, String id, String mapping ){
        return addProperty( propertyName, id, ScopeType.REQUEST, EnumerationType.ORDINAL, "dd/MM/yyyy",
                mapping, null );
    }

    /**
     * Configura uma propriedade do controlador.
     *
     * @param propertyName Nome da propriedade no controlador.
     * @param id Identifica��o da propriedade.
     * @return Contrutor da propriedade.
     */
    public PropertyBuilder addProperty( String propertyName, String id ){
        return addProperty( propertyName, id, ScopeType.REQUEST, EnumerationType.ORDINAL, "dd/MM/yyyy",
                null, null );
    }

    /**
     * Configura uma propriedade do controlador.
     *
     * @param propertyName Nome da propriedade no controlador.
     * @param id Identifica��o da propriedade.
     * @param scope Escopo do volor da propriedade.
     * @param enumProperty Usado na configura��o de propriedades do tipo enum.
     * @param mapping nome do mapeamento do valor da propriedade. Esse mapemanto
     * deve ser previamente criado com o m�todo buildMappingBean(...).
     * @param temporalProperty Usado na configura��o de datas.
     * @param type Faz o processamento do valor da propriedade.
     * @return Contrutor da propriedade.
     */
    public PropertyBuilder addProperty( String propertyName, String id, ScopeType scope, EnumerationType enumProperty,
            String temporalProperty, String mapping, Type type ){

        id =
            id == null || id.replace( " ", "" ).length() == 0?
                null :
                id;
        propertyName =
            propertyName == null || propertyName.replace( " ", "" ).length() == 0?
                null :
                propertyName;

        temporalProperty =
            temporalProperty == null || temporalProperty.replace( " ", "" ).length() == 0?
                null :
                temporalProperty;

        mapping =
            mapping == null || mapping.replace( " ", "" ).length() == 0?
                null :
                mapping;

        if( id == null )
            throw new BrutosException( "name is required: " +
                    controller.getClassType().getName() );

        if( propertyName == null )
            throw new BrutosException( "property name is required: " +
                    controller.getClassType().getName() );

        Configuration validatorConfig = new Configuration();
        
        UseBeanData useBean = new UseBeanData();
        useBean.setNome( id );
        useBean.setScopeType( scope );
        useBean.setValidate( validatorProvider.getValidator( validatorConfig ) );

        FieldForm fieldBean = new FieldForm();
        fieldBean.setBean( useBean );
        fieldBean.setName(propertyName);


        BeanInstance bean = new BeanInstance( null, controller.getClassType() );

        if( !bean.containProperty(propertyName) )
            throw new BrutosException( "no such property: " +
                controller.getClassType().getName() + "." + propertyName );


        if( mapping != null ){
            if( controller.getMappingBeans().containsKey( mapping ) )
                useBean.setMapping( controller.getMappingBean( mapping ) );
            else
                throw new BrutosException( "mapping not found: " + mapping );

        }
        else
        if( type != null )
            useBean.setType( type );
        else{
            try{
                useBean.setType(
                        Types.getType(
                            bean.getGenericType(propertyName),
                            enumProperty,
                            temporalProperty ) );
            }
            catch( UnknownTypeException e ){
                throw new UnknownTypeException(
                        String.format( "%s.%s : %s" ,
                            controller.getClassType().getName(),
                            propertyName,
                            e.getMessage() ) );
            }
        }

        if( controller.getFields().contains( fieldBean ) )
            throw new BrutosException( "property already defined: " +
                    controller.getClassType().getName() + "." + propertyName );

        controller.getFields().add( fieldBean );

        return new PropertyBuilder( validatorConfig );
    }

    /**
     * Obt�m a classe do controlador.
     * @return Classe do controlador.
     */

    public Class getClassType(){
        return controller.getClassType();
    }
    
}
