package org.brandao.brutos.annotation.configuration;

import org.brandao.brutos.DataType;
import org.brandao.brutos.DispatcherType;
import org.brandao.brutos.annotation.Result;
import org.brandao.brutos.annotation.ThrowSafe;
import org.brandao.brutos.annotation.View;
import org.brandao.brutos.mapping.StringUtil;

public class ThrowActionConfig 
	extends ActionConfig{

	public ThrowActionConfig(ActionEntry actionEntry) {
		super(actionEntry);
	}

	public String getActionId(){
		return null;
	}

	public Class<? extends Throwable> getTarget(){
		Class<? extends Throwable>[] targets = 
				super.actionEntry.getAnnotation(ThrowSafe.class).target();
		return targets.length == 0? null : targets[0];
	}

	@SuppressWarnings("unchecked")
	public Class<? extends Throwable>[] getTargetAlias(){
		Class<? extends Throwable>[] targets = 
				super.actionEntry.getAnnotation(ThrowSafe.class).target();
		if(targets.length < 2){
			return new Class[0];
		}
		else{
			Class<? extends Throwable>[] alias = new Class[targets.length - 1];
			System.arraycopy(targets, 1, alias, 0, alias.length);
			return alias;
		}
	}
	
	public String getResultActionName(){
		String value = StringUtil.adjust(super.actionEntry.getAnnotation(ThrowSafe.class).name());
		
		return StringUtil.isEmpty(value) && super.actionEntry.isAnnotationPresent(Result.class)?
				super.getResultActionName() :
				value;
	}
	
	public boolean isRenderable(){
		boolean value = super.actionEntry.getAnnotation(ThrowSafe.class).rendered();
		return !value && super.actionEntry.isAnnotationPresent(View.class)?
				super.isRenderable() :
				value;
	}
	
	public boolean isResolvedView(){
		boolean value = super.actionEntry.getAnnotation(ThrowSafe.class).resolved();
		return !value && super.actionEntry.isAnnotationPresent(View.class)?
				super.isResolvedView() :
				value;
	}
	
	public String getActionView() {
		
		String value = StringUtil.adjust(super.actionEntry.getAnnotation(ThrowSafe.class).view());
		
		return StringUtil.isEmpty(value) && super.actionEntry.isAnnotationPresent(View.class)?
				super.getActionView() :
				value;
	}
	
	public DataType[] getRequestTypes(){
		throw new UnsupportedOperationException();
	}
	
	public DataType[] getResponseTypes(){
		throw new UnsupportedOperationException();
	}
	
	public DispatcherType getDispatcherType(){
		String value = StringUtil.adjust(super.actionEntry.getAnnotation(ThrowSafe.class).dispatcher());
		return StringUtil.isEmpty(value) && super.actionEntry.isAnnotationPresent(View.class)?
				DispatcherType.valueOf(value):
				super.getDispatcherType();
	}
	
	public String[] getAliasName(){
		throw new UnsupportedOperationException();
	}
	
}
