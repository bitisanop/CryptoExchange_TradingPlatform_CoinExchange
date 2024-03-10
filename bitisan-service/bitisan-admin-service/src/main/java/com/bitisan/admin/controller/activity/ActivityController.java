package com.bitisan.admin.controller.activity;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bitisan.active.entity.Activity;
import com.bitisan.active.entity.ActivityOrder;
import com.bitisan.active.entity.LockedOrder;
import com.bitisan.active.entity.MiningOrder;
import com.bitisan.active.feign.ActivityFeign;
import com.bitisan.active.feign.ActivityOrderFeign;
import com.bitisan.active.feign.LockedOrderFeign;
import com.bitisan.active.feign.MiningOrderFeign;
import com.bitisan.admin.controller.common.BaseAdminController;
import com.bitisan.admin.entity.Admin;
import com.bitisan.annotation.AccessLog;
import com.bitisan.constant.AdminModule;
import com.bitisan.constant.BooleanEnum;
import com.bitisan.constant.SysConstant;
import com.bitisan.constant.TransactionType;
import com.bitisan.screen.PageParam;
import com.bitisan.service.LocaleMessageSourceService;
import com.bitisan.user.entity.Coin;
import com.bitisan.user.entity.Member;
import com.bitisan.user.entity.MemberTransaction;
import com.bitisan.user.entity.MemberWallet;
import com.bitisan.user.feign.CoinFeign;
import com.bitisan.user.feign.MemberFeign;
import com.bitisan.user.feign.MemberTransactionFeign;
import com.bitisan.user.feign.MemberWalletFeign;
import com.bitisan.util.DateUtil;
import com.bitisan.util.MD5;
import com.bitisan.util.MessageResult;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.util.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.springframework.util.Assert.notNull;

@RestController
@RequestMapping("activity/activity")
public class ActivityController extends BaseAdminController {

	@Autowired
	private ActivityFeign activityFeign;
	@Value("${spark.system.md5.key}")
	private String md5Key;
	@Autowired
	private ActivityOrderFeign activityOrderFeign;
	@Autowired
	private LocaleMessageSourceService messageSource;
	@Autowired
	private MemberWalletFeign memberWalletFeign;
	@Autowired
	private MemberTransactionFeign memberTransactionFeign;
	@Autowired
	private MemberFeign memberFeign;

	@Autowired
	private CoinFeign coinFeign;
	@Autowired
	private MiningOrderFeign miningOrderFeign;

	@Autowired
	private LockedOrderFeign lockedOrderFeign;


	@RequiresPermissions("activity:activity:locked-activity")
	@PostMapping("locked-activity")
	@AccessLog(module = AdminModule.ACTIVITY, operation = "查看锁仓活动列表")
	public MessageResult lockedActivityList() {
		List<Activity> all = activityFeign.lockedActivityList(); // 查询锁仓活动并且在进行中的
		return success(all);
	}

	/**
	 * 分页查询
	 * @param
	 * @return
	 */
	@RequiresPermissions("activity:activity:page-query")
	@PostMapping("page-query")
	@AccessLog(module = AdminModule.ACTIVITY, operation = "分页查看活动列表Activity")
	public MessageResult activityList(PageParam pageParam) {
		Page<Activity> all = activityFeign.findAll(pageParam);
		return success(IPage2Page(all));
	}


	/**
	 * 添加活动信息
	 * @param activity
	 * @return
	 */
	@RequiresPermissions("activity:activity:add")
	@PostMapping("add")
	@AccessLog(module = AdminModule.ACTIVITY, operation = "新增活动信息Activity")
	public MessageResult ExchangeCoinList(
			@Valid Activity activity) {
		activity.setCreateTime(DateUtil.getCurrentDate());
		activity = activityFeign.save(activity);
		return MessageResult.getSuccessInstance(messageSource.getMessage("SUCCESS"), activity);
	}

	@RequiresPermissions("activity:activity:orderlist")
	@GetMapping("{aid}/orderlist")
	public MessageResult orderList(
			@PathVariable Long aid) {
		List<ActivityOrder> activityOrderList = activityOrderFeign.findAllByActivityId(aid);
		Assert.notNull(activityOrderList, "validate id!");
		return success(activityOrderList);
	}

