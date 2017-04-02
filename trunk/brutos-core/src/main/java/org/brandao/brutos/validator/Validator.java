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

package org.brandao.brutos.validator;

import java.util.Properties;
import org.brandao.brutos.mapping.Action;
import org.brandao.brutos.mapping.ConstructorArgBean;
import org.brandao.brutos.mapping.ConstructorBean;
import org.brandao.brutos.mapping.ParameterAction;
import org.brandao.brutos.mapping.PropertyBean;
import org.brandao.brutos.mapping.PropertyController;

/**
 * 
 * @author Brandao
 */
public interface Validator {

	void configure(Properties config);

	Properties getConfiguration();

	void validate(ConstructorArgBean source, Object value)
			throws ValidatorException;

	void validate(ConstructorBean source, Object factoryInstance, Object[] value)
			throws ValidatorException;

	void validate(ConstructorBean source, Object factoryInstance, Object value)
			throws ValidatorException;

	void validate(PropertyBean source, Object beanInstance, Object value)
			throws ValidatorException;

	void validate(PropertyController source, Object controllerInstance,
			Object value) throws ValidatorException;

	void validate(ParameterAction source, Object controllerInstance,
			Object value) throws ValidatorException;

	void validate(Action source, Object controller, Object[] value)
			throws ValidatorException;

	void validate(Action source, Object controller, Object value)
			throws ValidatorException;

}
