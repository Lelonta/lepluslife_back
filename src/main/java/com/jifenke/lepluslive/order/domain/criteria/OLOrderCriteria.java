package com.jifenke.lepluslive.order.domain.criteria;

import java.util.Date;

/**
 * Created by wcg on 16/5/5.
 */
public class OLOrderCriteria {

  private String startDate;

  private String endDate;

  private Integer state;

  private String phoneNumber;

  private String userSid;

  private String merchant;

  private Long payWay;

  private Integer offset;

  public Integer getOffset() {
    return offset;
  }

  public void setOffset(Integer offset) {
    this.offset = offset;
  }

  public String getStartDate() {
    return startDate;
  }

  public void setStartDate(String startDate) {
    this.startDate = startDate;
  }

  public String getEndDate() {
    return endDate;
  }

  public void setEndDate(String endDate) {
    this.endDate = endDate;
  }

  public Integer getState() {
    return state;
  }

  public void setState(Integer state) {
    this.state = state;
  }

  public String getPhoneNumber() {
    return phoneNumber;
  }

  public void setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }

  public String getUserSid() {
    return userSid;
  }

  public void setUserSid(String userSid) {
    this.userSid = userSid;
  }

  public String getMerchant() {
    return merchant;
  }

  public void setMerchant(String merchant) {
    this.merchant = merchant;
  }

  public Long getPayWay() {
    return payWay;
  }

  public void setPayWay(Long payWay) {
    this.payWay = payWay;
  }
}
