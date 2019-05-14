package com.answer.bdframework.dao;

/**
 * @author Answer.AI.L
 * @date 2019-05-14
 */
public interface OrderDao {

    String findOrderList();

    String findOrderByOrderNo(String orderNo);

    String findOrderByParams(String createTime, String cardNo, int status);

}