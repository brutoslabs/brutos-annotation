package org.brandao.brutos.annotation.helper;

import org.brandao.brutos.annotation.Action;
import org.brandao.brutos.annotation.ActionStrategy;
import org.brandao.brutos.annotation.View;
import org.brandao.brutos.annotation.web.ResponseError;
import org.brandao.brutos.annotation.web.ResponseErrors;
import org.brandao.brutos.annotation.web.WebActionStrategyType;
import org.brandao.brutos.web.HttpStatus;

public class WebAnnotationApplicationContextResponseErrorsTestHelper {

	public static class Values{
		
	}

	public static class Entities{
		
	}
	
	public static class Controllers{
		
		/* Configuração padrão */
		
		@ActionStrategy(WebActionStrategyType.DETACHED)
		public static class DefaultConfigController{
			
			@Action("/action")
			@View("view")
			public void action(){
				throw new NullPointerException();
			}
			
		}

		/* Alteração da configuração padrão */
		
		@ResponseErrors(code=HttpStatus.NOT_FOUND)
		@ActionStrategy(WebActionStrategyType.DETACHED)
		public static class ControllerLevelController{
			
			@Action("/action")
			@View("view")
			public void action(){
				throw new NullPointerException();
			}
			
		}

		/* Alteração da configuração de uma exceção */
		
		@ResponseErrors(
			code=HttpStatus.NOT_FOUND,
			exceptions=@ResponseError(code=HttpStatus.BAD_REQUEST, target=NullPointerException.class)
		)
		@ActionStrategy(WebActionStrategyType.DETACHED)
		public static class ControllerLevelExceptionController{
			
			@Action("/action")
			@View("view")
			public void action(){
				throw new NullPointerException();
			}
			
		}

		/* Alteração da configuração de uma exceção */
		
		@ResponseErrors(
			code=HttpStatus.NOT_FOUND,
			exceptions=
				@ResponseError(
					code=HttpStatus.BAD_REQUEST,
					rendered=true,
					view="exp", 
					target=NullPointerException.class
				)
		)
		@ActionStrategy(WebActionStrategyType.DETACHED)
		public static class ControllerLevelExceptionWithViewController{
			
			@Action("/action")
			@View("view")
			public void action(){
				throw new NullPointerException();
			}
			
		}
		
	}
	
}
