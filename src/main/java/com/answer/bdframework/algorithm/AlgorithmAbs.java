package com.answer.bdframework.algorithm;

/**
 * Created by L.Answer on 2018-07-31 10:54
 */
public abstract class AlgorithmAbs {

    public abstract String encrypt(final String source, final String salt) throws Exception;

    public abstract String decrypt(final String source, final String salt) throws Exception;
}