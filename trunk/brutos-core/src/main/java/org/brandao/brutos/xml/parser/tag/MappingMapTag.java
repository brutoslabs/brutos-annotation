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

package org.brandao.brutos.xml.parser.tag;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.brandao.brutos.xml.parser.Stack;
import org.brandao.brutos.xml.parser.Tag;
import org.xml.sax.Attributes;

/**
 *
 * @author Afonso Brandao
 */
public class MappingMapTag implements Tag{

    private Stack stack;

    public void setStack(Stack stack) {
        this.stack = stack;
    }

    public void setText(String txt) {
    }

    public boolean isRead() {
        return false;
    }

    public void start(String localName, Attributes atts) {

        Map<String,Object> mapping = new HashMap();
        
        mapping.put( "@tag", "mapping-map" );
        mapping.put( "name", atts.getValue( "name" ) );
        mapping.put( "target", atts.getValue( "target" ) );

        stack.push( mapping );
    }

    public void end(String localName) {
        Map<String,Object> data    = (Map)stack.pop();//bean mapping
        Map<String,Object> key     = (Map)stack.pop();//key
        Map<String,Object> mapping = (Map)stack.pop();//map
        Map<String,Object> parent  = (Map)stack.pop();//parent

        mapping.put( "bean" , data );
        mapping.put( "key" , key );
        
        stack.push( parent );
        
        if( "web-frame".equals( parent.get("@tag") ) ){
            List<Map<String,Object>> mappings = (List)parent.get( "mapping" );
            if( mappings == null ){
                mappings = new ArrayList();
                parent.put( "mapping", mappings );
            }
            mappings.add( mapping );
        }
        else
            stack.push( mapping );
    }

}
