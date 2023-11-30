package com.jason.datacollection.entity;


import java.util.Date;

public class KLog {

  private String id;
  private String admdivcode;
  private String type;
  private String transname;
  private String stepname;
  private String I;
  private String O;
  private String R;
  private String W;
  private String U;
  private String E;
  private Date time;
  private String categoryId;


  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }


  public String getAdmdivcode() {
    return admdivcode;
  }

  public void setAdmdivcode(String admdivcode) {
    this.admdivcode = admdivcode;
  }


  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }


  public String getTransname() {
    return transname;
  }

  public void setTransname(String transname) {
    this.transname = transname;
  }


  public String getStepname() {
    return stepname;
  }

  public void setStepname(String stepname) {
    this.stepname = stepname;
  }


  public String getI() {
    return I;
  }

  public void setI(String I) {
    this.I = I;
  }


  public String getO() {
    return O;
  }

  public void setO(String O) {
    this.O = O;
  }


  public String getR() {
    return R;
  }

  public void setR(String R) {
    this.R = R;
  }


  public String getW() {
    return W;
  }

  public void setW(String W) {
    this.W = W;
  }


  public String getU() {
    return U;
  }

  public void setU(String U) {
    this.U = U;
  }


  public String getE() {
    return E;
  }

  public void setE(String E) {
    this.E = E;
  }


  public Date getTime() {
    return time;
  }

  public void setTime(Date time) {
    this.time = time;
  }


  public String getCategoryId() {
    return categoryId;
  }

  public void setCategoryId(String categoryId) {
    this.categoryId = categoryId;
  }

}
