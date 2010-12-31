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

import org.brandao.brutos.Configuration;
import org.brandao.brutos.validator.RestrictionRules;

/**
 *
 * @author Afonso Brandao
 */
public class PropertyBuilder{

    private Configuration config;
    
    public PropertyBuilder( Configuration config){
        this.config = config;
    }

    /**
     * Adiciona uma nova restri��o.
     *
     * @param ruleId Tipo da restri��o. Os valores s�o obtido em RestrictionRules.
     * @param value Valor da restri��o.
     * @return Construtor da restri��o.
     */
    public PropertyBuilder addRestriction( RestrictionRules ruleId, Object value ){
        config.put( ruleId.toString(), value );
        return this;
    }

    public PropertyBuilder setMessage( String message ){
        config.setProperty("message", message );
        return this;
    }

}
