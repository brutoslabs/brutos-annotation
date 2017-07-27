package org.brandao.brutos.web.bean;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.brandao.brutos.BrutosException;
import org.brandao.brutos.CodeGenerator;
import org.brandao.brutos.FetchType;
import org.brandao.brutos.ProxyFactory;
import org.brandao.brutos.mapping.Bean;
import org.brandao.brutos.mapping.BeanDecoder;
import org.brandao.brutos.mapping.BeanDecoderException;
import org.brandao.brutos.mapping.CollectionBean;
import org.brandao.brutos.mapping.ConstructorArgBean;
import org.brandao.brutos.mapping.ConstructorBean;
import org.brandao.brutos.mapping.DependencyBean;
import org.brandao.brutos.mapping.DependencyException;
import org.brandao.brutos.mapping.Element;
import org.brandao.brutos.mapping.Key;
import org.brandao.brutos.mapping.MapBean;
import org.brandao.brutos.mapping.MappingException;
import org.brandao.brutos.mapping.MetaBean;
import org.brandao.brutos.mapping.PropertyBean;
import org.brandao.brutos.mapping.SimpleKeyMap;
import org.brandao.brutos.mapping.StringUtil;
import org.brandao.brutos.mapping.UseBeanData;
import org.brandao.brutos.type.ArrayType;
import org.brandao.brutos.type.CollectionType;
import org.brandao.brutos.type.Type;

