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
import org.brandao.brutos.bean.BeanInstance;
import org.brandao.brutos.mapping.CollectionMapping;
import org.brandao.brutos.mapping.FieldBean;
import org.brandao.brutos.mapping.Form;
import org.brandao.brutos.mapping.MapMapping;
import org.brandao.brutos.mapping.MappingBean;
import org.brandao.brutos.type.Type;
import org.brandao.brutos.type.Types;
import org.brandao.brutos.validator.ValidatorProvider;

/**
 * Constr�i o mapeamento de um bean.<br>
 * Com essa classe � poss�vel definir as depend�ncias do bean, tanto por construtor
 * quanto por m�todo. � poss�vel tamb�m obter a inst�ncia do bean por meio de
 * uma f�brica ou m�todo est�tico. <br>
 * Sua inst�ncia � obtida a partir da classe ControllerBuilder.<br>
 * BeanBuilder n�o tem a mesma fun��o que um container IOC, ela apenas faz o
 * mapeamento de uma requisi��o em um determinado bean.
 *
 * <p>No exemplo abaixo a depend�ncia do bean � mapeada por construtor.</p>
 *
 * Ex:
 * <pre>
 *
 * &lt;html&gt;
 *   &lt;body&gt;
 *    &lt;a href="/index.jbrs?idBean=100"&gt;show&lt;/a&gt;
 *   &lt;/body&gt;
 * &lt;/html&gt;
 *
 * public class MyBean{
 *
 *   private int id;
 *
 *   public MyBean( int val ){
 *     this.id = id;
 *   }
 *   ...
 * }
 *
 * beanBuilder.addContructorArg( "idBean" );
 *
 * </pre>
 *
 * No pr�ximo exemplo o mesmo par�metro � mapeado em uma propriedade.
 *
 * Ex:
 * <pre>
 * public class MyBean{
 *
 *   private int id;
 *
 *   public MyBean(){
 *   }
 *
 *   public void setId( int id ){
 *     this.id = id;
 *   }
 *   ...
 * }
 *
 * beanBuilder.addProperty( "id","idBean" );
 *
 * </pre>
 *
 * Nesse exemplo � feito o mapeamento de um atributo do tipo enum.
 * <pre>
 *
 * &lt;html&gt;
 *   &lt;body&gt;
 *    &lt;a href="/index.jbrs?idBean=1"&gt;show&lt;/a&gt;
 *   &lt;/body&gt;
 * &lt;/html&gt;
 *
 * public enum MyEnum{
 *   VALUE1("Valor 1"),
 *   VALUE2("Valor 2"),
 *   VALUE3("Valor 3");
 *
 *   ...
 *
 * }
 *
 * public class MyBean{
 *
 *   private MyEnum id;
 *
 *   public MyBean(){
 *   }
 *
 *   public void setId( MyEnum id ){
 *     this.id = id;
 *   }
 *   ...
 * }
 *
 * beanBuilder.addProperty( "idBean","id", EnumerationType.ORDINAL );
 *
 * </pre>
 *
 * O mapeamento do enum pode tamb�m ser feito da seguinte maneira:
 * <pre>
 *
 * &lt;html&gt;
 *   &lt;body&gt;
 *    &lt;a href="/index.jbrs?enumName=VALUE2"&gt;show&lt;/a&gt;
 *   &lt;/body&gt;
 * &lt;/html&gt;
 *
 * controllerBuilder
 *      .buildMappingBean( "myEnumMapping", MyEnum.class )
 *      .setMethodfactory( "valueOf" )
 *      .addConstructorArg( "enumName" );
 *
 * beanBuilder.addMappedProperty( "id", "myEnumMapping" );
 *
 * </pre>
 *
 *
 * @author Afonso Brandao
 */
public class BeanBuilder {

    Form webFrame;
    ControllerBuilder controllerBuilder;
    MappingBean mappingBean;
    ValidatorProvider validatorProvider;

    public BeanBuilder( MappingBean mappingBean, Form webFrame, ControllerBuilder controllerBuilder,
            ValidatorProvider validatorProvider ) {
        this.controllerBuilder = controllerBuilder;
        this.mappingBean = mappingBean;
        this.webFrame = webFrame;
        this.validatorProvider = validatorProvider;
    }

    /**
     * Define o nome da f�brica do bean.
     * @param factory Nome da f�brica.
     * @return Construtor do mapeamento.
     */
    public BeanBuilder setFactory( String factory ){
        mappingBean.setFactory(factory);
        return this;
    }

