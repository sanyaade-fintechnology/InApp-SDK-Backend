package com.payleven.controller;

import com.payleven.service.EbankVerificationTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class EbankController {

    @Autowired
    private EbankVerificationTokenService tokenService;

    @Value("${payleven.iframe.url}")
    String iframeUrl;

    @Value("${payleven.country}")
    String country;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String ebankVerificationPage(Model model) throws Exception {

        String verificationToken = tokenService.getVerificationToken();
        model.addAttribute("url", iframeUrl.replace("%country%", country).replace("%token%", verificationToken));

        return "index";
    }
}
