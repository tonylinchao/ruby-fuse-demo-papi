package com.hkt.ruby.fuse.demo.controller;

import com.hkt.ruby.fuse.demo.utils.R;
import com.hkt.ruby.fuse.demo.utils.ResultCode;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.FluentProducerTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * MuleSoft VPC Test
 *
 * @author Tony C Lin
 */
@Slf4j
@RestController
@RequestMapping("/vpc")
@Tag(name = "MuleSoft VPC", description = "The MuleSoft VPC Test API")
public class VpcController {

    @Autowired
    private FluentProducerTemplate fluentProducerTemplate;

    /**
     * Test VPC
     */
    @GetMapping(path="/test", produces={"application/json"})
    public R testVPC() {

        Exchange result = fluentProducerTemplate
                .to("direct:vpc-test").send();

        return R.data(result, result.getIn().getBody(), ResultCode.SUCCESS.getMessage());
    }

}