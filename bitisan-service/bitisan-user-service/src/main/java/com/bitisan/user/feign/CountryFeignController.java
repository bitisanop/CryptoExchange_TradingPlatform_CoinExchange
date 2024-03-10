package com.bitisan.user.feign;


import com.bitisan.controller.BaseController;
import com.bitisan.user.entity.Country;
import com.bitisan.user.service.CountryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 会员用户 前端控制器
 * </p>
 *
 * @author markchao
 * @since 2021-06-14
 */
@Api(tags = "国家")
@RestController
@RequestMapping("/countryFeign")
public class CountryFeignController extends BaseController {

    @Autowired
    private CountryService countryService;

//    @ApiOperation(value = "根据中文名称查找")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "zhName", value = "中文名称"),
//    })
    @GetMapping("findByZhName")
    public Country findByZhName(@RequestParam("zhName")String zhName) {
        Country country = countryService.findOne(zhName);
        return country;
    }


}

