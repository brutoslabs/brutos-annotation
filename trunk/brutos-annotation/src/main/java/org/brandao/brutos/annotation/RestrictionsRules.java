/*
 * Brutos Web MVC http://www.brutosframework.com.br/
 * Copyright (C) 2009 Afonso Brandao. (afonso.rbn@gmail.com)
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

package org.brandao.brutos.annotation;

/**
 * Descreve as regras de validação pré-definidas.
 * 
 * @author Brandao
 */
public interface RestrictionsRules {
    
    /**
     * Somente são considerados válidos os valores maiores do que o definido. Somente permitido
     * para valores numéricos.
     */
    public static final String MIN       = "min";
    
    /**
     * Somente são considerados válidos os textos com tamanho maior que o definido.
     * Somente permitido para tipos texto.
     */
    public static final String MINLENGTH = "minlength";
    
    /**
     * Somente são considerados válidos os valores menores que o definido. Somente permitido
     * para valores numéricos.
     */
    public static final String MAX       = "max";
    
    /**
     * Somente são considerados válidos os textos com tamanho menor que o definido.
     * Somente permitido para tipos texto.
     */
    public static final String MAXLENGTH = "maxlength";
    
    /**
     * Somente são considerados válidos os valores que correspondem à expressão regular definida.
     * Somente permitido para tipos texto.
     */
    public static final String MATCHES   = "matches";
    
    /**
     * Indica que o valor é obrigatório.
     */
    public static final String REQUIRED  = "required";
    
    /**
     * Somente são considerados válidos os valores iguais ao definido.
     */
    public static final String EQUAL     = "equal";
    
}
