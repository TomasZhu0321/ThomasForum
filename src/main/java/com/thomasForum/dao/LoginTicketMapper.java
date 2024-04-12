package com.thomasForum.dao;

import com.thomasForum.entity.LoginTicket;
import com.thomasForum.entity.User;
import org.apache.ibatis.annotations.*;

import javax.annotation.processing.Generated;

@Mapper
public interface LoginTicketMapper {
    @Select({"select * from login_ticket where ticket=#{ticket} "})
    LoginTicket selectLoginTicket(String ticket);

    @Insert({"insert into login_ticket (user_id,ticket,status,expired) ",
    "values (#{userId}, #{ticket}, #{status}, #{expired}) "})
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertLoginTicket(LoginTicket loginTicket);

    @Update({"<script> ",
            "update login_ticket set status=#{status} where ticket=#{ticket} ",
            "<if test= \" ticket != null \"> ",
            "and 1=1 ",
            "</if> ",
            "</script>"
    })
    int updateLoginTicket(String ticket, int status);
}
