package com.answer.bdframework.test;

import com.answer.bdframework.annotation.BDValue;
import com.answer.bdframework.annotation.BDComponent;



/**
 * Created by L.Answer on 2018-07-24 10:32
 *
 * How To Usage BD Framework. entrance -> {@link BDApplicationTest}
 */
@BDComponent
public class AALController {
    @BDValue
    private String api_context;
    @BDValue
    private String mysql_url;
    @BDValue
    private String mysql_db;
    @BDValue
    private String mysql_aal_db;
    @BDValue
    private String mysql_username;
    @BDValue
    private String mysql_password;
    @BDValue
    private String mysql_driver;
    @BDValue
    private Float rho;
    @BDValue
    private Float alpha;



    public void connect() {
        System.out.println("api_context: " + api_context);
        System.out.println("mysql_url: " + mysql_url);
        System.out.println("mysql_db: " + mysql_db);
        System.out.println("mysql_aal_db: " + mysql_aal_db);
        System.out.println("mysql_username: " + mysql_username);
        System.out.println("mysql_password: " + mysql_password);
        System.out.println("mysql_driver: " + mysql_driver);
        System.out.println("rho: " + rho);
        System.out.println("alpha: " + alpha);
    }

}