public class WWWFormUrlEncodedBeanDecoder 
	implements BeanDecoder{

	private CodeGenerator codeGenerator;

	public void setCodeGenerator(CodeGenerator value) {
		this.codeGenerator = value;
	}
	
	/* useBeanData */
	
	public Object decode(UseBeanData entity, FetchType fetchType, Object data)
			throws BeanDecoderException {
		try{
			return this.getValue(entity, fetchType, data);
		}
		catch(Throwable e){
			throw new BeanDecoderException(e);
		}
	}

	public Object getValue(UseBeanData entity, FetchType fetchType, Object data) {

		if(fetchType == null){
			fetchType = entity.getFetchType();
		}
		
		if(fetchType.equals(FetchType.LAZY)){
			ProxyFactory proxyFactory = 
					this.codeGenerator.getProxyFactory(entity.getClassType());
			return proxyFactory.getNewProxy(entity, data, this);
		}
		
		if (!entity.isNullable()) {
			if(entity.getMetaBean() != null){
				Object value =
					this.getValue(
						entity.getMetaBean(),
						entity.getName() == null ? 
							null : 
							entity.getName() + entity.getMetaBean().getSeparator(),
						-1
					);
				
				return entity.getType().convert(value);
			}
			else
			if(entity.getMapping() != null) {
				Object value =
					this.getValue(
						entity.getMapping(),
						entity.getName() == null?
							null :
							entity.getName() + entity.getMapping().getSeparator(),
						-1);
					
						
				return entity.getType().convert(value);
			}
			else
			if(entity.getStaticValue() != null){
				return entity.getType().convert(entity.getStaticValue());
			}
			else{
				Type type = entity.getType();
				Object value;
				
				if(type instanceof CollectionType || type instanceof ArrayType){
					value = 
						entity.getName() == null? 
							null : 
							entity.getScope().getCollection(entity.getName());
				}
				else{
					value = 
							entity.getName() == null? 
								null : 
								entity.getScope().get(entity.getName());
				}
				return type.convert(value);
			}
		}
		
		return null;
	}	

	public Object getValue(MetaBean metaBean, String prefix, long index) {
		String key;
		
		if(prefix == null){
			key = metaBean.getName();
		}
		else{
			key = prefix + metaBean.getName();
		}
		
		Object metaValue = metaBean.getScope().get(key);
		metaValue = metaBean.getType().convert(metaValue);

		if (metaValue == null)
			return null;

		DependencyBean bean = metaBean.getMetaValues().get(metaValue);

		if (bean == null){
			throw new MappingException("bean not found: " + metaValue);
		}

		return this.getValue(bean, null, prefix, index, true);
	}
	
	
	/* dependencyBean */
	
	public Object decode(DependencyBean dependencyBean, FetchType fetchType,
			Object data) throws BeanDecoderException {
		try{
			Map<String,Object> dta = new HashMap<String, Object>();
			
			return this.getValue(
				dependencyBean, fetchType, 
				(String)dta.get("prefix"), 
				(Integer)dta.get("index"),
				(Boolean)dta.get("updatePrefix"));
		}
		catch(Throwable e){
			throw new BeanDecoderException(e);
		}
	}

	private Object getValue(DependencyBean entity, FetchType fetchType, 
			String prefix, long index, boolean updatePrefix) {
		
		if(fetchType == null){
			fetchType = entity.getFetchType();
		}
		
		if(fetchType.equals(FetchType.LAZY)){
			Map<String,Object> data = new HashMap<String, Object>();
			data.put("prefix",       prefix);
			data.put("index",        index);
			data.put("updatePrefix", updatePrefix);
			
			ProxyFactory proxyFactory = 
					this.codeGenerator.getProxyFactory(entity.getClassType());
			return proxyFactory.getNewProxy(entity, data, this);
		}
		
		Object result;

		if (entity.getMapping() != null) {
			Bean dependencyBean = entity.getController().getBean(entity.getMapping());

			if (dependencyBean == null){
				throw new BrutosException("mapping not found: " + entity.getMapping());
			}

			if(updatePrefix){
				prefix = this.getNewPrefix(entity, prefix, true);
			}
			
			Object value = this.getValue(dependencyBean, prefix, -1);
			return entity.getType().convert(value);
		}
		else
		if (entity.getMetaBean() == null) {
			if (entity.isStatic()){
				return entity.getValue();
			}
			else
			if(entity.isNullable()){
				return null;
			}
			else{
				if(updatePrefix){
					prefix = this.getNewPrefix(entity, prefix, false);
				}
				result = entity.getScope().get(prefix);
				return entity.getType().convert(result);
			}

		}
		else{
			if(updatePrefix){
				prefix = this.getNewPrefix(entity, prefix, true);
			}
			result = this.getValue(entity.getMetaBean(), prefix, -1);
			return entity.getType().convert(result);
		}
		
	}
	
	/* bean */
	
	private Object getValue(Bean entity, String prefix, long index) {
		
		if(entity.isCollection()){
			return this.getValueCollection((CollectionBean)entity, prefix, index);
		}
		else
		if(entity.isMap()){
			return this.getValueMap((MapBean)entity, prefix, index);
		}
		else{
			return this.getValueBean(entity, prefix, index);
		}
	}

	private Object getValueBean(Bean entity, String prefix, long index) {
		
		ConstructorBean constructorBean = entity.getConstructor();
		Object value = this.getInstance(constructorBean, prefix, index);
		
		if(value == null){
			return null;
		}
		
		Map<String, PropertyBean> props = entity.getFields();
		
		boolean exist =
				constructorBean.size() > 0 ||
				constructorBean.isMethodFactory() ||
				props.isEmpty();
		
		for(PropertyBean prop: props.values()){
			try{
				if(!prop.canSet()){
					continue;
				}
				
				Object p = this.getValue(prop, null, prefix, index, true);
				
				if(p != null){
					exist = true;
					prop.setValueInSource(value, p);
				}
			}
			catch(Throwable ex){
				throw new DependencyException("fail parse property: " + prop.getName(), ex);
			}
		}
		
		return exist? value : null;
	}
	
	/* collection */
	
	private Object getValueCollection(CollectionBean entity, String prefix, long index) {
		Element e = (Element)entity.getCollection();
		
		if(e.getParameterName() != null){
			return this.getValueCollectionObject(entity, e, prefix, index);
		}
		else{
			return this.getValueCollectionSimple(entity, e, prefix, index);
		}
	}

	/*
	 * root.bean.element[0]           = <value>
	 * root.bean.property             = <value>
	 * root.bean.property2            = <value>
	 * root.bean.element[0].property1 = <value>
	 * root.bean.element[1].property1 = <value>
	 * root.bean.element[2].property1 = <value>
	 * 
	 * */
	@SuppressWarnings("unchecked")
	public Object getValueCollectionObject(CollectionBean entity, Element e,
			String prefix, long index) {
		
		Collection<Object> destValue = (Collection<Object>)this.getValueBean(entity, prefix, index);
	
		String newPrefix = null;
		
		if(StringUtil.isEmpty(prefix)){
			newPrefix = e.getParameterName();
		}
		else
		if(!prefix.endsWith(entity.getSeparator())){
			newPrefix = prefix + entity.getSeparator() + e.getParameterName();
		}
		else{
			newPrefix = prefix + e.getParameterName();
		}
		
		int max = entity.getMaxItens() + 1;
		
		for(int i=0;i<max;i++){
			String ePreifx = 
				newPrefix + 
				entity.getIndexFormat().replace("$index", String.valueOf(i));
			
			/*
			if(e.getMapping() != null || e.getMetaBean() != null){
				ePreifx += entity.getSeparator();
			}
			*/
			
			Object element = this.getValue(e, FetchType.EAGER, ePreifx, -1, false);
			
			if(element != null){
				destValue.add(element);
			}
			else{
				break;
			}
		}
		
		if(destValue.size() > max){
			throw new DependencyException(destValue + " > " + max);
		}
		
		return destValue.isEmpty()? null : destValue;
	}
	
	/*
	 * root.bean[0]           = <value>
	 * root.bean[0].property1 = <value>
	 * root.bean[1].property1 = <value>
	 * root.bean[2].property1 = <value>
	 * 
	 * */
	@SuppressWarnings("unchecked")
	public Object getValueCollectionSimple(CollectionBean entity, Element e,
			String prefix, long index) {
		
		Collection<Object> destValue = 
				(Collection<Object>)this.getValueBean(entity, prefix, index);
	
		String newPrefix;
		
		if(prefix.endsWith(entity.getSeparator())){
			newPrefix = prefix.substring(0, prefix.length() - 1);
 		}
		else{
			newPrefix = prefix;
		}
		
		int max = entity.getMaxItens() + 1;
		
		for(int i=0;i<max;i++){
			String ePreifx = 
				newPrefix + 
				entity.getIndexFormat().replace("$index", String.valueOf(i));
				
			/*
			if(e.getMapping() != null || e.getMetaBean() != null){
				ePreifx += entity.getSeparator();
			}
			*/
			
			Object element = this.getValue(e, FetchType.EAGER, ePreifx, -1, false);
			
			if(element != null){
				destValue.add(element);
			}
			else{
				break;
			}
		}
		
		if(destValue.size() > max){
			throw new DependencyException(destValue + " > " + max);
		}
		
		return destValue.isEmpty()? null : destValue;
	}	

	/* map */
	
	private Object getValueMap(MapBean entity, String prefix, long index) {
		
		Key k = (Key)entity.getKey();
		
		if(k.getParameterName() != null){
			return this.getValueMapObject(entity, k, prefix, index);
		}
		else{
			return this.getValueMapSimple(entity, k, prefix, index);
		}
	}

	/*
	 * <prefixo>.(<property>|<elements>).(<element> | <key>) = value
	 * 
	 *  ex:
	 *  prefixo.prop1 = ?
	 *  prefixo.prop2 = ?
	 *  prefixo.element[0].element = ?
	 *  prefixo.element[0].element.prop = ?
	 *  prefixo.element[0].key = ?
	 *  prefixo.element[0].key.prop = ?
	 */
	@SuppressWarnings("unchecked")
	public Object getValueMapObject(MapBean entity, Key k, String prefix, long index){
		
		Map<Object,Object> destValue = 
				(Map<Object,Object>)this.getValueBean(entity, prefix, index);

		Element e = (Element)entity.getCollection();
		int max   = entity.getMaxItens() + 1;
		
		String newPrefix = null;
		
		if(StringUtil.isEmpty(prefix)){
			newPrefix = e.getParameterName();
		}
		else
		if(!prefix.endsWith(entity.getSeparator())){
			newPrefix = prefix + entity.getSeparator() + e.getParameterName();
		}
		else{
			newPrefix = prefix + e.getParameterName();
		}
		
		for(int i=0;i<max;i++){
			String indexPrefix = 
				newPrefix +
				entity.getIndexFormat().replace("$index", String.valueOf(i));
			
			String kPrefix = 
					indexPrefix + 
					entity.getSeparator() +
					k.getParameterName();
			
			Object key = this.getValue(k, FetchType.EAGER, kPrefix, -1, true);
			
			if(key == null){
				break;
			}
			
			String ePrefix =
				indexPrefix + 
				entity.getSeparator() +
				e.getParameterName();
			
			Object element = this.getValue(e, FetchType.EAGER, ePrefix, -1, true);
			
			destValue.put(key, element);
		}
		
		if(destValue.size() > max){
			throw new DependencyException(destValue + " > " + max);
		}
		
		return destValue.isEmpty()? null : destValue;
	}

	/*
	 * <prefixo>.<key>[.<property>] = <value> 
	 */
	@SuppressWarnings("unchecked")
	public Object getValueMapSimple(MapBean entity, Key k, String prefix, long index){
		Map<Object,Object> destValue = 
				(Map<Object,Object>)this.getValueBean(entity, prefix, index);

		Element e         = (Element)entity.getCollection();
		
		String itemPrefix;
		
		if(prefix.endsWith(entity.getSeparator())){
			itemPrefix = prefix.substring(0, prefix.length() - 1);
 		}
		else{
			itemPrefix = prefix;
		}
		
		List<String> itens = 
				k.getScope()
					.getNamesStartsWith(itemPrefix);

		if(itens.size() > entity.getMaxItens()){
			throw new DependencyException(itens.size() + " > " + entity.getMaxItens());
		}
		
		List<SimpleKeyMap> keys = 
				this.prepareKeysToSimpleMap(itens, itemPrefix);
		
		for(SimpleKeyMap keyValue: keys){
			Object keyObject = k.convert(keyValue.getName());
			String ePreifx   = itemPrefix + keyValue.getPrefix();
			Object element   = this.getValue(e, FetchType.EAGER, ePreifx, -1, false);
			
			destValue.put(keyObject, element);
		}
		
		return destValue.isEmpty()? null : destValue;	
	}
	
	private List<SimpleKeyMap> prepareKeysToSimpleMap(List<String> itens, String prefix){
		
		List<SimpleKeyMap> result = new ArrayList<SimpleKeyMap>();
		 
		for(String item: itens){
			String keyPrefix = item.substring(prefix.length());
			String key = keyPrefix;
			
			if(key.startsWith(".")){
				int endKeyName = key.indexOf(".", 1);
				
				if(endKeyName != -1){
					key = key.substring(1, endKeyName - 1);
				}
				else{
					key = key.substring(1);
				}
			}
			else
			if(key.startsWith("[")){
				int endKeyName = key.indexOf("]");
				
				if(endKeyName != -1){
					key = key.substring(1, endKeyName - 1);
				}
				else{
					throw new MappingException("expected ']' in " + item);
				}
			}
			
			result.add(new SimpleKeyMap(key, keyPrefix));
		}
		
		return result;
	}	
	
	/* constructor */
	
	private Object getInstance(ConstructorBean constructor, String prefix, long index){
		try{
			return constructor.isConstructor()? 
				this.getInstanceByConstructor(constructor, prefix, index) :
				this.getInstanceByFactory(constructor, prefix, index);
		}
		catch(Throwable e){
			throw new DependencyException("create instance failed: " + constructor.getBean().getName());
		}
	}
	
	private Object getInstanceByConstructor(ConstructorBean constructor,
			String prefix, long index) throws InstantiationException, 
			IllegalAccessException, IllegalArgumentException, InvocationTargetException{
		
		Constructor<?> insCons = constructor.getContructor();
		Object[] args          = this.getArgs(constructor, prefix, index);
		
		if(args == null){
			return null;
		}
		
		return insCons.newInstance(args);
	}

	private Object getInstanceByFactory(ConstructorBean constructor, String prefix, long index) 
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException{
		
		String factory = constructor.getMethodFactory();
		Object factoryInstance;
		
		if(factory != null){
			Bean factoryBean = constructor.getBean().getController().getBean(factory);
			
			if(factoryBean == null){
				throw new IllegalStateException("bean factory not found: " + factory);
			}
			
			factoryInstance = this.getValue(factoryBean, prefix, index);
			
		}
		else{
			factoryInstance = constructor.getBean().getClassType();
		}
		
		Method method = constructor.getMethod(factoryInstance);
		
		if (constructor.isCollection() && constructor.size() == 0)
			throw new MappingException("infinite loop detected: "
					+ constructor.getBean().getName());
		
		Object[] args  = this.getArgs(constructor, prefix, index);
		
		return method.invoke(factoryInstance, args);
	}
	
	private Object[] getArgs(ConstructorBean constructor, String prefix, long index) 
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException{

		List<ConstructorArgBean> argsList = constructor.getConstructorArgs();
		
		Object[] args = new Object[argsList.size()];
		int i         = 0;
		boolean exist = argsList.size() < 1;
		
		for(ConstructorArgBean arg: constructor.getConstructorArgs()){
			args[i] = this.getValue(arg, null, prefix, index, true);
			if(!exist){
				exist = exist || args[i] != null || arg.isNullable();
			}
			
			i++;
		}
		
		return exist? args : null;
	}

	/* util */

	private String getNewPrefix(DependencyBean entity, String prefix, boolean hasNext){
		
		if(entity.getParent().isHierarchy()){
			String parameterName = entity.getParameterName();
			
			if(parameterName != null){
				if(!prefix.endsWith(entity.getParent().getSeparator())){
					prefix += entity.getParent().getSeparator();
				}
				
				prefix = 
					prefix == null?
						parameterName + entity.getParent().getSeparator() :
						prefix + parameterName;
			}
			
			if(hasNext && !prefix.endsWith(entity.getParent().getSeparator())){
				prefix +=entity.getParent().getSeparator();
			}
		}
		
		return prefix;
	}

}
