package com.clairepay.gateway.Merchant;


import com.clairepay.gateway.dto.MerchantDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/clairepay/merchants")
public class MerchantController {
    private final MerchantService merchantService;
    private Long merchantId;
    private String apiKey;

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
