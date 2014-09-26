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
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import org.brandao.brutos.BrutosException;
import org.brandao.brutos.ComponentRegistry;
import org.brandao.brutos.io.Resource;
import org.brandao.brutos.io.ResourceLoader;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 *
 * @author Brandao
 */
public abstract class AbstractXMLDefinitionReader 
    extends AbstractDefinitionReader{

    public AbstractXMLDefinitionReader(ComponentRegistry componenetRegistry) {
        super(componenetRegistry);
    }

    protected Element buildDocument( Resource resource, String[] schemaLocation ){
        DocumentBuilderFactory documentBuilderFactory =
                DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder;

        try{
            SchemaFactory schemaFactory = 
                    SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            
            StreamSource[] streamSource = new StreamSource[schemaLocation.length];
            //ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            ResourceLoader resourceLoader = getResourceLoader();
            
            for(int i=0;i<schemaLocation.length;i++){
                streamSource[i] = 
                        new StreamSource(
                                resourceLoader
                                    .getResource(schemaLocation[i])
                                    .getInputStream());
            }
            
            Schema schema = schemaFactory.newSchema(streamSource);
            
            documentBuilderFactory.setNamespaceAware(true);
            documentBuilderFactory.setValidating(false);
            documentBuilderFactory.setSchema(schema);
            /*documentBuilderFactory.setAttribute(
                    XMLBrutosConstants.JAXP_SCHEMA_LANGUAGE,
                    XMLBrutosConstants.W3C_XML_SCHEMA
            );

            documentBuilderFactory.setAttribute(
                    XMLBrutosConstants.JAXP_SCHEMA_SOURCE,
                    schemaURL.toString()
            );
            */
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

    public abstract void loadDefinitions(Resource resource);
    
}
