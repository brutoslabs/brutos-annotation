package org.brandao.brutos.web;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.brandao.brutos.ActionType;
import org.brandao.brutos.mapping.Action;
import org.brandao.brutos.mapping.ActionID;
import org.brandao.brutos.mapping.Controller;
import org.brandao.brutos.web.mapping.WebAction;
import org.brandao.brutos.web.mapping.WebActionID;
import org.brandao.brutos.web.mapping.WebController;
import org.brandao.brutos.web.util.WebUtil;

public class WebActionType extends ActionType{

	public static final WebActionType PARAMETER = new WebActionType() {
		
		public String id(){
			return "PARAMETER";
		}

		public String name(){
			return "Parameter";
		}
		
		public String getControllerID(String className){
			return "/" + className;
		}

		public String getActionID(String actionName){
			return "/" + actionName;
		}
		
		public boolean isComposite(){
			return false;
		}

		public boolean isDelegate(){
			return true;
		}
		
		public boolean isValidControllerId(String value){
			try{
				WebUtil.checkURI(value, true);
				return true;
			}
			catch(Throwable e){
				return false;
			}
		}

		public boolean isValidActionId(String value){
			return true;
		}

		public List<ActionID> getIDs(String controllerID, Controller controller, 
				String actionID, Action action){
			//ações não possuem ids
			if(action != null){
				return null;
			}
			
			return Arrays.asList(new ActionID(controllerID));
		}
		
	};	
	
	public static final WebActionType HEADER = new WebActionType() {
		
		public String id(){
			return "HEADER";
		}

		public String name(){
			return "Header";
		}
		
		public String getControllerID(String className){
			return "/" + className;
		}

		public String getActionID(String actionName){
			return "/" + actionName;
		}
		
		public boolean isComposite(){
			return false;
		}

		public boolean isDelegate(){
			return true;
		}
		
		public boolean isValidControllerId(String value){
			try{
				WebUtil.checkURI(value, true);
				return true;
			}
			catch(Throwable e){
				return false;
			}
		}

		public boolean isValidActionId(String value){
			return true;
		}

		public List<ActionID> getIDs(String controllerID, Controller controller, 
				String actionID, Action action){
			return PARAMETER.getIDs(controllerID, controller, 
					actionID, action);
		}
		
	};
	
	public static final WebActionType HIERARCHY = new WebActionType() {
		
		public String id(){
			return "HIERARCHY";
		}

		public String name(){
			return "Hierarchy";
		}

		public String getControllerID(String className){
			return "/" + className;
		}

		public String getActionID(String actionName){
			return "/" + actionName;
		}
		
		public boolean isComposite(){
			return true;
		}

		public boolean isDelegate(){
			return false;
		}
		
		public boolean isValidControllerId(String value){
			try{
				WebUtil.checkURI(value, true);
				return true;
			}
			catch(Throwable e){
				return false;
			}
		}
		
		public boolean isValidActionId(String value){
			try{
				WebUtil.checkURI(value, true);
				return true;
			}
			catch(Throwable e){
				return false;
			}
		}

		public List<ActionID> getIDs(String controllerID, Controller controller, 
				String actionID, Action action){
			
			List<ActionID> result       = new ArrayList<ActionID>();
			WebController webController = (WebController)controller;
			WebAction webAction         = (WebAction)action;

			List<String> controllerIds = new ArrayList<String>();
			List<String> actionIds     = new ArrayList<String>();
			
			if(controllerID.equals(controller.getId())){
				controllerIds.add(controller.getId());
				controllerIds.addAll(controller.getAlias());
			}
			else{
				controllerIds.add(controllerID);
			}
			
			for(String cID: controllerIds){
				
				result.add(new WebActionID(cID, webController.getRequestMethod()));

				if(action == null)
					continue;
				
				if(actionID.equals(webAction.getName())){
					actionIds.add(action.getName());
					actionIds.addAll(action.getAlias());
				}
				else{
					actionIds.add(actionID);
				}
				
				for(String aID: actionIds){
					result.add(new WebActionID(cID + aID, webAction.getRequestMethod()));
				}
				
			}
			
			return result;
		}
		
	};

	public static final WebActionType DETACHED = new WebActionType() {

		public String id(){
			return "DETACHED";
		}

		public String name(){
			return "Detached";
		}

		public String getControllerID(String className){
			throw new UnsupportedOperationException();
		}

		public String getActionID(String actionName){
			return "/" + actionName;
		}
		
		public boolean isComposite(){
			return false;
		}

		public boolean isDelegate(){
			return false;
		}
		
		public boolean isValidControllerId(String value){
			return value == null;
		}
		
		public boolean isValidActionId(String value){
			try{
				WebUtil.checkURI(value, true);
				return true;
			}
			catch(Throwable e){
				return false;
			}
		}

		public List<ActionID> getIDs(String controllerID, Controller controller, 
				String actionID, Action action){
			
			List<ActionID> result       = new ArrayList<ActionID>();
			WebAction webAction         = (WebAction)action;

			List<String> actionIds     = new ArrayList<String>();
			
			if(action == null){
				return result;
			}
				
			if(actionID.equals(webAction.getName())){
				actionIds.add(action.getName());
				actionIds.addAll(action.getAlias());
			}
			else{
				actionIds.add(actionID);
			}
			
			for(String aID: actionIds){
				result.add(new WebActionID(aID, webAction.getRequestMethod()));
			}
				
			return result;
		}
		
	};

	private final static Map<String, WebActionType> defaultTypes = 
			new HashMap<String, WebActionType>();
	
	static {
		defaultTypes.put(PARAMETER.id(),	PARAMETER);
		defaultTypes.put(HIERARCHY.id(),	HIERARCHY);
		defaultTypes.put(DETACHED.id(),		DETACHED);
	}

	public static WebActionType valueOf(String value) {
		if (value == null)
			return null;
		else
			return defaultTypes.get(value);
	}
	
}
