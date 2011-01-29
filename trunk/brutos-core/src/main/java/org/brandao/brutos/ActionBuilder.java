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

import org.brandao.brutos.type.UnknownTypeException;
import org.brandao.brutos.mapping.Form;
import org.brandao.brutos.mapping.MethodForm;
import org.brandao.brutos.mapping.ParameterMethodMapping;
import org.brandao.brutos.mapping.ThrowableSafeData;
import org.brandao.brutos.mapping.UseBeanData;
import org.brandao.brutos.type.Type;
import org.brandao.brutos.type.Types;
import org.brandao.brutos.validator.ValidatorProvider;

/**
 * Constr�i uma a��o. A a��o pode ter ou n�o par�metros. Os par�metros podem ser
 * obtidos tanto da requisi��o, sess�o ou do contexto. Podendo ser de tipo primitivo
 * ou n�o. No caso de um objeto complexo, � poss�vel usar um mapeamento predefinido.
 * Se a a��o retornar algum valor, este ser� processado e inclu�do na requisi��o,
 * para posteriormente ser usada na vis�o.
 * As exce��es lan�adas durante a execu��o da a��o, podem alterar o fluxo l�gico
 * da aplica��o.
 *
 * No exemplo a seguir, depois de executar a a��o showPerson � exibido a
 * vis�o personView.jsp, e se for lan�ada a exce��o NotExistPerson a vis�o
 * error.jsp ser� exibida.
 * 
 * <pre>
 * public class MyController{
 *
 *   public void showPerson( int id ){
 *     ...
 *   }
 * }
 *
 * controllerBuilder
 *   .addAction( "show", "showPerson", "personView.jsp" )
 *   .addThrowable( NotExistPerson.class, "error.jsp", "exception", DispatcherType.FORWARD )
 *   .addParameter( "idPerson", int.class );
 *   
 * </pre>
 * 
 * @author Afonso Brandao
 */
public class ActionBuilder {
    
    Form webFrame;
    MethodForm methodForm;
    ValidatorProvider validatorProvider;

    public ActionBuilder( MethodForm methodForm, Form controller, ValidatorProvider validatorProvider ) {
        this.webFrame = controller;
        this.methodForm = methodForm;
        this.validatorProvider = validatorProvider;
    }

    /**
     * Configura um novo par�metro.
     *
     * @param name Identifica��o do par�metro.
     * @param scope Escopo do valor do par�metro.
     * @param enumProperty Usado na configura��o de par�metros do tipo enum.
     * @param classType Tipo do par�metro.
     * @return Contrutor do par�metro.
     */
    public ParameterBuilder addParameter( String name, ScopeType scope, EnumerationType enumProperty, Class classType ){
        return addParameter( name, scope, enumProperty, null, null, null, classType );
    }
    
    /**
     * Configura um novo par�metro.
     *
     * @param name Identifica��o do par�metro.
     * @param scope Escopo do volor do par�metro.
     * @param enumProperty Usado na configura��o de par�metros do tipo enum.
     * @param temporalProperty Usado na configura��o de datas.
     * @param classType Tipo do par�metro.
     * @return Contrutor do par�metro.
     */
    public ParameterBuilder addParameter( String name, ScopeType scope, String temporalProperty, Class classType ){
        return addParameter( name, scope, EnumerationType.ORDINAL, temporalProperty, null, null, classType );
    }
    
    /**
     * Configura um novo par�metro.
     *
     * @param name Identifica��o do par�metro.
     * @param scope Escopo do volor do par�metro.
     * @param type Faz o processamento do valor do par�metro.
     * @return Contrutor do par�metro.
     */
    public ParameterBuilder addParameter( String name, ScopeType scope, Type type ){
        return addParameter( name, scope, EnumerationType.ORDINAL, "dd/MM/yyyy",
                null, type, type.getClassType() );
    }
    
    /**
     * Configura um novo par�metro.
     *
     * @param name Identifica��o do par�metro.
     * @param enumProperty Usado na configura��o de par�metros do tipo enum.
     * @param classType Tipo do par�metro.
     * @return Contrutor do par�metro.
     */
    public ParameterBuilder addParameter( String name, EnumerationType enumProperty, Class classType ){
        return addParameter( name, ScopeType.REQUEST, enumProperty, null, null, null, classType );
    }
    
    /**
     * Configura um novo par�metro.
     *
     * @param name Identifica��o do par�metro.
     * @param scope Escopo do valor do par�metro.
     * @param classType Tipo do par�metro.
     * @return Contrutor do par�metro.
     */
    public ParameterBuilder addParameter( String name, ScopeType scope, Class classType ){
        return addParameter( name, scope, EnumerationType.ORDINAL, "dd/MM/yyyy", 
                null, null, classType );
    }
    
    /**
     * Configura um novo par�metro.
     *
     * @param name Identifica��o do par�metro.
     * @param temporalProperty Usado na configura��o de datas.
     * @param classType Tipo do par�metro.
     * @return Contrutor do par�metro.
     */
    public ParameterBuilder addParameter( String name, String temporalProperty, Class classType ){
        return addParameter( name, ScopeType.PARAM, EnumerationType.ORDINAL, temporalProperty, null, null, classType );
    }
    
    /**
     * Configura um novo par�metro.
     *
     * @param name Identifica��o do par�metro.
     * @param type Faz o processamento do valor do par�metro.
     * @return Contrutor do par�metro.
     */
    public ParameterBuilder addParameter( String name, Type type ){
        return addParameter( name, ScopeType.PARAM, EnumerationType.ORDINAL, "dd/MM/yyyy",
                null, type, type.getClassType() );
    }
    
