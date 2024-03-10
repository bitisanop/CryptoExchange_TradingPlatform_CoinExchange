package com.bitisan.user.controller;

import com.alibaba.fastjson.JSON;
import com.bitisan.annotation.PermissionOperation;
import com.bitisan.constant.SysConstant;
import com.bitisan.controller.BaseController;
import com.bitisan.service.LocaleMessageSourceService;
import com.bitisan.user.entity.PaymentType;
import com.bitisan.user.entity.PaymentTypeRecord;
import com.bitisan.user.service.PaymentTypeRecordService;
import com.bitisan.user.service.PaymentTypeService;
import com.bitisan.user.transform.AuthMember;
import com.bitisan.user.vo.PaymentTypeConfig;
import com.bitisan.util.MessageResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * @author Bitisan E-Mali:bitisanop@gmail.com
 * @Description: coin
 * @date 2021/4/214:20
 */
@Api(tags = "支付")
@RestController
@RequestMapping("payment")
public class PaymentController extends BaseController {
    @Autowired
    private PaymentTypeService paymentTypeService;
    @Autowired
    private PaymentTypeRecordService paymentTypeRecordService;
    @Autowired
    private LocaleMessageSourceService msService;


    private String[] colors = {"#f0a70a","#e5dc2a","#4fbe51","#d07e3b","#0a4bf0","#810af0","#2b9f76"};
    // 查询所有支付方式
    @ApiOperation(value = "查询所有支付方式")
    @GetMapping("list")
    public MessageResult list() {
        List<PaymentType> list = paymentTypeService.findAll();
        return success(list);
    }
    // 查询支付方式配置
    @ApiOperation(value = "查询支付方式配置")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "id"),
    })
    @GetMapping("findPaymentTypeConfigById")
    public MessageResult list(@RequestParam(value = "id") Long id) {
        List<PaymentTypeConfig> list = paymentTypeService.findPaymentTypeConfigById(id);
        return success(list);
    }

    /**
     * 绑定支付方式
     */
    @ApiOperation(value = "绑定支付方式")
    @PermissionOperation
    @RequestMapping("saveOrUpdate")
    public MessageResult saveOrUpdate(@RequestHeader(SysConstant.SESSION_MEMBER) String authMember,
                                      @RequestParam(value = "id",required = false) Long id,
                                      @RequestParam(value = "type",required = false) Long type,
                                      @RequestParam(value = "field_1",required = false) String field_1,
                                      @RequestParam(value = "field_2",required = false) String field_2,
                                      @RequestParam(value = "field_3",required = false) String field_3,
                                      @RequestParam(value = "field_4",required = false) String field_4,
                                      @RequestParam(value = "field_5",required = false) String field_5,
                                      @RequestParam(value = "field_6",required = false) String field_6,
                                      @RequestParam(value = "field_7",required = false) String field_7
                                      ) {

        PaymentTypeRecord record = new PaymentTypeRecord();
        record.setType(type);
        if(id!=null){
            record.setId(id);
        }
        record.setField_1(field_1);
        record.setField_2(field_2);
        record.setField_3(field_3);
        record.setField_4(field_4);
        record.setField_5(field_5);
        record.setField_6(field_6);
        record.setField_7(field_7);
        AuthMember user = AuthMember.toAuthMember(authMember);
        record.setMemberId(user.getId());
        paymentTypeRecordService.saveOrUpdate(record);
        return success();
    }

    /**
     * 绑定支付方式
     */
    @ApiOperation(value = "获取记录")
    @PermissionOperation
    @RequestMapping("getRecords")
    public MessageResult getRecords(@RequestHeader(SysConstant.SESSION_MEMBER) String authMember) {
        AuthMember user = AuthMember.toAuthMember(authMember);
       List<PaymentTypeRecord> list = paymentTypeRecordService.getRecordsByUserId(user.getId());
       if(list!=null && list.size()>0) {
           int index = 0;
           for (PaymentTypeRecord record : list) {
               PaymentType type = paymentTypeService.findPaymentTypeById(record.getType());
               record.setTypeName(type.getCode());
               List<PaymentTypeConfig> cList = JSON.parseArray(type.getConfigJson(),PaymentTypeConfig.class);

               Map<String, String> fieldType = cList.stream().collect(Collectors.toMap(PaymentTypeConfig::getFieldName, PaymentTypeConfig::getType));
               Map<String, String> fieldName = cList.stream().collect(Collectors.toMap(PaymentTypeConfig::getFieldName, o->{
                   String message = msService.getMessage(o.getShowText());
                   if(message.indexOf("(")>0){
                       String substring = message.substring(message.indexOf("("), message.indexOf(")")+1);
                       message = message.replace(substring, "");
                   }
                   return message.trim();
               }));
               record.setFieldType(fieldType);
               record.setFieldName(fieldName);
               record.setColor(colors[index%6]);
               index++;
           }
       }
       return success(list);
    }

    public static void main(String[] args) {
      String s =  "付款详情(选填)";
      if(s.indexOf("(")>0){
          String substring = s.substring(s.indexOf("("), s.indexOf(")")+1);
          System.out.println(s.replace(substring, ""));
      }
    }
    /**
     * 绑定支付方式
     */
    @ApiOperation(value = "根据id删除记录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "id"),
    })
    @PermissionOperation
    @RequestMapping("delRecordById")
    public MessageResult delRecordById(@RequestHeader(SysConstant.SESSION_MEMBER) String authMember,@RequestParam("id") Long id) {
        AuthMember user = AuthMember.toAuthMember(authMember);
        paymentTypeRecordService.delRecordById(id);
        return success();
    }

}
