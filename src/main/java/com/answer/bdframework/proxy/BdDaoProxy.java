package com.answer.bdframework.proxy;

import com.answer.bdframework.sqlcontainer.SQLContainer;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;

import static com.answer.bdframework.entity.ExecEnum.*;

/**
 * @author Answer.AI.L
 * @date 2019-04-03
 */
public class BdDaoProxy implements InvocationHandler {

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object result = null;
        String className = method.getDeclaringClass().getName();
        String methodName = method.getName();

        if (args == null || args.length > 0) {
            args = new Object[1];
            args[0] = 0;
        }

        System.out.println("className: " + className + "\n" +
                "method name: " + methodName + "\n" +
                "method return type: " + method.getReturnType().getName() + "\n" +
                "method args: " + Arrays.asList(args));


        String sql = SQLContainer.getSqlText(String.format("%s.%s", className, methodName));
        if (true) {
            return sql;
        }

        // TODO Answer.AI.L 执行 sql 语句并将结果返回(包括预编译及对结果进行解析封装)

        if (INSERT.getName().equals(methodName) ||
                DELETE.getName().equals(methodName) ||
                UPDATE.getName().equals(methodName)) {
            result = 1;
        } else if (SELECT.getName().equals(methodName)) {
            result = String.format("%s-%s", methodName, args[0]);
        } else if (SELECT_MANY.getName().equals(methodName)) {
            result = Collections.singletonList(String.format("%s-%s", methodName, args[0]));
        }

        return result;
    }

}
