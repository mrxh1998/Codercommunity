package com.coder.community.dao;

import com.coder.community.entity.LoginTicket;
import org.apache.ibatis.annotations.*;
@Deprecated
@Mapper
public interface LoginTicketMapper {
    @Insert({
            "insert into login_ticket(user_id,ticket,status,expired) ",
            "value(#{userId},#{ticket},#{status},#{expired}) "
    })
    @Options(useGeneratedKeys = true,keyProperty = "id")
    int insertLoginTicket(LoginTicket loginTicket);

    @Select({
            "select user_id,ticket,status,expired from login_ticket ",
            "where ticket = #{ticket}"
    })
    LoginTicket selectByTicket(String ticket);

    @Update({
            "<script> ",
            "update login_ticket set status = #{status} ",
            "where ticket = #{ticket} ",
            "</script>"
    })
    int updateStatus(String ticket,int status);
}