    /**
     * Define o nome do m�todo da f�brica.
     * @param methodFactory Nome do m�todo.
     * @return Construtor do mapeamento.
     */
    public BeanBuilder setMethodfactory( String methodFactory ){
        mappingBean.getConstructor().setMethodFactory(methodFactory);
        return this;
    }

    /**
     * Define o separador. Se n�o informado, o separador ser� ".".
     * @param separator Separador
     * @return Construtor do mapeamento.
     */
    public BeanBuilder setSeparator( String separator ){
        mappingBean.setSeparator(separator);
        return this;
    }

    /**
     * Faz o mapeamento de uma propriedade.
     *
     * @param name Nome do par�metro.
     * @param propertyName Nome da propriedade
     * @param enumProperty Usado no mapeamento de enum.
     * @return Construtor do mapeamento.
     */
    public PropertyBuilder addProperty( String name, String propertyName,
            EnumerationType enumProperty ){
        return addProperty( name, propertyName, enumProperty, null, null, 
                ScopeType.PARAM, null, null );
    }
    
    /**
     * Faz o mapeamento de uma propriedade.
     *
     * @param name Nome do par�metro.
     * @param propertyName Nome da propriedade
     * @param temporalProperty Usado no mapeamento de datas.
     * @return Construtor do mapeamento.
     */
    public PropertyBuilder addProperty( String name, String propertyName,
            String temporalProperty ){
        return addProperty( name, propertyName, EnumerationType.ORDINAL, 
                temporalProperty, null, ScopeType.PARAM, null, null );
    }
    
    /**
     * Faz o mapeamento de uma propriedade.
     *
     * @param name Nome do par�metro.
     * @param propertyName Nome da propriedade
     * @param type Faz o processamento da propriedade.
     * @return Construtor do mapeamento.
     */
    public PropertyBuilder addProperty( String name, String propertyName,
            Type type ){
        return addProperty( name, propertyName, EnumerationType.ORDINAL, "dd/MM/yyyy",
                null,ScopeType.PARAM, null, type );
    }
    
    /**
     * Faz o mapeamento de uma propriedade.
     *
     * @param name Nome do par�metro.
     * @param propertyName Nome da propriedade
     * @param mapping Mapeamento customizado.
     * @return Construtor do mapeamento.
     */
    public PropertyBuilder addMappedProperty( String name, String propertyName, String mapping ){
        return addProperty( name, propertyName, EnumerationType.ORDINAL, "dd/MM/yyyy",
                mapping, ScopeType.PARAM, null, null );
    }

    /**
     * Faz o mapeamento de uma propriedade.
     *
     * @param propertyName Nome da propriedade
     * @param mapping Mapeamento customizado.
     * @return Construtor do mapeamento.
     */
    public PropertyBuilder addMappedProperty( String propertyName, String mapping ){
        return addProperty( null, propertyName, EnumerationType.ORDINAL, "dd/MM/yyyy",
                mapping, ScopeType.PARAM, null, null );
    }

    /**
     * Contr�i o mapeamento da chave usada para acessar os elementos de uma cole��o.
     * 
     * @param type Classe alvo do mapeamento.
     * @return Construtor do mapeamento.
     * @throws org.brandao.brutos.BrutosException Lan�ado se a classe alvo do
     * mapeamento n�o for uma cole��o.
     */
    public BeanBuilder buildKey( Class type ){

        if( !this.mappingBean.isMap() )
            throw new BrutosException(
                String.format("is not allowed for this type: %s",
                    this.mappingBean.getClassType() ) );
        
        String beanName = mappingBean.getName() + "#key";
        BeanBuilder bb = controllerBuilder
                    .buildMappingBean(beanName, type);

        MapMapping map = (MapMapping)webFrame.getMappingBean(beanName);
        map.setMappingKey(mappingBean);
        return bb;
    }
    
    /**
     * Constr�i o mapeamento dos elementos de uma cole��o.
     *
     * @param type Classe alvo do mapeamento.
     * @return Construtor do mapeamento dos elementos.
     */
    public BeanBuilder buildElement( Class type ){

        String beanName = mappingBean.getName() + "#bean";
        BeanBuilder bb = controllerBuilder
                    .buildMappingBean(beanName, type);

        setElement( beanName );

        return bb;
    }

    /**
     * Define a representa��o do �ndice do objeto em uma cole��o.
     * @param indexFormat Representa��o.
     * @return Construtor do mapeamento.
     */
    public BeanBuilder setIndexFormat( String indexFormat ){
        mappingBean.setIndexFormat(indexFormat);
        return this;
    }

