package org.brandao.brutos.scanner.vfs;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import org.brandao.brutos.mapping.StringUtil;


public final class Vfs {
    
    public static Dir getDir(URL url){
        return getDir(url, DefaultURLTypes);
    }
    
    public static Dir getDir(URL url, List types){
        try{
            for(int i=0;i<DefaultURLTypes.size();i++){
                URLType type = (URLType) DefaultURLTypes.get(i);
                if(type.matches(url))
                    return type.toDir(url);
            }
            
            throw new VfsException("invalid type: " + url);
        }
        catch(VfsException e){
            throw e;
        }
        catch(Exception e){
            throw new VfsException(e);
        }
            
    }
    
    public static java.io.File toFile(URL url){
        java.io.File file;
        String path = cleanPath(url);
        file = new java.io.File(path);
        return file.exists()? file : null;
    }
    
    public static String cleanPath(URL url) {
        String path = url.getPath();
        try{
            path = path.replace("%20", " ");
            path = URLDecoder.decode(path, "UTF-8");
        } 
        catch (UnsupportedEncodingException e){
        }
        
        if (path.startsWith("jar:"))
            path = path.substring("jar:".length());
        
        if (path.startsWith("file:"))
            path = path.substring("file:".length());
        
        if (path.contains(".jar!")) 
            path = path.substring(0, path.indexOf(".jar!") + ".jar".length());        
        
        return path;
    }
    
    public static String getRelativePath(URL url){
        String path = url.getPath();
        int index = path.indexOf("!");
        
        if(index != -1){
            path = path.substring(index+1);
            path = path.startsWith("/")? path.substring(1) : path;
            return StringUtil.isEmpty(path)? null : path;
        }
        else
            return null;
    }
    
    public static final List DefaultURLTypes;
    
    static{
        DefaultURLTypes = new ArrayList();
        DefaultURLTypes.add(new JarFileURLType());
        DefaultURLTypes.add(new JarURLType());
        DefaultURLTypes.add(new SystemURLType());
        
        try{
            DefaultURLTypes.add(new JbossVFSURLType());
        }
        catch(Throwable e){
        }
        
    }

    public static String toClass(String resource){
        return resource
                .replaceAll("/+", ".")
                .replaceAll("\\+", ".")
                .replaceAll(".class$", "")
                .replaceAll("^\\.", "");
    }
    
   public static String toResource(String value){
        return value
            .replaceAll("\\.+", "/")
            .replaceAll("\\\\+", "/")
            .replaceAll( "/+" , "/")
            .replaceAll("^\\/", "");
    }
    
}
