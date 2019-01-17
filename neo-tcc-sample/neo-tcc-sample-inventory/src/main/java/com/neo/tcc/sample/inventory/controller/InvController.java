package com.neo.tcc.sample.inventory.controller;

import com.lonntec.common.bean.result.SuccessResp;
import com.neo.tcc.sample.inventory.bean.Inv;
import com.neo.tcc.sample.inventory.bean.InvItem;
import com.neo.tcc.sample.inventory.bean.InvUse;
import com.neo.tcc.sample.inventory.service.InvService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private InvService invService;

    @PostMapping("/preuse")
    public SuccessResp preUse(@RequestBody @Valid InvUse inv) {
        log.info("preUse inventory completed...");
        return SuccessResp.build();
    }

    @PostMapping("/use")
    public SuccessResp use(@RequestBody @Valid InvUse inv) {
        Inv inv1 = new Inv();
        inv1.setNumber(inv.getNumber());
        inv1.setItems(inv.getItems());
        invService.use(inv.getTransactionContext(), inv1);
        log.info("use inventory completed...");
        return SuccessResp.build();
    }

    @PostMapping("/unuse")
    public SuccessResp unUse(@RequestBody @Valid InvUse inv) {
        log.info("unUse inventory completed...");
        return SuccessResp.build();
    }
}