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

package org.brandao.brutos.type;

import java.io.IOException;
import org.brandao.brutos.MvcResponse;

/**
 * 
 * @author Brandao
 */
public class DoubleType extends AbstractType implements Type {

	private static final double DEFAULT_VALUE = 0.0;

	public DoubleType() {
	}

	public Class<?> getClassType() {
		return Double.TYPE;
	}

	public Object getValue(Object value) {
		return null;
	}

	public Object convert(Object value) {
		if (value instanceof Double)
			return value;
		else if (value instanceof String)
			return ((String) value).isEmpty() ? DEFAULT_VALUE : Double
					.valueOf((String) value);
		else if (value == null)
			return null;
		else
			throw new UnknownTypeException();
	}

	public void setValue(Object value) throws IOException {
	}

	public void show(MvcResponse response, Object value) throws IOException {
		response.process(value);
	}

}
