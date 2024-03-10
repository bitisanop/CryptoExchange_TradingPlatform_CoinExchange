package com.bitisan.user.feign;

import com.bitisan.controller.BaseController;
import com.bitisan.user.entity.Addressext;
import com.bitisan.user.entity.Coinprotocol;
import com.bitisan.user.service.AddressextService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * @author Bitisan E-Mali:bitisanop@gmail.com
 * @Description: coin
 * @date 2021/4/214:20
 */

@RestController
@RequestMapping("/addressFeign")
public class AddressFeignController extends BaseController {

    @Autowired
    private AddressextService addressextService;

    @PostMapping(value = "/findByAddress")
    public Addressext findByAddress(@RequestParam("address")String address){
        return addressextService.findByAddress(address);
    }

    @PostMapping(value = "/save")
    public Addressext save(@RequestBody Addressext addressext){
       return addressextService.saveAndFlush(addressext);
    }

}
