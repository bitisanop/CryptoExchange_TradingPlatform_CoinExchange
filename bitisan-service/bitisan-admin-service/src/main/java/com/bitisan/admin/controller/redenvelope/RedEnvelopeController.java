package com.bitisan.admin.controller.redenvelope;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bitisan.annotation.AccessLog;
import com.bitisan.constant.AdminModule;
import com.bitisan.controller.BaseController;
import com.bitisan.screen.PageParam;
import com.bitisan.service.LocaleMessageSourceService;
import com.bitisan.user.entity.Coin;
import com.bitisan.user.entity.RedEnvelope;
import com.bitisan.user.entity.RedEnvelopeDetail;
import com.bitisan.user.feign.CoinFeign;
import com.bitisan.user.feign.RedEnvelopDetailFeign;
import com.bitisan.user.feign.RedEnvelopeFeign;
import com.bitisan.util.DateUtil;
import com.bitisan.util.GeneratorUtil;
import com.bitisan.util.MessageResult;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.util.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.apache.commons.lang3.Validate.notNull;

/**
 * 红包管理
 * @author Bitisan  E-mail:bizzanhevin@gmail.com
 *
 */
@RestController
@RequestMapping("/envelope")
public class RedEnvelopeController extends BaseController {
	@Autowired
	private RedEnvelopeFeign redEnveloperService;

	@Autowired
	private RedEnvelopDetailFeign redEnvelopDetailFeign;

	@Autowired
    private LocaleMessageSourceService messageSource;

	@Autowired
	private CoinFeign coinService;

	/**
	 * 红包分页列表
	 * @param pageParam
	 * @return
	 */
	@RequiresPermissions("envelope:page-query")
    @PostMapping("page-query")
    @AccessLog(module = AdminModule.REDENVELOPE, operation = "分页查看红包列表RedEnvelopeController")
    public MessageResult envelopeList(PageParam pageParam) {
        Page<RedEnvelope> all = redEnveloperService.findAll(pageParam);
        return success(all);
    }

	/**
	 * 红包详情
	 * @param
	 * @return
	 */
	@RequiresPermissions("envelope:detail")
	@GetMapping("{id}/detail")
    @AccessLog(module = AdminModule.REDENVELOPE, operation = "查看红包详情RedEnvelopeController")
    public MessageResult envelopeDetail(@PathVariable Long id) {
		RedEnvelope redEnvelope = redEnveloperService.findOne(id);
		Assert.notNull(redEnvelope, "validate id!");
        return success(redEnvelope);
    }

	/**
	 * 领取详情分页
	 * @param
	 * @return
	 */
	@RequiresPermissions("envelope:receive-detail")
    @PostMapping("receive-detail")
    @AccessLog(module = AdminModule.REDENVELOPE, operation = "查看红包领取详情RedEnvelopeController")
    public MessageResult envelopeDetailList(@RequestParam(value = "envelopeId", defaultValue = "0") Long envelopeId,
    		@RequestParam(value = "pageNo", defaultValue = "0") Integer pageNo,
    		@RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {

		Page<RedEnvelopeDetail> detailList = redEnvelopDetailFeign.findByEnvelope(envelopeId, pageNo, pageSize);

        return success(detailList);
    }

	/**
	 * 新建红包
	 * @param redEnvelope
	 * @return
	 */
	@RequiresPermissions("envelope:add")
    @PostMapping("add")
    @AccessLog(module = AdminModule.REDENVELOPE, operation = "新增红包信息RedEnvelopeController")
    public MessageResult addRedEnvelope(
            @Valid RedEnvelope redEnvelope) {
		// 检查币种是否存在
		Coin coin = coinService.findByUnit(redEnvelope.getUnit());
		Assert.notNull(coin, messageSource.getMessage("INVALID_CURRENCY"));

		// 生成红包编号
		SimpleDateFormat f = new SimpleDateFormat("MMddHHmmss");
		redEnvelope.setEnvelopeNo(f.format(new Date()) + GeneratorUtil.getNonceString(5).toUpperCase());

		redEnvelope.setMemberId(1L); // 平台发行的固定为1的用户
		redEnvelope.setPlateform(1); // 平台发行的固定为1（平台红包）
		redEnvelope.setState(0);
		redEnvelope.setReceiveAmount(BigDecimal.ZERO);
		redEnvelope.setReceiveCount(0);

		redEnvelope.setCreateTime(DateUtil.getCurrentDate());
		redEnvelope = redEnveloperService.save(redEnvelope);
        return MessageResult.getSuccessInstance(messageSource.getMessage("SUCCESS"), redEnvelope);
    }

	/**
	 * 修改红包
	 * @param id
	 * @param type
	 * @param invite
	 * @param maxRand
	 * @param totalAmount
	 * @param count
	 * @param logoImage
	 * @param bgImage
	 * @param name
	 * @param expiredHours
	 * @param state
	 * @return
	 */
	@RequiresPermissions("envelope:modify")
    @PostMapping("modify")
    @AccessLog(module = AdminModule.REDENVELOPE, operation = "新增红包信息RedEnvelopeController")
    public MessageResult modifyRedEnvelope(
    		@RequestParam("id") Long id,
            @RequestParam(value = "type", required = false) Integer type,
            @RequestParam(value = "invite", required = false) Integer invite,
            @RequestParam(value = "unit", required = false) String unit,
            @RequestParam(value = "maxRand", required = false) BigDecimal maxRand,
            @RequestParam(value = "totalAmount", required = false) BigDecimal totalAmount,
            @RequestParam(value = "count", required = false) Integer count,
            @RequestParam(value = "logoImage", required = false) String logoImage,
            @RequestParam(value = "bgImage", required = false) String bgImage,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "detail", required = false) String detail,
            @RequestParam(value = "expiredHours", required = false) Integer expiredHours,
            @RequestParam(value = "state", required = false) Integer state) {

		RedEnvelope redEnvelope = redEnveloperService.findOne(id);
		notNull(redEnvelope, "Validate Red Envelope!");

		if(type != null) redEnvelope.setType(type);
		if(invite != null) redEnvelope.setInvite(invite);
		if(unit != null) {
			// 检查币种是否存在
			Coin coin = coinService.findByUnit(redEnvelope.getUnit());
			Assert.notNull(coin, messageSource.getMessage("INVALID_CURRENCY"));
			redEnvelope.setUnit(unit);
		};
		if(maxRand != null) redEnvelope.setMaxRand(maxRand);
		if(totalAmount != null) redEnvelope.setTotalAmount(totalAmount);
		if(count != null) redEnvelope.setCount(count);
		if(logoImage != null) redEnvelope.setLogoImage(logoImage);
		if(bgImage != null) redEnvelope.setBgImage(bgImage);
		if(name != null) redEnvelope.setName(name);
		if(detail != null) redEnvelope.setDetail(detail);
		if(expiredHours != null) redEnvelope.setExpiredHours(expiredHours);
		if(state != null) redEnvelope.setState(state);

		redEnvelope = redEnveloperService.save(redEnvelope);

        return MessageResult.getSuccessInstance(messageSource.getMessage("SUCCESS"), redEnvelope);
    }
}