    /**
     * Define o tipo da cole��o.
     * 
     * @param ref Nome do mapeamento.
     * @return Construtor do mapeamento.
     * @throws java.lang.NullPointerException Lan�ado se o nome do mapeamento for igual a null.
     * @throws org.brandao.brutos.NotFoundMappingBeanException Lan�ado se o
     * mapeamento n�o for encontrado.
     * @throws org.brandao.brutos.BrutosException Lan�ado se a classe alvo do
     * mapeamento n�o for uma cole��o.
     */
    public BeanBuilder setElement( String ref ){

        if( ref == null )
            throw new NullPointerException();

        if( !this.mappingBean.isCollection() && !this.mappingBean.isMap() )
            throw new BrutosException(
                String.format("is not allowed for this type: %s",
                    this.mappingBean.getClassType() ) );
        
        ref = ref == null || ref.replace( " ", "" ).length() == 0? null : ref;

        if( !webFrame.getMappingBeans().containsKey( ref ) )
            throw new NotFoundMappingBeanException(
                    String.format(
                        "mapping %s not found: %s",
                        ref, webFrame.getClassType().getName() ) );

        MappingBean bean = webFrame.getMappingBeans().get( ref );

        /*
        if( !bean.isBean() )
            throw new BrutosException(
                    "not allowed: " +
                    webFrame.getClassType().getName() );
        */

        ((CollectionMapping)mappingBean).setBean( bean );
        return this;
    }

    /**
     * Constr�i o mapeamento de uma propriedade.
     * 
     * @param name Identifica��o.
     * @param propertyName Nome da propriedade
     * @param target Classe alvo do mapeamento.
     * @return Construtor da propriedade.
     */
    public BeanBuilder buildProperty( String name, String propertyName, Class target ){
        String beanName = this.mappingBean.getName() + "#" + propertyName;
        
        BeanBuilder beanBuilder = 
                this.controllerBuilder.buildMappingBean(beanName, target);

        this.addMappedProperty(name, propertyName, beanName);
        
        return beanBuilder;
    }

    /**
     * Faz o mapeamento de uma propriedade.
     *
     * @param name Nome do par�metro.
     * @param propertyName Nome da propriedade
     * @return Construtor do mapeamento.
     */
    public PropertyBuilder addProperty( String name, String propertyName ){
        return addProperty( name, propertyName, EnumerationType.ORDINAL, "dd/MM/yyyy", 
                null, ScopeType.PARAM, null, null );
    }

    /**
     * Faz o mapeamento de uma propriedade.
     *
     * @param name Nome do par�metro.
     * @param propertyName Nome da propriedade
     * @param scope Escopo.
     * @return Construtor do mapeamento.
     */
    public PropertyBuilder addProperty( String name, String propertyName, ScopeType scope ){
        return addProperty( name, propertyName, EnumerationType.ORDINAL, "dd/MM/yyyy",
                null, scope, null, null );
    }

    /**
     * Faz o mapeamento de uma propriedade.
     *
     * @param name Nome do par�metro.
     * @param propertyName Nome da propriedade
     * @param value Valor da propriedade. Tem a mesma fun��o do modificador final.
     * @return Construtor do mapeamento.
     */
    public PropertyBuilder addStaticProperty( String name, String propertyName, Object value ){
        return addProperty( null, propertyName,
            EnumerationType.ORDINAL, "dd/MM/yyyy", null, ScopeType.PARAM, value, null );
    }

