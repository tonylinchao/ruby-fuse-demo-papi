package com.hkt.ruby.fuse.demo.controller;

import com.hkt.ruby.fuse.demo.utils.R;
import com.hkt.ruby.fuse.demo.utils.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.FluentProducerTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Demo REST API of Customer Info
 *
 * @author Tony C Lin
 */
@Slf4j
@RestController
@RequestMapping("/customers")
public class CustController {

	@Autowired
	private FluentProducerTemplate fluentProducerTemplate;

	/**
	 * Health test
	 *
	 */
	@GetMapping(path="/test")
	public R test() {
		return R.data(200, "Hello, this is Tony!");
	}

	/**
	 * Get Customer Info by HKID
	 *
	 * @param hkid Customer HKID No.
	 */
	@GetMapping(path="/{hkid}")
	public R getCustomers(@PathVariable(value="hkid",required=true) String hkid) {

		log.debug("Customer HKID No is - " + hkid);
		Exchange result = fluentProducerTemplate
			    .withHeader("hkid", hkid)
			    .to("direct:customers").send();

		return R.data(result, result.getIn().getBody(), ResultCode.SUCCESS.getMessage());
	}

	/**
	 * Call MuleSoft API for mTLS testing
	 */
	@GetMapping(path="/customer-info")
	public R customerInfo() {

		Exchange result = fluentProducerTemplate
				.to("direct:customer-info").send();

		return R.data(result, result.getIn().getBody(), ResultCode.SUCCESS.getMessage());
	}

}