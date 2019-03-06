package com.answer.bdframework.test;

import com.answer.bdframework.annotation.BigDataBootApplication;
import com.answer.bdframework.context.BDApplicationContext;
import com.answer.bdframework.context.BigDataApplication;
import com.answer.bdframework.entity.LogLevel;
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

        bgApplicationContext.close();
    }

}