    /**
     * Faz o mapeamento de uma propriedade.
     * 
     * @param name Nome do par�metro.
     * @param propertyName Nome da propriedade
     * @param enumProperty Usado no mapeamento de enum.
     * @param temporalProperty Usado no mapeamento de datas.
     * @param mapping Mapeamento customizado.
     * @param scope Escopo.
     * @param value Valor da propriedade. Tem a mesma fun��o do modificador final.
     * @param type Faz o processamento da propriedade.
     * @return Construtor do mapeamento.
     */
    public PropertyBuilder addProperty( String name, String propertyName,
            EnumerationType enumProperty, String temporalProperty, String mapping, 
            ScopeType scope, Object value, Type type ){

        /*
        name = name == null || name.replace( " ", "" ).length() == 0? null : name;
        propertyName = propertyName == null || propertyName.replace( " ", "" ).length() == 0? null : propertyName;
        temporalProperty = temporalProperty == null || temporalProperty.replace( " ", "" ).length() == 0? null : temporalProperty;
        mapping = mapping == null || mapping.replace( " ", "" ).length() == 0? null : mapping;

        if( propertyName == null )
            throw new BrutosException( "the property name is required!" );
        else
        if( this.mappingBean.getFields().containsKey( propertyName ) )
            throw new BrutosException( "duplicate property name: " + propertyName );

        /*
        if( name == null )
            throw new BrutosException( "name is required: " +
                    mappingBean.getClassType().getName() );
        */
        /*
        FieldBean fieldBean = new FieldBean();
        fieldBean.setEnumProperty( enumProperty );
        fieldBean.setParameterName( name );
        fieldBean.setName(propertyName);
        fieldBean.setTemporalType( temporalProperty );
        fieldBean.setValue(value);
        fieldBean.setScopeType( scope );

        BeanInstance bean = new BeanInstance( null, mappingBean.getClassType() );

        if( !bean.containProperty(propertyName) )
            throw new BrutosException( "no such property: " +
                mappingBean.getClassType().getName() + "." + propertyName );

        if( mapping != null ){
            fieldBean.setMapping( mapping );
                
        }
        else
        if( type != null ){
            fieldBean.setType( type );
        }
        else{
            try{
                fieldBean.setType(
                        Types.getType(
                            bean.getGenericType(propertyName),
                            enumProperty,
                            temporalProperty ) );
            }
            catch( UnknownTypeException e ){
                throw new UnknownTypeException(
                        String.format( "%s.%s : %s" ,
                            webFrame.getClassType().getName(),
                            propertyName,
                            e.getMessage() ) );
            }
        }
        */

        FieldBean fieldBean = this.createDependencyBean(name, propertyName, true,
                enumProperty, temporalProperty, mapping, scope, value, type);

        Configuration validatorConfig = new Configuration();
        fieldBean.setValidator( validatorProvider.getValidator( validatorConfig ) );
        this.mappingBean.getFields().put( propertyName, fieldBean );

        return new PropertyBuilder( validatorConfig );
    }

    /**
     * Constr�i o mapeamento de um argumento do construtor.
     *
     * @param name Identifica��o.
     * @param target Classe alvo do mapeamento.
     * @return Construtor do argumento.
     */
    public BeanBuilder buildConstructorArg( String name, Class target ){
        String beanName = this.mappingBean.getName()
                + "#[" + this.mappingBean.getConstructor().getArgs().size() + "]";

        BeanBuilder beanBuilder =
                this.controllerBuilder.buildMappingBean(beanName, target);

        this.addMappedContructorArg(name, beanName);

        return beanBuilder;
    }

    /**
     * Faz o mapeamento de um argumento do construtor.
     *
     * @param name Nome do par�metro.
     * @param enumProperty Usado no mapeamento argumentos do tipo enum.
     * @return Construtor do argumento.
     */
    public ConstructorBuilder addContructorArg( String name,
            EnumerationType enumProperty ){
        return addContructorArg( name, enumProperty, null, null, ScopeType.PARAM, null, null );
    }

    /**
     * Faz o mapeamento de um argumento do construtor.
     *
     * @param name Nome do par�metro.
     * @param temporalProperty Usado no mapeamento de datas.
     * @return Construtor do argumento.
     */
    public ConstructorBuilder addContructorArg( String name,
            String temporalProperty ){
        return addContructorArg( name, EnumerationType.ORDINAL, temporalProperty, null, ScopeType.PARAM, null, null );
    }

    /**
     * Faz o mapeamento de um argumento do construtor.
     *
     * @param name Nome do par�metro.
     * @param type Faz o processamento do argumento.
     * @return Construtor do argumento.
     */
    public ConstructorBuilder addContructorArg( String name,
            Type type ){
        return addContructorArg( name, EnumerationType.ORDINAL, "dd/MM/yyyy",
                null,ScopeType.PARAM, null, type );
    }

    /**
     * Faz o mapeamento de um argumento do construtor.
     *
     * @param name Nome do par�metro.
     * @param mapping Mapeamento customizado.
     * @return Construtor do argumento.
     */
    public ConstructorBuilder addMappedContructorArg( String name, String mapping ){
        return addContructorArg( name, EnumerationType.ORDINAL, "dd/MM/yyyy",
                mapping, ScopeType.PARAM, null, null );
    }

    /**
     * Faz o mapeamento de um argumento do construtor.
     *
     * @param name Nome do par�metro.
     * @return Construtor do argumento.
     */
    public ConstructorBuilder addContructorArg( String name ){
        return addContructorArg( name, EnumerationType.ORDINAL, "dd/MM/yyyy",
                null, ScopeType.PARAM, null, null );
    }

