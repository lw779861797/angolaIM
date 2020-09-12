package com.goatlerbon.aim.common.core.proxy;

import com.alibaba.fastjson.JSONObject;
import com.goatlerbon.aim.common.enums.StatusEnum;
import com.goatlerbon.aim.common.exception.AIMException;
import com.goatlerbon.aim.common.util.HttpClient;
import okhttp3.OkHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * 动态代理管理类
 * @param <T>
 */
public final class ProxyManager<T> {
    private final static Logger LOGGER = LoggerFactory.getLogger(ProxyManager.class);

    private Class<T> clazz;

    private String url;

    private OkHttpClient okHttpClient;

    public ProxyManager(Class<T> clazz, String url, OkHttpClient okHttpClient) {
        this.clazz = clazz;
        this.url = url;
        this.okHttpClient = okHttpClient;
    }

    public T getInstance(){
        return (T) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),new Class[]{clazz},new ProxyInvocation());
    }

    public Class<T> getClazz() {
        return clazz;
    }

    public void setClazz(Class<T> clazz) {
        this.clazz = clazz;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public OkHttpClient getOkHttpClient() {
        return okHttpClient;
    }

    public void setOkHttpClient(OkHttpClient okHttpClient) {
        this.okHttpClient = okHttpClient;
    }

    private class ProxyInvocation implements InvocationHandler{

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            JSONObject jsonObject = new JSONObject();
//            这个就是可以拼接出一个 路由 是在路由服务器中controller中的方法
            String serverUrl = url + "/" + method.getName();

            //参数效验失败时报错
            if(args != null && args.length > 1){
                throw new AIMException(StatusEnum.VALIDATION_FAIL);
            }

            if(method.getParameterTypes().length > 0){
                Object param = args[0];
                Class<?> parameterType = method.getParameterTypes()[0];
                for(Field field : parameterType.getDeclaredFields()){
                    field.setAccessible(true);
                    jsonObject.put(field.getName(),field.get(param));
                }
            }
            return HttpClient.call(okHttpClient,jsonObject.toString(),serverUrl);
        }
    }
}
