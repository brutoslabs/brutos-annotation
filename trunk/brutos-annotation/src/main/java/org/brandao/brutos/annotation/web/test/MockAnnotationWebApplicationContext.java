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

package org.brandao.brutos.annotation.web.test;

import java.util.Properties;
import org.brandao.brutos.BrutosConstants;
import org.brandao.brutos.annotation.web.AnnotationWebApplicationContext;
import org.brandao.brutos.io.ByteArrayResource;
import org.brandao.brutos.io.Resource;
import org.brandao.brutos.test.MockObjectFactory;
import org.brandao.brutos.test.MockRenderView;
import org.brandao.brutos.validator.JSR303ValidatorFactory;
import org.brandao.brutos.web.test.MockWebInvoker;

/**
 *
 * @author Brandao
 */
public class MockAnnotationWebApplicationContext extends
		AnnotationWebApplicationContext {

	public static final String IGNORE_RESOURCES = "org.brandao.brutos.mock.ignore_resources";

	public static final String XML_CONTENT = "org.brandao.brutos.mock.xml_content";

	private boolean ignoreResources;

	private String resource;

	@Override
	protected void overrideConfig() {
		Properties config = this.getConfiguration();
/*
		if (config.get(BrutosConstants.RENDER_VIEW_CLASS) == null)
			config.put(BrutosConstants.RENDER_VIEW_CLASS,
					MockRenderView.class.getName());

		if (config.get(BrutosConstants.INVOKER_CLASS) == null)
			config.put(BrutosConstants.INVOKER_CLASS,
					MockWebInvoker.class.getName());
*/
		if (config.get(BrutosConstants.OBJECT_FACTORY_CLASS) == null)
			config.put(BrutosConstants.OBJECT_FACTORY_CLASS,
					MockObjectFactory.class.getName());

		if (config.get(BrutosConstants.VALIDATOR_FACTORY_CLASS) == null)
			config.put(BrutosConstants.VALIDATOR_FACTORY_CLASS,
					JSR303ValidatorFactory.class.getName());

		super.overrideConfig();

		this.ignoreResources = "true".equals(config.getProperty(
				IGNORE_RESOURCES, "false"));
		this.resource = config.getProperty(XML_CONTENT);

		if (this.resource != null)
			super.setResources(new Resource[] { new ByteArrayResource(
					this.resource.getBytes()) });

		if (this.ignoreResources)
			super.setLocations(new String[] {});

	}

}
