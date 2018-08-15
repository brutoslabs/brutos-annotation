package org.brandao.brutos.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Define os tipos de resposta suportados por uma ação.
 * 
 * @author Brandao
 *
 */
@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface ResponseType {

	/**
	 * Coleção de tipos.
	 */
	String[] value();
	
}
