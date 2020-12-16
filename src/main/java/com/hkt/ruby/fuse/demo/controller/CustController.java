package com.hkt.ruby.fuse.demo.controller;

import com.hkt.ruby.fuse.demo.utils.R;
import com.hkt.ruby.fuse.demo.utils.ResultCode;
import org.apache.camel.Exchange;
import org.apache.camel.FluentProducerTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Demo REST API of Customer Info
 *
 * @author Tony C Lin
 */
@RestController
@RequestMapping("/customers")
public class CustController {
	private static final Logger logger = LoggerFactory.getLogger(CustController.class);

	@Autowired
	private FluentProducerTemplate fluentProducerTemplate;

	/**
	 * Get Customer Info by HKID
	 *
	 * @param hkid Customer HKID No.
	 */
	@GetMapping(path="/{hkid}")
	public R getCustomers(@PathVariable(value="hkid",required=true) String hkid) {

		return R.data(200, "Hello, this is Tony!");

//		logger.debug("Customer HKID No is - " + hkid);
//		Exchange result = fluentProducerTemplate
//			    .withHeader("hkid", hkid)
//			    .to("direct:customers").send();
//
//		return R.data(result, result.getIn().getBody(), ResultCode.SUCCESS.getMessage());
	}

	/**
	 * Test VPC
	 *
	 */
	@GetMapping(path="/")
	public R testVPC() {

		return R.data(200, "Enquiry customer list success!");
//		Exchange result = fluentProducerTemplate
//				.to("direct:test-vpc").send();
//
//		return R.data(result, result.getIn().getBody(), ResultCode.SUCCESS.getMessage());
	}

}