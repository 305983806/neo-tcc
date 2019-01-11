package com.neo.tcc.sample.inventory.controller;

import com.lonntec.common.bean.result.SuccessResp;
import com.neo.tcc.sample.inventory.bean.InvUse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * @Auther: cp.Chen
 * @Date: 2019/1/10 15:45
 * @Description:
 */
@RestController
@RequestMapping("/inv")
public class InvController {
    private static final Logger log = LoggerFactory.getLogger(InvController.class);

    @PostMapping("/preuse")
    public SuccessResp preUse(@RequestBody @Valid InvUse inv) {
        log.info("preUse inventory completed...");
        return SuccessResp.build();
    }

    @PostMapping("/use")
    public SuccessResp use(@RequestBody @Valid InvUse inv) {
        log.info("use inventory completed...");
        return SuccessResp.build();
    }

    @PostMapping("/unuse")
    public SuccessResp unUse(@RequestBody @Valid InvUse inv) {
        log.info("unUse inventory completed...");
        return SuccessResp.build();
    }
}