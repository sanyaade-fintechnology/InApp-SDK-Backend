package com.payleven.inapp.controller;

import com.payleven.inapp.domain.Merchant;
import com.payleven.inapp.service.MerchantService;
import org.apache.commons.lang.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.validation.Valid;

@RestController
public class MerchantController {

    @Autowired
    private MerchantService merchantService;

    private ModelMapper modelMapper;

    @PostConstruct
    public void init() {
        modelMapper = new ModelMapper();
    }

    @RequestMapping(value = "/merchants", method = RequestMethod.GET)
    @ResponseBody
    public Page<Merchant> lists(@RequestParam(value = "page", defaultValue = "0") int page, @RequestParam(value = "pageSize", defaultValue = "20") int pageSize) {
        final Page<Merchant> merchants = merchantService.getMerchants(page, pageSize);
        return merchants;
    }

    @RequestMapping(value = "/merchants", method = RequestMethod.POST)
    @ResponseBody
    public Merchant create(@RequestBody @Valid CreateMerchantApiRequest request) throws Exception {
        final Merchant merchant;

        final StringBuilder emailBuilder = new StringBuilder();
        emailBuilder.append("sample")
                    .append(request.getCountryCode().toUpperCase())
                    .append(StringUtils.capitalize(request.getType()))
                    .append("-")
                    .append(System.currentTimeMillis())
                    .append("@payleven.de");

        if ("limited".equalsIgnoreCase(request.getType())) {
            merchant = merchantService.onboardLimited(emailBuilder.toString(), request.getCountryCode());
        } else {
            merchant = merchantService.onboardSoleTrader(emailBuilder.toString(), request.getCountryCode());
        }
        return merchant;
    }
}
