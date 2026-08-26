package com.example.controller;

import com.example.common.APIResponse;
import com.example.common.ResponeCode;
import com.example.service.OverviewService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/overview")
public class OverViewController {

    @Autowired
    OverviewService overviewService;

    private final static Logger logger = LoggerFactory.getLogger(OverViewController.class);

    @RequestMapping(value = "/sysInfo", method = RequestMethod.GET)
    public APIResponse sysInfo() {
        Map<String, String> map = overviewService.getSystemInfo();
        return new APIResponse(ResponeCode.SUCCESS, "查询成功", map);
    }

    @RequestMapping(value = "/count", method = RequestMethod.GET)
    public APIResponse count() {
        Map<String, Object> map = overviewService.getStatistics();
        return new APIResponse(ResponeCode.SUCCESS, "查询成功", map);
    }


}
