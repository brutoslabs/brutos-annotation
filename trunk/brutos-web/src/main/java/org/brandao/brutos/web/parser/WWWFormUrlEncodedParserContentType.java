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

package org.brandao.brutos.web.parser;

import java.util.Properties;

import org.brandao.brutos.AbstractParserContentType;
import org.brandao.brutos.CodeGenerator;
import org.brandao.brutos.MutableMvcRequest;
import org.brandao.brutos.MutableRequestParserEvent;
import org.brandao.brutos.RequestParserException;
import org.brandao.brutos.mapping.BeanDecoder;
import org.brandao.brutos.web.bean.WWWFormUrlEncodedBeanDecoder;

/**
 * 
 * @author Brandao
 *
 */
public class WWWFormUrlEncodedParserContentType 
	extends AbstractParserContentType{
	
	
	public void parserContentType(MutableMvcRequest request,
			MutableRequestParserEvent requestParserInfo, 
			CodeGenerator codeGenerator, Properties config)
			throws RequestParserException {
	
        try{
        	BeanDecoder beanDecoder = new WWWFormUrlEncodedBeanDecoder();
        	beanDecoder.setCodeGenerator(codeGenerator);
            super.parser(request, requestParserInfo, beanDecoder, config, null);
        }
        catch(Throwable e){
        	throw new org.brandao.brutos.RequestParserException(e);
        }
		
	}
	
}
