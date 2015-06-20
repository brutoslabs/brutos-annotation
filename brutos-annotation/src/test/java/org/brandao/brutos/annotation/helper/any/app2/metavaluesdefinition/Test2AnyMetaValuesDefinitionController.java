package org.brandao.brutos.annotation.helper.any.app2.metavaluesdefinition;

import java.util.List;

import org.brandao.brutos.annotation.Any;
import org.brandao.brutos.annotation.Basic;
import org.brandao.brutos.annotation.Controller;
import org.brandao.brutos.annotation.ElementCollection;
import org.brandao.brutos.annotation.MetaValue;
import org.brandao.brutos.annotation.helper.any.app2.Property;

@Controller("/controller")
public class Test2AnyMetaValuesDefinitionController {

	@Basic(bean="propertyA")
	@ElementCollection(
		any=
			@Any(
				metaBean=@Basic(bean="propertyType"),
				metaType=String.class,
				metaValuesDefinition=TestMetaValuesDefinition.class
			)
		)
	public List<Property> property1;
	
	private List<Property> property2;

	public List<Property> getProperty2() {
		return property2;
	}

	@Basic(bean="propertyB")
	@ElementCollection(
		any=
			@Any(
				metaBean=@Basic(bean="propertyType2"),
				metaType=String.class,
				metaValuesDefinition=TestMetaValuesDefinition.class
			)
		)
	public void setProperty2(List<Property> property2) {
		this.property2 = property2;
	}
	
	
}