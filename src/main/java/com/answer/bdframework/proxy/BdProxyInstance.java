package com.answer.bdframework.proxy;

import java.lang.reflect.Proxy;

/**
 * @author Answer.AI.L
 * @date 2019-05-14
 */
public class BdProxyInstance<T> {
    private final Class<T> clazz;

    public BdProxyInstance(Class<T> clazz) {
        this.clazz = clazz;
    }

    @SuppressWarnings("unchecked")
    public T newInstance(BdDaoProxy proxy) {
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, proxy);
    }

}
