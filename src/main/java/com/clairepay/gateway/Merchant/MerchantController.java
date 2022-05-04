package com.clairepay.gateway.Merchant;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/v1/clairepay/merchants")
public class MerchantController {
    private final MerchantService merchantService;

    @Autowired
    public MerchantController(MerchantService merchantService) {
        this.merchantService = merchantService;
    }

    @GetMapping()
    public List<MerchantDTO> getAllMerchants(){
        return merchantService.getMerchantList();

    }

    @GetMapping(path="/{merchantId}")
    public List<MerchantDTO> getMerchant(@PathVariable Long merchantId){
       return merchantService.getMerchant(merchantId);
    }



}
