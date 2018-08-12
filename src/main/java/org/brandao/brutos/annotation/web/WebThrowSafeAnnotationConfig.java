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

package org.brandao.brutos.annotation.web;

import org.brandao.brutos.ActionBuilder;
import org.brandao.brutos.BrutosException;
import org.brandao.brutos.ComponentRegistry;
import org.brandao.brutos.ControllerBuilder;
import org.brandao.brutos.DataType;
import org.brandao.brutos.DispatcherType;
import org.brandao.brutos.annotation.Action;
import org.brandao.brutos.annotation.Controller;
import org.brandao.brutos.annotation.Stereotype;
import org.brandao.brutos.annotation.ThrowSafe;
import org.brandao.brutos.annotation.configuration.ActionEntry;
import org.brandao.brutos.annotation.configuration.AnnotationUtil;
import org.brandao.brutos.annotation.configuration.ThrowableEntry;
import org.brandao.brutos.annotation.configuration.web.WebActionAnnotationConfig;
import org.brandao.brutos.annotation.configuration.web.WebActionConfig;
import org.brandao.brutos.annotation.configuration.web.WebThrowableEntry;
import org.brandao.brutos.mapping.MappingException;
import org.brandao.brutos.mapping.StringUtil;
import org.brandao.brutos.web.WebActionBuilder;
import org.brandao.brutos.web.WebControllerBuilder;
import org.brandao.brutos.web.WebThrowSafeBuilder;

/**
 * 
 * @author Brandao
 *
 */
@Stereotype(
	target=
		ThrowSafe.class, 
	minorVersion=
		1,
	executeAfter= { 
		Action.class,
		Controller.class 
	}
)
public class WebThrowSafeAnnotationConfig 
	extends WebActionAnnotationConfig{

	public boolean isApplicable(Object source) {
		return
			(source instanceof ActionEntry && AnnotationUtil.isExceptionAction((ActionEntry)source)) ||
			source instanceof ThrowableEntry;
	}

	public Object applyConfiguration(Object source, Object builder,
			ComponentRegistry componentRegistry) {

		try{
			if(source instanceof ThrowableEntry){
				return applyThrowSafe(source, builder, componentRegistry);
			}
			else{
				return this.applyThrowSafeAction(source, builder, componentRegistry);
			}
		} catch (Exception e) {
			throw new BrutosException("can't create mapping exception", e);
		}

	}
	
	protected Object applyThrowSafeAction(Object source, Object builder,
			ComponentRegistry componentRegistry) {
		
		//vars
		ActionEntry actionEntry                = (ActionEntry) source;
		WebActionConfig actionConfig           = new WebActionConfig(actionEntry);
		WebControllerBuilder controllerBuilder = (WebControllerBuilder) builder;
		String actionID                        = actionConfig.getActionId();
		String result                          = actionConfig.getResultActionName();
		String view                            = actionConfig.getActionView();
		boolean resultRendered                 = actionConfig.isResultRenderable();
		boolean rendered                       = actionConfig.isRenderable();
		boolean resolved                       = actionConfig.isResolvedView();
		String executor                        = actionConfig.getActionExecutor();
		DataType[] requestTypes                = actionConfig.getRequestTypes();
		DataType[] responseTypes               = actionConfig.getResponseTypes();
		int responseStatus                     = actionConfig.getResponseStatus();
		DispatcherType dispatcher              = actionConfig.getDispatcherType();
		ResponseError responseError            = actionEntry.getAnnotation(ResponseError.class);
		String exceptionName                   = StringUtil.isEmpty(responseError.name())? result : StringUtil.adjust(responseError.name());
		Class<?> target                        = responseError.target();
		int responseErrorCode                  = responseStatus != 0? responseStatus : responseError.code();
		String reason                          = StringUtil.adjust(responseError.reason());
		
		//registry
		WebThrowSafeBuilder actionBuilder = 
				(WebThrowSafeBuilder)
				controllerBuilder.addThrowable(
						target, 
						executor, 
						responseErrorCode, 
						reason, 
						rendered ? view : null, 
						dispatcher, 
						rendered ? resolved : true, 
						exceptionName, 
						resultRendered
				);

		if(requestTypes != null){
			for(DataType type: requestTypes){
				actionBuilder.addRequestType(type);
			}
		}

		if(responseTypes != null){
			for(DataType type: responseTypes){
				actionBuilder.addResponseType(type);
			}
		}
		
		addParameters(actionBuilder.buildParameters(), actionEntry, componentRegistry);

		addResultAction(actionBuilder, actionEntry.getResultAction(), componentRegistry);
		
		return actionBuilder;		
	}
	
	protected Object applyThrowSafe(Object source, Object builder,
			ComponentRegistry componentRegistry) {

		if (builder instanceof ActionBuilder)
			addThrowSafe((ActionBuilder) builder, componentRegistry,
					(ThrowableEntry) source);
		else
			addThrowSafe((ControllerBuilder) builder, componentRegistry,
					(ThrowableEntry) source);

		return builder;
	}
	
	protected void addThrowSafe(ActionBuilder actionBuilder,
			ComponentRegistry componentRegistry, ThrowableEntry source) {

		WebActionBuilder builder    = (WebActionBuilder)actionBuilder;
		WebThrowableEntry throwSafe = (WebThrowableEntry)source;
		
		if (throwSafe.isEnabled()) {
			builder.addThrowable(
					throwSafe.getResponseError(), 
					throwSafe.getReason(),
					throwSafe.getTarget(),
					throwSafe.isRendered() ? throwSafe.getView() : null,
					throwSafe.getName(), 
					throwSafe.getDispatcher(),
					throwSafe.isRendered() ? throwSafe.isResolved() : true
			);
		}
	}

	protected void addThrowSafe(ControllerBuilder controllerBuilder,
			ComponentRegistry componentRegistry, ThrowableEntry source) {

		WebControllerBuilder builder = (WebControllerBuilder)controllerBuilder;
		WebThrowableEntry throwSafe  = (WebThrowableEntry)source;
		
		if (throwSafe.isEnabled()) {
			builder.addThrowable(
					throwSafe.getResponseError(), 
					throwSafe.getReason(),
					throwSafe.getTarget(),
					throwSafe.isRendered() ? throwSafe.getView() : null,
					throwSafe.getName(), 
					throwSafe.getDispatcher(),
					throwSafe.isRendered() ? throwSafe.isResolved() : true
			);
		}

	}
	
}
