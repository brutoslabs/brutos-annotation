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

package org.brandao.brutos.xml;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.brandao.brutos.BrutosException;
import org.brandao.brutos.ClassUtil;
import org.brandao.brutos.ComponentRegistry;
import org.brandao.brutos.io.Resource;
import org.brandao.brutos.io.ResourceLoader;
import org.brandao.brutos.type.TypeFactory;
import org.brandao.brutos.type.TypeManager;
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

    protected XMLParseUtil parseUtil;

    public ContextDefinitionReader(ComponentRegistry componenetRegistry){
        super(componenetRegistry);
    }
    
    protected Element validate( Resource resource ){
        DocumentBuilderFactory documentBuilderFactory =
                DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder;

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

    protected void build( Element document ){
        loadTypes( parseUtil.getElement(
                document,
                XMLBrutosConstants.XML_BRUTOS_TYPES ) );
        
        loadContextParams(
            parseUtil.getElement(
                document, 
                XMLBrutosConstants.XML_BRUTOS_CONTEXT_PARAMS ) );
        
    }

    private void loadContextParams( Element cp ){

        if( cp == null )
            return;
        
        NodeList list = parseUtil
            .getElements(
                cp,
                XMLBrutosConstants.XML_BRUTOS_CONTEXT_PARAM );

        for( int i=0;i<list.getLength();i++ ){
            Element c = (Element) list.item(i);
            String name  = parseUtil.getAttribute(c, "name" );
            String value = parseUtil.getAttribute(c,"value");

            value = value == null? c.getTextContent() : value;
            
            super.componenetRegistry.registerProperty(name, value);
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
            //String name  = parseUtil.getAttribute(c,"class-type" );
            String value = parseUtil.getAttribute(c,"factory");

            value = value == null? c.getTextContent() : value;

            //Class type;
            Class factory;

            try{
                factory = ClassUtil.get(value);
                TypeManager.register((TypeFactory)ClassUtil.getInstance(factory/*,super.handler*/));
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
        Resource resource = this.componenetRegistry.getResource(location);
        this.loadDefinitions(resource);
    }

    public ResourceLoader getResourceLoader() {
        return this.componenetRegistry;
    }

}
