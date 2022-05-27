package com.clairepay.gateway.service;

import com.clairepay.gateway.models.Merchant;
import com.clairepay.gateway.repository.MerchantRepository;
import com.clairepay.gateway.dto.MerchantDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MerchantService {

    private final MerchantRepository merchantRepository;

    @Autowired
    public MerchantService(MerchantRepository merchantRepository) {
        this.merchantRepository = merchantRepository;
    }

    public List<MerchantDTO> getMerchantList() {
       return merchantRepository.findAll()
               .stream()
               .map(this::convertEntityToDTO)
               .collect(Collectors.toList());
    }

    private MerchantDTO convertEntityToDTO(Merchant merchant) {
        MerchantDTO merchantDTO = new MerchantDTO();
        merchantDTO.setFirstName(merchant.getFirstName());
        merchantDTO.setLastName(merchant.getLastName());
        merchantDTO.setPhoneNumber(merchant.getPhoneNumber());
        merchantDTO.setEmail(merchant.getEmail());
        merchantDTO.setMerchantBalance(merchant.getMerchantBalance());
        return merchantDTO;

    }

    public List<MerchantDTO> getMerchant(Long merchantId){
        return merchantRepository.findById(merchantId)
                .stream()
                .map(this::convertEntityToDTO)
                .collect(Collectors.toList());
    }

}
