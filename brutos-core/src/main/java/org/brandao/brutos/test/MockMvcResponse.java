

package org.brandao.brutos.test;

import java.io.OutputStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import org.brandao.brutos.MvcResponse;


public class MockMvcResponse implements MvcResponse{

    private Map values;
    private Map info;
    private OutputStream out;
    private String type;
    private int length;
    private String characterEncoding;
    private Locale locale;

    public MockMvcResponse(){
        this( new HashMap(), new HashMap() );
    }

    public MockMvcResponse( Map values, Map property ){
        this(values,property,null);
    }

    public MockMvcResponse( Map values, Map info, OutputStream out ){
        this.values = values;
        this.info = info;
        this.out = out;
    }

    public Map getValues() {
        return values;
    }

    public void setValues(Map values) {
        this.values = values;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public String getCharacterEncoding() {
        return characterEncoding;
    }

    public void setCharacterEncoding(String characterEncoding) {
        this.characterEncoding = characterEncoding;
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public void process(Object object) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public OutputStream processStream() {
        return out;
    }

    public void setInfo(String name, String value) {
        this.info.put(name, value);
    }

    public Object getInfo(String name) {
        return this.info.get(name);
    }

}
