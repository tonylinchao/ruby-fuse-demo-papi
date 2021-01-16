package com.hkt.ruby.fuse.demo.controller;

import com.hkt.ruby.fuse.demo.utils.R;
import com.hkt.ruby.fuse.demo.utils.ResultCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.FluentProducerTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
@RequestMapping("/itar")
@Tag(name = "ITAR Test", description = "The ITAR API")
public class ItarController {

    @Autowired
    private FluentProducerTemplate fluentProducerTemplate;

    /**
     * Get Salesforce contacts via Mule
     */
    @Operation(summary = "ITAR digiSPACE Application List", description = "Get ITAR digiSPACE application list", tags = { "itar" })
    @GetMapping(path="/salesforce-contacts")
    public R getdigiSpaceAppList() {

        Exchange result = fluentProducerTemplate
                .to("direct:itar-digispace").send();

        return R.data(result, result.getIn().getBody(), ResultCode.SUCCESS.getMessage());
    }

    /**
     * Get ITAR digiSPACE application list from Redis
     */
    @Operation(summary = "ITAR digiSPACE Application List from Redis", description = "Get ITAR digiSPACE application list from Redis", tags = { "itar" })
    @GetMapping(path="/digispace-redis")
    public R getDigiSpaceAppListByRedis() {

        Exchange result = fluentProducerTemplate
                .to("direct:itar-digispace-redis").send();

        return R.data(result, result.getIn().getBody(), ResultCode.SUCCESS.getMessage());
    }
}
