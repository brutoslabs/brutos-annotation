package org.brandao.brutos.annotation.helper.target.fail;

import org.brandao.brutos.annotation.Controller;
import org.brandao.brutos.annotation.Target;
import org.brandao.brutos.annotation.helper.target.app1.Test1TargetBean;

@Controller("/controller")
public class Test1FailTargetController {

	@Target(Test1TargetBean.class)
	public Object property1;
	
}