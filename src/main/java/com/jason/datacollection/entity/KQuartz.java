package com.jason.datacollection.entity;


import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public class KQuartz {

  private String id;
  private String quartzDescription;
  private String quartzCron;
  @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
  private Date addTime;
  private String addUser;
  @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
  private Date editTime;
  private String editUser;
  private String delFlag;


  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }


  public String getQuartzDescription() {
    return quartzDescription;
  }

  public void setQuartzDescription(String quartzDescription) {
    this.quartzDescription = quartzDescription;
  }


  public String getQuartzCron() {
    return quartzCron;
  }

  public void setQuartzCron(String quartzCron) {
    this.quartzCron = quartzCron;
  }


  public Date getAddTime() {
    return addTime;
  }

  public void setAddTime(Date addTime) {
    this.addTime = addTime;
  }


  public String getAddUser() {
    return addUser;
  }

  public void setAddUser(String addUser) {
    this.addUser = addUser;
  }


  public Date getEditTime() {
    return editTime;
  }

  public void setEditTime(Date editTime) {
    this.editTime = editTime;
  }


  public String getEditUser() {
    return editUser;
  }

  public void setEditUser(String editUser) {
    this.editUser = editUser;
  }


  public String getDelFlag() {
    return delFlag;
  }

  public void setDelFlag(String delFlag) {
    this.delFlag = delFlag;
  }

}
