package com.jason.datacollection.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class KUser implements Serializable {
    private String id;

    private String nickname;

    private String email;

    private String phone;

    private String account;

    private String password;
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date addTime;

    private String addUser;
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date editTime;

    private String editUser;

    private Long delFlag;

}