	/**
	 * 修改活动进度数值
	 * @param id
	 * @param progress
	 * @return
	 */
	@RequiresPermissions("activity:activity:modify-progress")
	@PostMapping("modify-progress")
	@AccessLog(module = AdminModule.ACTIVITY, operation = "修改活动进度Activity")
	public MessageResult alterActivity(
			@RequestParam("id") Long id,
			@RequestParam("progress") Integer progress) {
		notNull(id, "validate id!");

		Activity result = activityFeign.findById(id);
		notNull(result, "validate activity!");

		if(result.getProgress() > progress.intValue()) {
			return error(messageSource.getMessage("NEW_PROGRESS_LESS_THAN_CURRENT"));
		}
		result.setProgress(progress);

		activityFeign.save(result);

		return success(messageSource.getMessage("SUCCESS"));
	}

	//Modify
	@RequiresPermissions("activity:activity:modify")
	@PostMapping("modify")
	@AccessLog(module = AdminModule.ACTIVITY, operation = "修改活动信息Activity")
	public MessageResult alterActivity(
			@RequestParam("id") Long id,
			@RequestParam(value = "title", required = false) String title,
			@RequestParam(value = "detail", required = false) String detail,
			@RequestParam(value = "status", required = false) Integer status,
			@RequestParam(value = "step", required = false) Integer step,
			@RequestParam(value = "type", required = false) Integer type,
			@RequestParam(value = "startTime", required = false) String startTime,
			@RequestParam(value = "endTime", required = false) String endTime,
			@RequestParam(value = "totalSupply", required = false) BigDecimal totalSupply,
			@RequestParam(value = "price", required = false) BigDecimal price,
			@RequestParam(value = "priceScale", required = false) Integer priceScale,
			@RequestParam(value = "unit", required = false) String unit,
			@RequestParam(value = "acceptUnit", required = false) String acceptUnit,
			@RequestParam(value = "amountScale", required = false) Integer amountScale,
			@RequestParam(value = "maxLimitAmout", required = false) BigDecimal maxLimitAmout,
			@RequestParam(value = "minLimitAmout", required = false) BigDecimal minLimitAmout,
			@RequestParam(value = "limitTimes", required = false) Integer limitTimes,
			@RequestParam(value = "settings", required = false) String settings,
			@RequestParam(value = "content", required = false) String content,
			@RequestParam(value = "smallImageUrl", required = false) String smallImageUrl,
			@RequestParam(value = "bannerImageUrl", required = false) String bannerImageUrl,
			@RequestParam(value = "noticeLink", required = false) String noticeLink,
			@RequestParam(value = "activityLink", required = false) String activityLink,
			@RequestParam(value = "leveloneCount", required = false) Integer leveloneCount,
			@RequestParam(value = "holdLimit", required = false) BigDecimal holdLimit,
			@RequestParam(value = "holdUnit", required = false) String holdUnit,
			@RequestParam(value = "miningDays", required = false) Integer miningDays,
			@RequestParam(value = "miningDaysprofit", required = false) BigDecimal miningDaysprofit,
			@RequestParam(value = "miningUnit", required = false) String miningUnit,
			@RequestParam(value = "miningInvite", required = false) BigDecimal miningInvite,
			@RequestParam(value = "miningInvitelimit", required = false) BigDecimal miningInvitelimit,
			@RequestParam(value = "miningPeriod", required = false) Integer miningPeriod,
			@RequestParam(value = "lockedUnit", required = false) String lockedUnit,
			@RequestParam(value = "lockedPeriod", required = false) Integer lockedPeriod,
			@RequestParam(value = "lockedDays", required = false) Integer lockedDays,
			@RequestParam(value = "releaseType", required = false) Integer releaseType,
			@RequestParam(value = "releasePercent", required = false) BigDecimal releasePercent,
			@RequestParam(value = "lockedFee", required = false) BigDecimal lockedFee,
			@RequestParam(value = "releaseAmount", required = false) BigDecimal releaseAmount,
			@RequestParam(value = "releaseTimes", required = false) BigDecimal releaseTimes,

			@RequestParam(value = "password") String password,
			@SessionAttribute(SysConstant.SESSION_ADMIN) Admin admin) throws Exception {
		password = MD5.md5(password + md5Key);
		Assert.isTrue(password.equals(admin.getPassword()), messageSource.getMessage("WRONG_PASSWORD"));

		Activity result = activityFeign.findById(id);

		notNull(result, "validate activity!");

		if(title != null) result.setTitle(title);
		if(detail != null) result.setDetail(detail);
		if(status != null) result.setStatus(status);
		if(step != null) result.setStep(step);
		if(type != null) result.setType(type);
		if(startTime != null) result.setStartTime(startTime);
		if(endTime != null) result.setEndTime(endTime);
		if(totalSupply != null) result.setTotalSupply(totalSupply);
		if(price != null) result.setPrice(price);
		if(priceScale != null) result.setPriceScale(priceScale);
		if(unit != null) result.setUnit(unit);
		if(acceptUnit != null) result.setAcceptUnit(acceptUnit);
		if(amountScale != null) result.setAmountScale(amountScale);
		if(maxLimitAmout != null) result.setMaxLimitAmout(maxLimitAmout);
		if(minLimitAmout != null) result.setMinLimitAmout(minLimitAmout);
		if(limitTimes != null) result.setLimitTimes(limitTimes);
		if(settings != null) result.setSettings(settings);
		if(content != null) result.setContent(content);
		if(smallImageUrl != null) result.setSmallImageUrl(smallImageUrl);
		if(bannerImageUrl != null) result.setBannerImageUrl(bannerImageUrl);
		if(noticeLink != null) result.setNoticeLink(noticeLink);
		if(activityLink != null) result.setActivityLink(activityLink);
		if(leveloneCount != null) result.setLeveloneCount(leveloneCount);
		if(holdLimit != null) result.setHoldLimit(holdLimit);
		if(holdUnit != null) result.setHoldUnit(holdUnit);
		if(miningDays != null) result.setMiningDays(miningDays);
		if(miningDaysprofit != null) result.setMiningDaysprofit(miningDaysprofit);
		if(miningUnit != null) result.setMiningUnit(miningUnit);
		if(miningInvite != null) result.setMiningInvite(miningInvite);
		if(miningInvitelimit != null) result.setMiningInvitelimit(miningInvitelimit);
		if(miningPeriod != null) result.setMiningPeriod(miningPeriod);
		if(lockedUnit != null) result.setLockedUnit(lockedUnit);
		if(lockedPeriod != null) result.setLockedPeriod(lockedPeriod);
		if(lockedDays != null) result.setLockedDays(lockedDays);
		if(releaseType != null) result.setReleaseType(releaseType);
		if(releasePercent != null) result.setReleasePercent(releasePercent);
		if(lockedFee != null) result.setLockedFee(lockedFee);
		if(releaseAmount != null) result.setReleaseAmount(releaseAmount);
		if(releaseTimes != null) result.setReleaseTimes(releaseTimes);

		activityFeign.save(result);
		return success(messageSource.getMessage("SUCCESS"));
	}

