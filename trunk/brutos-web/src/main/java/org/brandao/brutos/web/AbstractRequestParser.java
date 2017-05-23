/*
 * Brutos Web MVC http://www.brutosframework.com.br/
 * Copyright (C) 2009-2017 Afonso Brandao. (afonso.rbn@gmail.com)
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

package org.brandao.brutos.web;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author Brandao
 */
@Deprecated
public abstract class AbstractRequestParser 
	implements ConfigurableRequestParser{

	protected Map<String, ParserContentType> parsers;
	
	public AbstractRequestParser(){
		this.parsers = new HashMap<String, ParserContentType>();
	}
	
	public synchronized void registryParser(String contentType, 
			ParserContentType parser) throws RequestParserException{
		
		if(this.parsers.containsKey(contentType)){
			throw new RequestParserException("Parser already registered: " + contentType);
		}
		
		this.parsers.put(contentType, parser);
	}
	
	public synchronized void removeParser(String contentType) throws RequestParserException{
		
		if(!this.parsers.containsKey(contentType)){
			throw new RequestParserException("Parser not registered: " + contentType);
		}
		
		this.parsers.remove(contentType);
	}
	
}
