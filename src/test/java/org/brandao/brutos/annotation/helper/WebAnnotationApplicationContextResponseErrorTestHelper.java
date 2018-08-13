package org.brandao.brutos.annotation.helper;

import org.brandao.brutos.annotation.Action;
import org.brandao.brutos.annotation.ActionStrategy;
import org.brandao.brutos.annotation.View;
import org.brandao.brutos.annotation.web.ResponseError;
import org.brandao.brutos.annotation.web.WebActionStrategyType;
import org.brandao.brutos.web.HttpStatus;

public class WebAnnotationApplicationContextResponseErrorTestHelper {

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
		
		@ResponseError(code=HttpStatus.NOT_FOUND, target=NullPointerException.class)
		@ActionStrategy(WebActionStrategyType.DETACHED)
		public static class ControllerLevelController{
			
			@Action("/action")
			@View("view")
			public void action(){
				throw new NullPointerException();
			}
			
		}

		/* Alteração da configuração de uma exceção */
		
		@ResponseError(
				code=HttpStatus.BAD_REQUEST,
				rendered=true,
				view="exp", 
				target=NullPointerException.class
		)
		@ActionStrategy(WebActionStrategyType.DETACHED)
		public static class ControllerLevelExceptionWithViewController{
			
			@Action("/action")
			@View("view")
			public void action(){
				throw new NullPointerException();
			}
			
		}

		/*---- Action level----*/
		
		
		/* Configuração padrão */
		
		@ActionStrategy(WebActionStrategyType.DETACHED)
		public static class DefaultConfigActionController{
			
			@Action("/action")
			@View("view")
			public void action(){
				throw new NullPointerException();
			}
			
		}

		/* Alteração da configuração padrão */
		
		@ResponseError(code=HttpStatus.BAD_REQUEST, target=NullPointerException.class)
		@ActionStrategy(WebActionStrategyType.DETACHED)
		public static class ActionLevelController{
			
			@Action("/action")
			@View("view")
			@ResponseError(code=HttpStatus.NOT_FOUND, target=NullPointerException.class)
			public void action(){
				throw new NullPointerException();
			}
			
		}

		/* Alteração da configuração de uma exceção */
		
		@ResponseError(
				code=HttpStatus.CONFLICT,
				rendered=true,
				view="xxx", 
				target=NullPointerException.class
			)
		@ActionStrategy(WebActionStrategyType.DETACHED)
		public static class ActionLevelExceptionWithViewController{
			
			@Action("/action")
			@View("view")
			@ResponseError(
					code=HttpStatus.BAD_REQUEST,
					rendered=true,
					view="exp", 
					target=NullPointerException.class
			)
			public void action(){
				throw new NullPointerException();
			}
			
		}

		/* Exceção delegada a um método */
		
		@ActionStrategy(WebActionStrategyType.DETACHED)
		public static class ExceptionWithMethodDefaultConfigController{
			
			@Action("/action")
			@View("view")
			public void action(){
				throw new NullPointerException();
			}

			@ResponseError(code=HttpStatus.BAD_REQUEST, target=NullPointerException.class)
			public void npeException(){
				
			}
		}
		
	}
	
}
