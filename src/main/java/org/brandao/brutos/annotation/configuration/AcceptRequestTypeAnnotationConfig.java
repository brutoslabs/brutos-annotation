package org.brandao.brutos.annotation.configuration;

import org.brandao.brutos.ActionBuilder;
import org.brandao.brutos.ComponentRegistry;
import org.brandao.brutos.ControllerBuilder;
import org.brandao.brutos.DataType;
import org.brandao.brutos.annotation.AcceptRequestType;
import org.brandao.brutos.annotation.Action;
import org.brandao.brutos.annotation.Controller;
import org.brandao.brutos.annotation.Stereotype;
import org.brandao.brutos.annotation.configuration.AbstractAnnotationConfig;
import org.brandao.brutos.annotation.configuration.ActionEntry;
import org.brandao.brutos.annotation.configuration.AnnotationUtil;

@Stereotype(
	target=AcceptRequestType.class, 
	executeAfter={
		Controller.class,
		Action.class
	}
)
public class AcceptRequestTypeAnnotationConfig 
	extends AbstractAnnotationConfig {

	public boolean isApplicable(Object source) {
		if(source instanceof Class){
			return AnnotationUtil.isController((Class<?>) source);	
		}
		else
		if(source instanceof ActionEntry){
			return AnnotationUtil.isAction((ActionEntry)source);
		}
		else
			return false;
		
	}

	public Object applyConfiguration(Object source, Object builder,
			ComponentRegistry componentRegistry) {

		if(source instanceof ControllerBuilder){
			Class<?> clazz = (Class<?>)source;
			
			if(clazz.isAnnotationPresent(AcceptRequestType.class)){
				AcceptRequestType rm = clazz.getAnnotation(AcceptRequestType.class);
				ControllerBuilder b = (ControllerBuilder)builder;
				String[] values = rm.value();
				for(String v: values){
					b.addRequestType(DataType.valueOf(v));
				}
			}
		}
		else
		if(source instanceof ActionBuilder){
			ActionEntry action = (ActionEntry)source;
			
			if(action.isAnnotationPresent(AcceptRequestType.class)){
				AcceptRequestType rm = action.getAnnotation(AcceptRequestType.class);
				ActionBuilder b = (ActionBuilder)builder;
				String[] values = rm.value();
				for(String v: values){
					b.addRequestType(DataType.valueOf(v));
				}
			}
		}
		
		return builder;
	}

}