    /**
     * Configura um novo par�metro.
     *
     * @param mapping Nome do mapeamento do valor do par�metro. Esse mapeamento
     * deve ser previamente criado com o m�todo buildMappingBean(...).
     * @param classType Tipo do par�metro.
     * @return Contrutor do par�metro.
     */
    public ParameterBuilder addParameterMapping( String mapping, Class classType ){
        return addParameter( null, ScopeType.PARAM, EnumerationType.ORDINAL, "dd/MM/yyyy",
                mapping, null, classType );
    }
    
    /**
     * Configura um novo par�metro.
     *
     * @param name Identifica��o do par�metro.
     * @param classType Tipo do par�metro.
     * @return Contrutor do par�metro.
     */
    public ParameterBuilder addParameter( String name, Class classType ){
        return addParameter( name, ScopeType.PARAM, EnumerationType.ORDINAL, "dd/MM/yyyy",
                null, null, classType );
    }


    /**
     * Configura um novo par�metro.
     * 
     * @param name Identifica��o do par�metro.
     * @param scope Escopo do valor do par�metro.
     * @param enumProperty Usado na configura��o de par�metros do tipo enum.
     * @param mapping Nome do mapeamento do valor do par�metro. Esse mapeamento
     * deve ser previamente criado com o m�todo buildMappingBean(...).
     * @param temporalProperty Usado na configura��o de datas.
     * @param type Faz o processamento do valor do par�metro.
     * @param classType Tipo do par�metro.
     * @return Contrutor do par�metro.
     */
    public ParameterBuilder addParameter( String name, ScopeType scope, EnumerationType enumProperty,
            String temporalProperty, String mapping, Type type, Class classType ){
        
        name = name == null || name.replace( " ", "" ).length() == 0? null : name;
        temporalProperty = temporalProperty == null || temporalProperty.replace( " ", "" ).length() == 0? null : temporalProperty;
        mapping = mapping == null || mapping.replace( " ", "" ).length() == 0? null : mapping;
        
        //if( methodForm.getParameters().size() > methodForm.getParametersType().size() )
        //    throw new BrutosException( "index > " + methodForm.getParametersType().size() );
        
        /*Class classType = methodForm.
                    getParametersType().get( methodForm.getParamterSize() );*/

        Configuration validatorConfig = new Configuration();
        
        UseBeanData useBean = new UseBeanData();

        useBean.setNome( name );
        useBean.setScopeType( scope );
        useBean.setValidate( validatorProvider.getValidator( validatorConfig ) );
        
        if( mapping != null ){
            if( webFrame.getMappingBeans().containsKey( mapping ) )
                useBean.setMapping( webFrame.getMappingBean( mapping ) );
            else
                throw new BrutosException( "mapping name " + mapping + " not found!" );
                
        }
        else
        if( type != null ){
            useBean.setType( type );
            if( !classType.isAssignableFrom( useBean.getType().getClassType() ) )
                throw new BrutosException( "expected " + classType.getName() + " found " + type.getClassType().getName() );
        }
        else{
            try{
                /*useBean.setType( Types.getType( methodForm.getGenericParameterType( methodForm.getParamterSize() ), enumProperty, temporalProperty ) );*/
                useBean.setType( Types.getType( classType ));
            }
            catch( UnknownTypeException e ){
                throw new UnknownTypeException( 
                        String.format( "%s.%s(...) index %d : %s" ,
                            methodForm.getMethod().getDeclaringClass().getName(),
                            methodForm.getMethod().getName(),
                            methodForm.getParamterSize(),
                            e.getMessage() ) );
            }
            
            if( useBean.getType() == null )
                throw new UnknownTypeException( classType.getName() );
        }

        ParameterMethodMapping pmm = new ParameterMethodMapping();
        pmm.setBean( useBean );
        pmm.setParameterId( 0 );
        
        methodForm.addParameter( pmm );
        return new ParameterBuilder( validatorConfig );
    }

    /**
     * Intercepta e atribui uma identifica��o a uma determinada exce��o. O
     * objeto resultante da exce��o pode ser usando na vis�o.
     *
     * @param target Exce��o alvo.
     * @param view Vis�o. Se omitido, ser� usada a vis�o do controlador.
     * @param id Identifica��o.
     * @return Contrutor do controlador.
     */
    public ActionBuilder addThrowable( Class target, String id ){
        return addThrowable( target, null, id, DispatcherType.FORWARD );
    }

    /**
     * Intercepta e atribui uma identifica��o a uma determinada exce��o. O
     * objeto resultante da exce��o pode ser usando na vis�o.
     *
     * @param target Exce��o alvo.
     * @param view Vis�o. Se omitido, ser� usada a vis�o da a��o.
     * @param id Identifica��o.
     * @param dispatcher Modo como ser� direcionado o fluxo para a vis�o.
     * @return Contrutor do controlador.
     */
    public ActionBuilder addThrowable( Class target, String view, String id, DispatcherType dispatcher ){
        view = view == null || view.replace( " ", "" ).length() == 0? null : view;
        id = id == null || id.replace( " ", "" ).length() == 0? null : id;

        if( target == null )
            throw new BrutosException( "target is required: " + webFrame.getClassType().getName() );

        if( !Throwable.class.isAssignableFrom( target ) )
            throw new BrutosException( "target is not allowed: " +target.getName() );

        ThrowableSafeData thr = new ThrowableSafeData();
        thr.setParameterName(id);
        thr.setTarget(target);
        thr.setUri(view);
        thr.setRedirect( false );
        thr.setDispatcher(dispatcher);
        methodForm.setThrowsSafe(thr);
        return this;
    }

}