    /**
     * Faz o mapeamento de um argumento do construtor.
     *
     * @param name Nome do par�metro.
     * @param scope Escopo.
     * @return Construtor do argumento.
     */
    public ConstructorBuilder addContructorArg( String name, ScopeType scope ){
        return addContructorArg( name, EnumerationType.ORDINAL, "dd/MM/yyyy",
                null, scope, null, null );
    }

    /**
     * Faz o mapeamento de um argumento do construtor.
     *
     * @param name Nome do par�metro.
     * @param value Valor da propriedade. Tem a mesma fun��o do modificador final.
     * @return Construtor do argumento.
     */
    public ConstructorBuilder addStaticContructorArg( String name, Object value ){
        return addContructorArg( name,
            EnumerationType.ORDINAL, "dd/MM/yyyy", null, ScopeType.PARAM, value, null );
    }

    /**
     * Faz o mapeamento de um argumento do construtor.
     *
     * @param name Nome do par�metro.
     * @param enumProperty Usado no mapeamento argumentos do tipo enum.
     * @param temporalProperty Usado no mapeamento de datas.
     * @param mapping Mapeamento customizado.
     * @param scope Escopo.
     * @param value Valor da propriedade. Tem a mesma fun��o do modificador final.
     * @param type Faz o processamento do argumento.
     * @return Construtor do argumento.
     */
    public ConstructorBuilder addContructorArg( String name,
            EnumerationType enumProperty, String temporalProperty, String mapping,
            ScopeType scope, Object value, Type type ){

        FieldBean fieldBean = this.createDependencyBean(name, null, false,
                enumProperty, temporalProperty, mapping, scope, value, type);

        Configuration validatorConfig = new Configuration();
        fieldBean.setValidator( validatorProvider.getValidator( validatorConfig ) );
        this.mappingBean.getConstructor().getArgs().add(fieldBean);
        return new ConstructorBuilder( validatorConfig );
    }

    private FieldBean createDependencyBean( String name, String propertyName,
            boolean propertyNameRequired, EnumerationType enumProperty,
            String temporalProperty, String mapping, ScopeType scope,
            Object value, Type type ){

        name = name == null || name.replace( " ", "" ).length() == 0? null : name;
        propertyName = propertyName == null || propertyName.replace( " ", "" ).length() == 0? null : propertyName;
        temporalProperty = temporalProperty == null || temporalProperty.replace( " ", "" ).length() == 0? null : temporalProperty;
        mapping = mapping == null || mapping.replace( " ", "" ).length() == 0? null : mapping;

        if( propertyNameRequired && propertyName == null )
            throw new BrutosException( "the property name is required!" );
        else
        if( this.mappingBean.getFields().containsKey( propertyName ) )
            throw new BrutosException( "duplicate property name: " + propertyName );

        /*
        if( name == null )
            throw new BrutosException( "name is required: " +
                    mappingBean.getClassType().getName() );
        */
        FieldBean fieldBean = new FieldBean();
        fieldBean.setEnumProperty( enumProperty );
        fieldBean.setParameterName( name );
        fieldBean.setName(propertyName);
        fieldBean.setTemporalType( temporalProperty );
        fieldBean.setValue(value);
        fieldBean.setScopeType( scope );

        BeanInstance bean = new BeanInstance( null, mappingBean.getClassType() );

        if( !bean.containProperty(propertyName) )
            throw new BrutosException( "no such property: " +
                mappingBean.getClassType().getName() + "." + propertyName );

        if( mapping != null ){
            fieldBean.setMapping( mapping );

        }
        else
        if( type != null ){
            fieldBean.setType( type );
        }
        else{
            try{
                fieldBean.setType(
                        Types.getType(
                            bean.getGenericType(propertyName),
                            enumProperty,
                            temporalProperty ) );
            }
            catch( UnknownTypeException e ){
                throw new UnknownTypeException(
                        String.format( "%s.%s : %s" ,
                            webFrame.getClassType().getName(),
                            propertyName,
                            e.getMessage() ) );
            }
        }

        Configuration validatorConfig = new Configuration();
        fieldBean.setValidator( validatorProvider.getValidator( validatorConfig ) );
        return fieldBean;
    }

    /**
     * Verifica se � o mapeamento de um Map.
     * @return Verdadeiro se � o mapeamento de um Map, caso contr�rio falso.
     */
    public boolean isMap(){
        return this.mappingBean.isMap();
    }

    /**
     * Verifica se � o mapeamento de uma Collection.
     * @return Verdadeiro se � o mapeamento de uma Collection,
     * caso contr�rio falso.
     */
    public boolean isCollection(){
        return this.mappingBean.isCollection();
    }

}
