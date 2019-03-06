package com.answer.bdframework.test;

import com.answer.bdframework.annotation.BDValue;
import com.answer.bdframework.annotation.BDComponent;

import java.util.List;
import java.util.Map;

/**
 * Created by L.Answer on 2018-07-30 20:02
 *
 * How To Usage BD Framework. entrance -> {@link BDApplicationTest}
 */
@BDComponent
public class BigDataController {

    @BDValue(name = "orderCount")
    private Integer order_count;
    @BDValue(name = "orderAmt")
    private Float order_amt;
    @BDValue
    private String rho_val;
    @BDValue
    private Map<String, String> company;
    @BDValue
    private List<String> http;
    @BDValue
    private String mysql_ai_db;

    @BDValue
    private String hive_user_name;
    @BDValue
    private String hive_passwd;
    @BDValue
    private String hive_db_url;
    @BDValue(defaultVal = "test")
    private String hive_url;

    public void showConnectInfo() {
        System.out.println("order_count: " + order_count);
        System.out.println("order_amt: " + order_amt);
        System.out.println("rho_val: " + rho_val);
        System.out.println("company: " + company);
        System.out.println("http: " + http);
        System.out.println("mysql_ai_db: " + mysql_ai_db);

        System.out.println("hive_user_name: " + hive_user_name);
        System.out.println("hive_passwd: " + hive_passwd);
        System.out.println("hive_db_url: " + hive_db_url);

        System.out.println("hive_url: " + hive_url);
    }

}