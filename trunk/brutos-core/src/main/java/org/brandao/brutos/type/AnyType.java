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
public class AnyType implements Type {

	private Class<?> type;

	public AnyType(Class<?> type) {
		this.type = type;
	}

	public Object convert(Object value) {
		if (value == null)
			return null;
		else if (type.isAssignableFrom(value.getClass()))
			return value;
		else
			throw new UnknownTypeException(value.getClass().getSimpleName());
	}

	public void show(MvcResponse response, Object value) throws IOException {
		response.process(value);
	}

	public Class getClassType() {
		return this.type;
	}

	public void setClassType(Class value) {
		this.type = value;
	}

	public boolean isAlwaysRender() {
		return false;
	}

}
