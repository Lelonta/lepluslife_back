package com.jifenke.lepluslive.order.controller;

import com.jifenke.lepluslive.global.util.LejiaResult;
import com.jifenke.lepluslive.global.util.MvUtil;
import com.jifenke.lepluslive.merchant.service.MerchantService;
import com.jifenke.lepluslive.merchant.service.MerchantWeiXinUserService;
import com.jifenke.lepluslive.order.controller.view.FinancialViewExcel;
import com.jifenke.lepluslive.order.controller.view.OrderViewExcel;
import com.jifenke.lepluslive.order.domain.criteria.FinancialCriteria;
import com.jifenke.lepluslive.order.domain.criteria.OLOrderCriteria;
import com.jifenke.lepluslive.order.domain.entities.FinancialStatistic;
import com.jifenke.lepluslive.order.service.OffLineOrderService;
import com.jifenke.lepluslive.weixin.service.WxTemMsgService;

import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;

/**
 * Created by wcg on 16/5/9.
 */
@RestController
@RequestMapping("/manage")
public class OffLineOrderController {


  @Inject
  private OrderViewExcel orderViewExcel;

  @Inject
  private OffLineOrderService offLineOrderService;

  @Inject
  private FinancialViewExcel financialViewExcel;

  @Inject
  private WxTemMsgService wxTemMsgService;

  @Inject
  private MerchantService merchantService;

  @Inject
  private MerchantWeiXinUserService merchantWeiXinUserService;


  @RequestMapping("/offLineOrder")
  public ModelAndView offLineOrder() {
    return MvUtil.go("/order/offLineOrderList");
  }

  @RequestMapping(value = "/offLineOrder", method = RequestMethod.POST)
  public
  @ResponseBody
  LejiaResult getOffLineOrder(@RequestBody OLOrderCriteria olOrderCriteria) {
    Page page = offLineOrderService.findOrderByPage(olOrderCriteria, 10);
    if (olOrderCriteria.getOffset() == null) {
      olOrderCriteria.setOffset(1);
    }
    return LejiaResult.ok(page);
  }

  @RequestMapping(value = "/offLineOrder/{id}", method = RequestMethod.GET)
  public
  @ResponseBody
  LejiaResult changeOrderStateToPaid(@PathVariable Long id) {
    offLineOrderService.changeOrderStateToPaid(id);
    return LejiaResult.ok();
  }

  @RequestMapping(value = "/offLineOrder/export", method = RequestMethod.POST)
  public ModelAndView exporeExcel(OLOrderCriteria olOrderCriteria) {
    if (olOrderCriteria.getOffset() == null) {
      olOrderCriteria.setOffset(1);
    }
    Page page = offLineOrderService.findOrderByPage(olOrderCriteria, 10000);
    Map map = new HashMap();
    map.put("orderList", page.getContent());
    return new ModelAndView(orderViewExcel, map);
  }

  @RequestMapping(value = "/financial", method = RequestMethod.GET)
  public ModelAndView financialList() {

    return MvUtil.go("/order/financialList");
  }


  @RequestMapping(value = "/financial", method = RequestMethod.POST)
  public
  @ResponseBody
  LejiaResult searchFinancialBycriterial(@RequestBody FinancialCriteria financialCriteria) {
    if (financialCriteria.getOffset() == null) {
      financialCriteria.setOffset(1);
    }
    Page page = offLineOrderService.findFinancialByCirterial(financialCriteria, 10);
    return LejiaResult.ok(page);
  }


  @RequestMapping(value = "/financial/{id}", method = RequestMethod.GET)
  public
  @ResponseBody
  LejiaResult changeFinancialStateToTransfer(@PathVariable Long id) {
    //改变统计单状态并发送模版消息
    FinancialStatistic financialStatistic = offLineOrderService.changeFinancialStateToTransfer(id);
    String s = financialStatistic.getMerchant().getMerchantBank().getBankNumber();
    String[] keys = new String[4];
    StringBuffer sb = new StringBuffer(s.substring(s.length() - 4, s.length()));
    for (int i = 0; i < s.length() - 4; i++) {
      sb.insert(0, "*");
    }
    keys[0] = sb.toString();
    sb.setLength(0);
    keys[1] = sb.append(financialStatistic.getTransferPrice() / 100.0).toString();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    keys[2] = dateFormat.format(financialStatistic.getTransferDate());
    HashMap<String, Object> mapRemark = new HashMap<>();
    sb.setLength(0);
    sb.append("点击查看详情");
    mapRemark.put("value", sb.toString());
    mapRemark.put("color", "#173177");
    HashMap<String, Object> map2 = new HashMap<>();
    map2.put("remark", mapRemark);
    merchantService.findMerchantUserByMerchant(financialStatistic
                                                   .getMerchant()).stream().map(merchantUser -> {
      merchantWeiXinUserService.findMerchantWeiXinUserByMerchantUser(merchantUser).stream()
          .map(merchantWeiXinUser -> {
            wxTemMsgService.sendTemMessage(merchantWeiXinUser.getOpenId(), 4L, keys,
                                           financialStatistic.getStatisticId(), 41L, map2);
            return null;
          })
          .collect(Collectors.toList());
      return null;
    }).collect(
        Collectors.toList());

    return LejiaResult.ok();
  }

  @RequestMapping(value = "/financial/export", method = RequestMethod.POST)
  public ModelAndView exporeExcel(FinancialCriteria financialCriteria) {
    if (financialCriteria.getOffset() == null) {
      financialCriteria.setOffset(1);
    }
    Page page = offLineOrderService.findFinancialByCirterial(financialCriteria, 10000);
    Map map = new HashMap();
    map.put("financialList", page.getContent());
    return new ModelAndView(financialViewExcel, map);
  }

}
