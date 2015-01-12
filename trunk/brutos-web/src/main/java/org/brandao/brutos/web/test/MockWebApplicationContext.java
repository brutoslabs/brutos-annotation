/*
 * Brutos Web MVC http://www.brutosframework.com.br/
 * Copyright (C) 2009 Afonso Brandao. (afonso.rbn@gmail.com)
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

package org.brandao.brutos.web.test;


import org.brandao.brutos.web.ConfigurableWebApplicationContext;
import org.brandao.brutos.web.WebApplicationContextWrapper;

/**
 *
 * @author Afonso Brandao
 */
public class MockWebApplicationContext extends WebApplicationContextWrapper{

    private static ConfigurableWebApplicationContext app;

    public MockWebApplicationContext(){
        super( app );
    }

    public static void setCurrentApplicationContext( ConfigurableWebApplicationContext apps ){
        app = apps;
    }

}
