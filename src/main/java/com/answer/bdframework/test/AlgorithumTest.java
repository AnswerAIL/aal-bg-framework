package com.answer.bdframework.test;

import com.answer.bdframework.algorithm.Algorithum3DES;

/**
 * Created by L.Answer on 2018-07-31 11:36
 */
public class AlgorithumTest {

    public static void main(String[] args) throws Exception {
        String source = "this is a algorithum demo.";
        String salt = "abcdef123456fedcba654321";

        Algorithum3DES algorithum3DES = new Algorithum3DES();
        String encRlt = algorithum3DES.encrypt(source, salt);
        System.out.println("encrypt encRlt: " + encRlt);

        String desRlt = algorithum3DES.decrypt(encRlt, salt);
        System.out.println("encrypt desRlt: " + desRlt);
    }

}