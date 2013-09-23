/*
  Brutos Web MVC http://brutos.sourceforge.net/
  Copyright (C) 2009 Afonso Brandão. (afonso.rbn@gmail.com)
*/
package br.brandao.type;

import br.brandao.beans.MyBean;
import br.brandao.controller.IndexController;
import java.io.IOException;
import org.brandao.brutos.Invoker;
import org.brandao.brutos.MvcResponse;
import org.brandao.brutos.mapping.Bean;
import org.brandao.brutos.mapping.Controller;
import org.brandao.brutos.type.Type;
import org.brandao.brutos.type.TypeFactory;
import org.brandao.brutos.web.ConfigurableWebApplicationContext;

/**
 *
 * @author Afonso Brandao
 */
public class MyBeanType implements Type,TypeFactory{

    public Class getClassType() {
        return MyBean.class;
    }

    /**
     * @deprecated 
     * 
     */
    public Object getValue(Object value) {
        return null;
    }
    
    public Object convert(Object value) {
        ConfigurableWebApplicationContext context =
            (ConfigurableWebApplicationContext) Invoker.getCurrentApplicationContext();
        
        Controller controller =
                context.getControllerManager()
                    .getController(IndexController.class);
        Bean mapping = controller.getBean( "myBean" );
        return mapping.getValue();
    }

    /**
     * @deprecated 
     * @param value
     * @throws IOException 
     */
    public void setValue(Object value) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    public void show(MvcResponse response, Object value) throws IOException {
        
    }

    public Type getInstance() {
        return this;
    }

    public boolean matches(Class type) {
        return MyBean.class == type;
    }

}
