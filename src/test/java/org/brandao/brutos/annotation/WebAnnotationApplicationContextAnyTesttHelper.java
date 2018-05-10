package org.brandao.brutos.annotation;

import org.brandao.brutos.annotation.web.WebActionStrategyType;

public class WebAnnotationApplicationContextAnyTesttHelper {
	
	public static class PropertyType{
		
		public String commonField;
		
	}

	public static class PropertyTypeA extends PropertyType{

		public String fieldA;
		
	}

	public static class PropertyTypeB extends PropertyType{

		public String fieldB;
		
	}
	
	public static class BeanAnyField{

		@Any(
			metaBean=@Basic(bean="type"),
			metaValues={
				@MetaValue(name="A", target=PropertyTypeA.class),
				@MetaValue(name="B", target=PropertyTypeB.class)
			}
		)
		public PropertyType property;
		
	}
	
	@ActionStrategy(WebActionStrategyType.DETACHED)
	@Controller
	public static class BeanAnyFieldControllerTest{
	
		public void actionAction(@Basic(bean="prop")BeanAnyField field){
		}
		
	}
	
}
