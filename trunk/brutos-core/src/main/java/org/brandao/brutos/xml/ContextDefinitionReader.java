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

package org.brandao.brutos.xml;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Properties;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.brandao.brutos.AbstractApplicationContext;
import org.brandao.brutos.ApplicationContext;
import org.brandao.brutos.BrutosException;
import org.brandao.brutos.ClassUtil;
import org.brandao.brutos.ConfigurableApplicationContext;
import org.brandao.brutos.io.Resource;
import org.brandao.brutos.io.ResourceLoader;
import org.brandao.brutos.type.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 *
 * @author Brandao
 */
public class ContextDefinitionReader extends AbstractDefinitionReader{

    private XMLParseUtil parseUtil;

    public static final String AnnotationApplicationContext =
            "org.brandao.brutos.annotation.AnnotationApplicationContext";
    
    public ContextDefinitionReader( ConfigurableApplicationContext handler,
            List blackList, ResourceLoader resourceLoader ){
        super( handler, resourceLoader );
        this.parseUtil = new XMLParseUtil();
    }

    public Element validate( Resource resource ){
        DocumentBuilderFactory documentBuilderFactory =
                DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = null;

        URL schemaURL = Thread.currentThread()
            .getContextClassLoader()
                .getResource( XMLBrutosConstants.XML_BRUTOS_CONTEXT_SCHEMA );

        try{
            documentBuilderFactory.setNamespaceAware(true);
            documentBuilderFactory.setValidating(true);
            documentBuilderFactory.setAttribute(
                    XMLBrutosConstants.JAXP_SCHEMA_LANGUAGE,
                    XMLBrutosConstants.W3C_XML_SCHEMA
            );

            documentBuilderFactory.setAttribute(
                    XMLBrutosConstants.JAXP_SCHEMA_SOURCE,
                    schemaURL.toString()
            );
            documentBuilder = documentBuilderFactory.newDocumentBuilder();

            InputStream in = resource.getInputStream();
            Document xmlDocument =
                documentBuilder
                    .parse(new InputSource(in));
            return xmlDocument.getDocumentElement();
        }
        catch (BrutosException ex) {
            throw ex;
        }
        catch (SAXParseException ex) {
            throw new BrutosException(
                     "Line " + ex.getLineNumber() + " in XML document from "
                     + resource + " is invalid", ex);
        }
        catch (SAXException ex) {
             throw new BrutosException("XML document from " + resource +
                     " is invalid", ex);
        }
        catch (ParserConfigurationException   ex) {
             throw new BrutosException("Parser configuration exception parsing "
                     + "XML from " + resource, ex);
        }
        catch (IOException ex) {
             throw new BrutosException("IOException parsing XML document from "
                     + resource, ex);
        }
        catch (Throwable ex) {
             throw new BrutosException("Unexpected exception parsing XML document "
                     + "from " + resource, ex);
        }
    }

    public void build( Element document ){

        loadContextParams(
            parseUtil.getElement(
                document, 
                XMLBrutosConstants.XML_BRUTOS_CONTEXT_PARAMS ) );

        loadTypes( parseUtil.getElement(
                document,
                XMLBrutosConstants.XML_BRUTOS_TYPES ) );

        loadAnnotationDefinition( parseUtil.getElement(
                document,
                XMLBrutosConstants.XML_BRUTOS_ANNOTATION_CONFIG ) );
    }

    private void loadAnnotationDefinition( Element cp ){

        if( cp == null )
            return;

        ApplicationContext aac = createApplicationContext();
        aac.configure();
        
    }

    private ApplicationContext createApplicationContext(){

        Class clazz = getApplicationContextClass();

        if(ApplicationContext.class.isAssignableFrom(clazz)){
            try{
                ApplicationContext app =
                    (ApplicationContext) clazz
                        .getConstructor(new Class[]{ApplicationContext.class})
                            .newInstance(new Object[]{this.handler});
                return app;
            }
            catch( Exception e ){
                throw new BrutosException("unable to create instance: " +
                        clazz.getName(),e);
            }
        }
        else
            throw new BrutosException("annotation application is not valid:"+
                    clazz.getName());
    }

    private Class getApplicationContextClass(){
        return this.getContextClass(AnnotationApplicationContext);
    }

    private Class getContextClass( String contextClassName ){
        try {
            return Thread.currentThread().getContextClassLoader()
                    .loadClass(contextClassName);
        } catch (ClassNotFoundException ex) {
            throw new BrutosException( "Failed to load: " + contextClassName, ex );
        }
    }

    private void loadContextParams( Element cp ){

        if( cp == null )
            return;
        
        Properties config = handler.getConfiguration();

        NodeList list = parseUtil
            .getElements(
                cp,
                XMLBrutosConstants.XML_BRUTOS_CONTEXT_PARAM );

        for( int i=0;i<list.getLength();i++ ){
            Element c = (Element) list.item(i);
            String name  = parseUtil.getAttribute(c, "name" );
            String value = parseUtil.getAttribute(c,"value");

            value = value == null? c.getTextContent() : value;
            
            config.setProperty(name, value);
        }
        
    }
    
    private void loadTypes( Element cp ){

        if( cp == null )
            return;
        
        NodeList list = parseUtil
            .getElements(
                cp,
                XMLBrutosConstants.XML_BRUTOS_TYPE );

        for( int i=0;i<list.getLength();i++ ){
            Element c = (Element) list.item(i);
            String name  = parseUtil.getAttribute(c,"class-type" );
            String value = parseUtil.getAttribute(c,"factory");

            value = value == null? c.getTextContent() : value;

            Class type = null;
            Class factory = null;

            try{
                /*
                type = Class.forName(
                            name,
                            true,
                            Thread.currentThread().getContextClassLoader() );
                factory = Class.forName(
                            value,
                            true,
                            Thread.currentThread().getContextClassLoader() );
                */
                type = ClassUtil.get(name);
                factory = ClassUtil.get(value);
                TypeManager.register(type, (TypeFactory)ClassUtil.getInstance(factory));
                
            }
            catch( Exception e ){
                throw new BrutosException( e );
            }

        }

    }

    public void loadDefinitions(Resource resource) {
        Element document = this.validate(resource);
        this.build(document);
    }

    public void loadDefinitions(Resource[] resource) {
        if( resource != null )
            for( int i=0;i<resource.length;i++ )
                this.loadDefinitions(resource[i]);
    }

    public void loadDefinitions(String[] locations) {
        if( locations != null )
            for( int i=0;i<locations.length;i++ )
                this.loadDefinitions(locations[i]);
    }

    public void loadDefinitions(String location) {
        Resource resource = this.resourceLoader.getResource(location);
        this.loadDefinitions(resource);
    }

    public ResourceLoader getResourceLoader() {
        return this.resourceLoader;
    }

}