	@RequiresPermissions("activity:activity:detail")
	@GetMapping("{id}/detail")
	public MessageResult detail(
			@PathVariable Long id) {
		Activity activity = activityFeign.findById(id);
		Assert.notNull(activity, "validate id!");
		return success(activity);
	}

	/**
	 * 分发活动币
	 * @return
	 */
	@RequiresPermissions("activity:activity:distribute")
	@PostMapping("distribute")
	@AccessLog(module = AdminModule.ACTIVITY, operation = "分发活动币Activity")
	@Transactional(rollbackFor = Exception.class)
	public MessageResult distribute(@RequestParam("oid") Long oid) {
		ActivityOrder order = activityOrderFeign.findById(oid);
		if(order == null) {
			return error(messageSource.getMessage("ORDER_NOT_FOUND"));
		}
		if(order.getState() != 1) {
			return error(messageSource.getMessage("ACTIVITY_NOT_FOUND"));
		}
		Activity activity = activityFeign.findById(order.getActivityId());
		if(activity == null) {
			return error(messageSource.getMessage("ACTIVITY_NOT_YET_ENDED"));
		}
		// 1、2、3、4类型活动需要判断是否处于派发阶段
		if(activity.getType() == 1 || activity.getType() == 2 || activity.getType() == 3 || activity.getType() == 4) {
			// 活动是否结束
			if(activity.getStep() != 2) {
				return error(messageSource.getMessage("FROZEN_COIN_WALLET_NOT_FOUND"));
			}
		}

		// type = 3（持仓瓜分）
		// 持仓瓜分活动详解：用户无需花费任何币就可以瓜分池内的数量的币，按照持仓比例进行瓜分
		if(activity.getType() == 3) {
			// 解冻接受币（acceptUnit）
			MemberWallet freezeWallet = memberWalletFeign.findByCoinUnitAndMemberId(activity.getAcceptUnit(), order.getMemberId());
			if(freezeWallet == null) {
				return error(messageSource.getMessage("ACTIVITY_COIN_WALLET_NOT_FOUND"));
			}
			memberWalletFeign.thawBalance(freezeWallet.getCoinId(),order.getMemberId(), order.getFreezeAmount());

			// 分发活动币（unit）(获取活动币钱包)
			MemberWallet distributeWallet = memberWalletFeign.findByCoinUnitAndMemberId(activity.getUnit(), order.getMemberId());

			if(distributeWallet == null) {
				return error(messageSource.getMessage("HOLDINGS_ACTIVITY_DISTRIBUTION"));
			}
			// 分发数量 = 持仓 / 总持仓 * 活动供应量
			BigDecimal disAmount = order.getFreezeAmount().divide(activity.getFreezeAmount()).multiply(activity.getTotalSupply()).setScale(activity.getAmountScale(), BigDecimal.ROUND_HALF_DOWN);
			// 用户钱包增加资产
			memberWalletFeign.increaseBalance(distributeWallet.getId(), disAmount);

			MemberTransaction memberTransaction = new MemberTransaction();
			memberTransaction.setFee(BigDecimal.ZERO);
			memberTransaction.setAmount(disAmount);
			memberTransaction.setMemberId(distributeWallet.getMemberId());
			memberTransaction.setSymbol(activity.getUnit());
			memberTransaction.setType(TransactionType.ACTIVITY_BUY.getCode());
			memberTransaction.setCreateTime(DateUtil.getCurrentDate());
			memberTransaction.setRealFee("0");
			memberTransaction.setDiscountFee("0");
			memberTransactionFeign.save(memberTransaction);

//			Member member = memberFeign.findMemberById(order.getMemberId());
//			try {
//				smsProvider.sendCustomMessage(member.getMobilePhone(), "尊敬的用户，您参与活动兑换的" + disAmount + activity.getUnit() + "已到账！");
//			} catch (Exception e) {
//				return error(e.getMessage());
//			}

			// 更新订单状态
			order.setState(2);// 已成交
			order.setAmount(disAmount); //成交数量
			activityOrderFeign.save(order);

			return success(messageSource.getMessage("HOLDINGS_ACTIVITY_DISTRIBUTION") + ":" + disAmount);
		}

		// type = 4（自由认购）
		if(activity.getType() == 4) {
			// 扣除接受币成交
			MemberWallet freezeWallet = memberWalletFeign.findByCoinUnitAndMemberId(activity.getAcceptUnit(), order.getMemberId());
			if(freezeWallet == null) {
				return error(messageSource.getMessage("FROZEN_COIN_WALLET_NOT_FOUND"));
			}
			memberWalletFeign.decreaseFrozen(freezeWallet.getId(), order.getTurnover());

			MemberTransaction memberTransaction1 = new MemberTransaction();
			memberTransaction1.setFee(BigDecimal.ZERO);
			memberTransaction1.setAmount(order.getTurnover().negate());
			memberTransaction1.setMemberId(freezeWallet.getMemberId());
			memberTransaction1.setSymbol(activity.getAcceptUnit());
			memberTransaction1.setType(TransactionType.ACTIVITY_BUY.getCode());
			memberTransaction1.setCreateTime(DateUtil.getCurrentDate());
			memberTransaction1.setRealFee("0");
			memberTransaction1.setDiscountFee("0");
			memberTransactionFeign.save(memberTransaction1);

			// 分发活动币
			BigDecimal disAmount = order.getAmount();
			MemberWallet distributeWallet = memberWalletFeign.findByCoinUnitAndMemberId(activity.getUnit(), order.getMemberId());
			if(distributeWallet == null) {
				return error(messageSource.getMessage("ACTIVITY_COIN_WALLET_NOT_FOUND"));
			}
			memberWalletFeign.increaseBalance(distributeWallet.getId(), disAmount);

			MemberTransaction memberTransaction = new MemberTransaction();
			memberTransaction.setFee(BigDecimal.ZERO);
			memberTransaction.setAmount(disAmount);
			memberTransaction.setMemberId(distributeWallet.getMemberId());
			memberTransaction.setSymbol(activity.getUnit());
			memberTransaction.setType(TransactionType.ACTIVITY_BUY.getCode());
			memberTransaction.setCreateTime(DateUtil.getCurrentDate());
			memberTransaction.setRealFee("0");
			memberTransaction.setDiscountFee("0");
			memberTransactionFeign.save(memberTransaction);

			// 更新订单状态
			order.setState(2);//已成交
			activityOrderFeign.save(order);

//			Member member = memberFeign.findMemberById(order.getMemberId());
//			try {
//				smsProvider.sendCustomMessage(member.getMobilePhone(), "尊敬的用户，您参与活动兑换的" + disAmount + activity.getUnit() + "已到账！");
//			} catch (Exception e) {
//				return error(e.getMessage());
//			}

			return success(messageSource.getMessage("FREE_SUBSCRIPTION_DISTRIBUTION") + ":" + disAmount);
		}

		// 云矿机销售类型
		if(activity.getType() == 5) {
			// 扣除接受币成交
			MemberWallet freezeWallet = memberWalletFeign.findByCoinUnitAndMemberId(activity.getAcceptUnit(), order.getMemberId());
			if(freezeWallet == null) {
				return error(messageSource.getMessage("FROZEN_COIN_WALLET_NOT_FOUND"));
			}
			memberWalletFeign.decreaseFrozen(freezeWallet.getId(), order.getTurnover());

			MemberTransaction memberTransaction1 = new MemberTransaction();
			memberTransaction1.setFee(BigDecimal.ZERO);
			memberTransaction1.setAmount(order.getTurnover().negate());
			memberTransaction1.setMemberId(freezeWallet.getMemberId());
			memberTransaction1.setSymbol(activity.getAcceptUnit());
			memberTransaction1.setType(TransactionType.ACTIVITY_BUY.getCode());
			memberTransaction1.setCreateTime(DateUtil.getCurrentDate());
			memberTransaction1.setRealFee("0");
			memberTransaction1.setDiscountFee("0");
			memberTransactionFeign.save(memberTransaction1);

			// 更新订单状态
			order.setState(2);//已成交
			activityOrderFeign.save(order);

			// 生成矿机
			for(int i = 0; i < order.getAmount().intValue(); i++) {
				Date currentDate = DateUtil.getCurrentDate();
				MiningOrder mo = new MiningOrder();
				mo.setActivityId(activity.getId());
				mo.setMemberId(order.getMemberId());
				mo.setMiningDays(activity.getMiningDays());
				mo.setMiningDaysprofit(activity.getMiningDaysprofit());
				mo.setMiningUnit(activity.getMiningUnit());
				mo.setCurrentDaysprofit(activity.getMiningDaysprofit());
				mo.setCreateTime(currentDate);
				mo.setEndTime(DateUtil.dateAddDay(currentDate, activity.getMiningDays()));
				mo.setImage(activity.getSmallImageUrl());
				mo.setTitle(activity.getTitle());
				mo.setMiningStatus(1); //挖矿状态（1：挖矿中）
				mo.setMiningedDays(0); //初始为0天
				mo.setTotalProfit(BigDecimal.ZERO);
				mo.setType(0); // 一般矿机
				mo.setMiningInvite(activity.getMiningInvite()); // 邀请
				mo.setMiningInvitelimit(activity.getMiningInvitelimit()); // 上限产能
				mo.setPeriod(activity.getMiningPeriod()); // 挖矿产出周期
				miningOrderFeign.save(mo);
			}
			Member member = memberFeign.findMemberById(order.getMemberId());
			// 邀请是否能增加产能(邀请一人参与仅能为一台矿机增加产能）
			if(activity.getMiningInvite().compareTo(BigDecimal.ZERO) > 0) {
				if(member != null) {
					if(member.getInviterId() != null) {
						Member inviter = memberFeign.findMemberById(member.getInviterId());
						List<MiningOrder> miningOrders = miningOrderFeign.findAllByMemberIdAndActivityId(inviter.getId(), activity.getId());
						if(miningOrders.size() > 0) {
							for(MiningOrder item : miningOrders) {
								// 如果当前产能低于极限产能
								if(item.getCurrentDaysprofit().subtract(item.getMiningDaysprofit()).divide(item.getMiningDaysprofit()).compareTo(activity.getMiningInvitelimit()) < 0) {
									// 获取新产能
									BigDecimal newMiningDaysprofit = item.getCurrentDaysprofit().add(item.getMiningDaysprofit().multiply(activity.getMiningInvite()));
									// 如果新产能比极限产能高
									if(newMiningDaysprofit.compareTo(item.getMiningDaysprofit().add(item.getMiningDaysprofit().multiply(activity.getMiningInvitelimit()))) > 0) {
										newMiningDaysprofit = item.getMiningDaysprofit().add(item.getMiningDaysprofit().multiply(activity.getMiningInvitelimit()));
									}
									item.setCurrentDaysprofit(newMiningDaysprofit);
									miningOrderFeign.save(item);
									break;
								}
							}
						}
					}
				}
			}

//			try {
//				smsProvider.sendCustomMessage(member.getMobilePhone(), "尊敬的用户，您购买的云矿机已部署成功！");
//			} catch (Exception e) {
//				return error(e.getMessage());
//			}
			return success(messageSource.getMessage("MINING_MACHINE_DEPLOYED"));
		}

		// 云矿机销售类型
		if(activity.getType() == 6) {
			if(activity.getReleaseTimes().compareTo(BigDecimal.ZERO) <=0  || activity.getReleaseTimes() == null) {
				return error(messageSource.getMessage("RELEASE_MULTIPLIER_CANNOT_BE_ZERO"));
			}
			// 扣除接受币成交
			MemberWallet freezeWallet = memberWalletFeign.findByCoinUnitAndMemberId(activity.getAcceptUnit(), order.getMemberId());
			if(freezeWallet == null) {
				return error(messageSource.getMessage("FROZEN_COIN_WALLET_NOT_FOUND"));
			}
			// 扣除了锁仓数量和门槛费
			memberWalletFeign.decreaseFrozen(freezeWallet.getId(), order.getTurnover());

			MemberTransaction memberTransaction1 = new MemberTransaction();
			memberTransaction1.setFee(BigDecimal.ZERO);
			memberTransaction1.setAmount(order.getTurnover().negate());
			memberTransaction1.setMemberId(freezeWallet.getMemberId());
			memberTransaction1.setSymbol(activity.getAcceptUnit());
			memberTransaction1.setType(TransactionType.ACTIVITY_BUY.getCode());
			memberTransaction1.setCreateTime(DateUtil.getCurrentDate());
			memberTransaction1.setRealFee("0");
			memberTransaction1.setDiscountFee("0");
			memberTransactionFeign.save(memberTransaction1);

			// ToRelease 增加待释放余额( = 用户参与数量 * 释放倍数）
			memberWalletFeign.increaseToRelease(freezeWallet.getId(), order.getAmount().multiply(activity.getReleaseTimes()));

			// 更新订单状态
			order.setState(2);//已成交
			activityOrderFeign.save(order);

			// 生成锁仓订单
			Date currentDate = DateUtil.getCurrentDate();
			LockedOrder lo = new LockedOrder();
			lo.setActivityId(activity.getId());
			lo.setMemberId(order.getMemberId());
			lo.setLockedDays(activity.getLockedDays());
			lo.setReleasedDays(0);
			lo.setReleaseUnit(activity.getLockedUnit());
			lo.setReleaseType(activity.getReleaseType());
			lo.setPeriod(activity.getLockedPeriod());
			lo.setLockedStatus(1); // 锁仓状态：释放中
			lo.setReleasePercent(activity.getReleasePercent());
			lo.setReleaseCurrentpercent(activity.getReleasePercent());
			lo.setImage(activity.getSmallImageUrl());
			lo.setTitle(activity.getTitle());
			lo.setTotalLocked(order.getAmount().multiply(activity.getReleaseTimes()));
			lo.setReleaseTimes(activity.getReleaseTimes());
			lo.setOriginReleaseamount(order.getAmount().multiply(activity.getReleaseTimes()).divide(BigDecimal.valueOf(activity.getLockedDays()), 8, BigDecimal.ROUND_HALF_DOWN));
			lo.setCurrentReleaseamount(order.getAmount().multiply(activity.getReleaseTimes()).divide(BigDecimal.valueOf(activity.getLockedDays()), 8, BigDecimal.ROUND_HALF_DOWN));
			lo.setTotalRelease(BigDecimal.ZERO);
			lo.setLockedInvite(activity.getMiningInvite());
			lo.setLockedInvitelimit(activity.getMiningInvitelimit());

			// 以日为周期的释放
			if(activity.getLockedPeriod() == 0) lo.setEndTime(DateUtil.dateAddDay(currentDate, activity.getLockedDays()));
			// 以周为周期的释放
			if(activity.getLockedPeriod() == 1) lo.setEndTime(DateUtil.dateAddDay(currentDate, activity.getLockedDays() * 7));
			// 以月为周期的释放
			if(activity.getLockedPeriod() == 2) lo.setEndTime(DateUtil.dateAddMonth(currentDate, activity.getLockedDays()));
			// 以年为周期的释放
			if(activity.getLockedPeriod() == 3) lo.setEndTime(DateUtil.dateAddYear(currentDate, activity.getLockedDays()));

			lockedOrderFeign.save(lo);

			Member member = memberFeign.findMemberById(order.getMemberId());
			// 邀请是否能增加产能(邀请一人参与仅能为一台矿机增加产能）
			if(activity.getMiningInvite().compareTo(BigDecimal.ZERO) > 0) {
				if(member != null) {
					if(member.getInviterId() != null) {
						Member inviter = memberFeign.findMemberById(member.getInviterId());
						List<LockedOrder> lockedOrders = lockedOrderFeign.findAllByMemberIdAndActivityId(inviter.getId(), activity.getId());
						if(lockedOrders.size() > 0) {
							for(LockedOrder item : lockedOrders) {
								// 如果当前产能低于极限产能
								if(item.getCurrentReleaseamount().subtract(item.getOriginReleaseamount()).divide(item.getOriginReleaseamount()).compareTo(activity.getMiningInvitelimit()) < 0) {
									// 获取新产能
									BigDecimal newReleaseAmount = item.getCurrentReleaseamount().add(item.getCurrentReleaseamount().multiply(activity.getMiningInvite()));
									// 如果新产能比极限产能高
									if(newReleaseAmount.compareTo(item.getOriginReleaseamount().add(item.getOriginReleaseamount().multiply(activity.getMiningInvitelimit()))) > 0) {
										newReleaseAmount = item.getOriginReleaseamount().add(item.getOriginReleaseamount().multiply(activity.getMiningInvitelimit()));
									}
									item.setCurrentReleaseamount(newReleaseAmount);
									lockedOrderFeign.save(item);
									break;
								}
							}
						}
					}
				}
			}

		/*	try {
				smsProvider.sendCustomMessage(member.getMobilePhone(), "尊敬的用户，您参与的锁仓活动已通过审核！");
			} catch (Exception e) {
				return error(e.getMessage());
			}*/
			return success(messageSource.getMessage("APPROVAL_SUCCESS"));
		}

		if(activity.getType() == 6) {

		}
		return error(messageSource.getMessage("UNKNOWN_ACTIVITY_TYPE"));
	}
	/**
	 * 管理员手工锁仓用户资产
	 * @param
	 * @return
	 */
	@RequiresPermissions("activity:activity:lock-member-coin")
	@PostMapping("lock-member-coin")
	@AccessLog(module = AdminModule.ACTIVITY, operation = "分页查看活动列表Activity")
	public MessageResult lockMemberCoin(@RequestParam("memberId") Long memberId,
										@RequestParam("activityId") Long activityId,
										@RequestParam("unit") String unit,
										@RequestParam("amount") BigDecimal amount) {
		// 检查用户是否存在
		Member member = memberFeign.findMemberById(memberId);
		org.springframework.util.Assert.notNull(member, messageSource.getMessage("USER"));

		// 检查活动是否存在
		Activity activity = activityFeign.findById(activityId);
		org.springframework.util.Assert.notNull(activity, messageSource.getMessage("ACTIVITY_NOT_FOUND"));
		// 检查活动是否在进行中
		if(activity.getType() != 6) {
			return MessageResult.error(messageSource.getMessage("NOT_LOCKING_ACTIVITY"));
		}
		// 检查活动是否在进行中
		if(activity.getStep() != 1) {
			return MessageResult.error(messageSource.getMessage("ACTIVITY_NOT_IN_PROGRESS"));
		}

		// 最低起兑量/最低锁仓量
		if(activity.getMinLimitAmout().compareTo(BigDecimal.ZERO) > 0) {
			if(activity.getMinLimitAmout().compareTo(amount) > 0) {
				return MessageResult.error(messageSource.getMessage("BELOW_MINIMUM_REDEMPTION_AMOUNT"));
			}
		}
		if(activity.getMaxLimitAmout().compareTo(BigDecimal.ZERO) > 0 || activity.getLimitTimes() > 0) {
			// 最高兑换量/最高锁仓量(先获取已下单的量)
			List<ActivityOrder> orderDetailList = activityOrderFeign.findAllByActivityIdAndMemberId(activityId,member.getId());
			BigDecimal alreadyAttendAmount = BigDecimal.ZERO;
			int alreadyAttendTimes = 0;
			if(orderDetailList != null) {
				alreadyAttendTimes = orderDetailList.size();
				for(int i = 0; i < orderDetailList.size(); i++) {
					if(activity.getType() == 3) {
						alreadyAttendAmount = alreadyAttendAmount.add(orderDetailList.get(i).getFreezeAmount());
					}else {
						alreadyAttendAmount = alreadyAttendAmount.add(orderDetailList.get(i).getAmount());
					}
				}
			}
			// 最高限购量
			if(activity.getMaxLimitAmout().compareTo(BigDecimal.ZERO) > 0) {
				if(alreadyAttendAmount.add(amount).compareTo(activity.getMaxLimitAmout()) > 0) {
					return MessageResult.error(messageSource.getMessage("EXCEEDS_MAXIMUM_REDEMPTION_AMOUNT"));
				}
			}
			// 个人限购次数
			if(activity.getLimitTimes() > 0) {
				if(activity.getLimitTimes() < alreadyAttendTimes + 1) {
					return MessageResult.error(messageSource.getMessage("EXCEEDS_PURCHASE_LIMIT"));
				}
			}
		}

		// 检查持仓要求
		if(activity.getHoldLimit().compareTo(BigDecimal.ZERO) > 0 && activity.getHoldUnit() != null && activity.getHoldUnit() != "") {
			MemberWallet holdCoinWallet = memberWalletFeign.findByCoinUnitAndMemberId(activity.getHoldUnit(), member.getId());
			if (holdCoinWallet == null) {
				return MessageResult.error(messageSource.getMessage("REQUIRED_HOLDINGS_WALLET_MISSING"));
			}
			if(holdCoinWallet.getIsLock().equals(BooleanEnum.IS_TRUE)){
				return MessageResult.error(messageSource.getMessage("REQUIRED_HOLDINGS_WALLET_LOCKED"));
			}
			if(holdCoinWallet.getBalance().compareTo(activity.getHoldLimit()) < 0) {
				return MessageResult.error(messageSource.getMessage("YOUR") + activity.getHoldUnit() +messageSource.getMessage("INSUFFICIENT_HOLDINGS_QUANTITY"));
			}
		}

		// 查询币种是否存在
		Coin coin;
		coin = coinFeign.findByUnit(activity.getAcceptUnit());
		if (coin == null) {
			return MessageResult.error(messageSource.getMessage("CURRENCY_NOT_FOUND"));
		}

		// 检查钱包是否可用
		MemberWallet acceptCoinWallet = memberWalletFeign.findByCoinUnitAndMemberId(activity.getAcceptUnit(), member.getId());
		if (acceptCoinWallet == null || acceptCoinWallet == null) {
			return MessageResult.error(messageSource.getMessage("USER_WALLET_NOT_FOUND"));
		}
		if(acceptCoinWallet.getIsLock().equals(BooleanEnum.IS_TRUE)){
			return MessageResult.error(messageSource.getMessage("USER_WALLET_LOCKED"));
		}

		// 检查余额是否充足
		BigDecimal totalAcceptCoinAmount = BigDecimal.ZERO;
		totalAcceptCoinAmount = amount.add(activity.getLockedFee()).setScale(activity.getAmountScale(), BigDecimal.ROUND_HALF_DOWN);

		if(acceptCoinWallet.getBalance().compareTo(totalAcceptCoinAmount) < 0){
			return MessageResult.error(messageSource.getMessage("INSUFFICIENT_USER_BALANCE"));
		}

		ActivityOrder activityOrder = new ActivityOrder();
		activityOrder.setActivityId(activityId);
		activityOrder.setAmount(amount); // 实际锁仓数量
		activityOrder.setFreezeAmount(totalAcceptCoinAmount); // 这里冻结的资产包含了用户实际锁仓数量和门槛费
		activityOrder.setBaseSymbol(activity.getAcceptUnit());
		activityOrder.setCoinSymbol(activity.getUnit());
		activityOrder.setCreateTime(DateUtil.getCurrentDate());
		activityOrder.setMemberId(member.getId());
		activityOrder.setPrice(activity.getPrice());
		activityOrder.setState(1); //未成交
		activityOrder.setTurnover(totalAcceptCoinAmount);//作为资产冻结或扣除资产的标准，锁仓活动中，此项目作为参与数量
		activityOrder.setType(activity.getType());

		MessageResult mr = activityOrderFeign.saveActivityOrder(activityOrder);

		if (mr.getCode() != 0) {
			return MessageResult.error(500, messageSource.getMessage("ACTIVITY_PARTICIPATION_FAILED") + ":" + mr.getMessage());
		}else {
			return MessageResult.success(messageSource.getMessage("LOCK_SUBMITTED_SUCCESS"));
		}
	}
}
