package com.answer.bdframework.test;

import com.answer.bdframework.annotation.BigDataBootApplication;
import com.answer.bdframework.context.BDApplicationContext;
import com.answer.bdframework.context.BigDataApplication;
import com.answer.bdframework.dao.OrderDao;
import com.answer.bdframework.entity.LogLevel;
import com.answer.bdframework.proxy.BdDaoProxy;
import com.answer.bdframework.proxy.BdProxyInstance;
import com.answer.bdframework.sqlcontainer.SQLContainer;

/**
 * Created by L.Answer on 2018-07-30 11:11
 */
@BigDataBootApplication(scanSQLXmlPath = {"src/main/resources/sqlText/"}, scanJavaPath = "com.answer.bdframework.test", logLevel = LogLevel.DEBUG)
public class BDApplicationTest {

    public static void main(String[] args) {
        BDApplicationContext bgApplicationContext = BigDataApplication.run(BDApplicationTest.class, args);

        AALController aalController = (AALController) bgApplicationContext.getBean(AALController.class);
        aalController.connect();

        System.out.println("========================================================================");

        BigDataController bigDataController = (BigDataController) bgApplicationContext.getBean(BigDataController.class);
        bigDataController.showConnectInfo();

        System.out.println("========================================================================");

        System.out.println(SQLContainer.getSqlText("templateMapper.findOrderList"));

        System.out.println("========================================================================");

        // TODO 添加注解进行注入 @MapperScan or @Mapper
        BdDaoProxy proxy = new BdDaoProxy();
        BdProxyInstance instance = new BdProxyInstance<>(OrderDao.class);
        OrderDao dao = (OrderDao) instance.newInstance(proxy);

        String sql1 = dao.findOrderByOrderNo("20190514001");
        System.out.println("sql1: " + sql1);
        System.out.println("--------------------------------------------------");

        String sql2 = dao.findOrderList();
        System.out.println("sql2: " + sql2);
        System.out.println("--------------------------------------------------");

        String sql3 = dao.findOrderByParams("2019-05-14", "5000501", 1);
        System.out.println("sql3: " + sql3);


        bgApplicationContext.close();
    }

}