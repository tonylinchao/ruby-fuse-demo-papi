package com.hkt.ruby.fuse.demo.controller;

import com.hkt.ruby.fuse.demo.model.CustomerDetail;
import com.hkt.ruby.fuse.demo.model.DeliveryAddress;
import com.hkt.ruby.fuse.demo.model.PostalAddress;
import com.hkt.ruby.fuse.demo.utils.R;
import com.hkt.ruby.fuse.demo.utils.ResultCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.FluentProducerTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;

/**
 * Demo REST API of Customer Info
 *
 * @author Tony C Lin
 */
@Slf4j
@RestController
@RequestMapping("/customers")
@Tag(name = "Customers", description = "The Customer API")
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
	@Operation(summary="Get customer detail", description="Get customer detail by HKID", tags={"customer"})
	@ApiResponses(value = {
			@ApiResponse(responseCode="200", description="Successful operation", content=@Content(
					schema=@Schema(implementation=R.class))),
			@ApiResponse(responseCode="400", description="Invalid HKID supplied", content=@Content),
			@ApiResponse(responseCode="404", description="Customer not found", content=@Content)
	})
	@GetMapping(path="/{hkid}")
	public R getCustomerByHKID(@Parameter(description = "HKID card number", required = true)
							  @PathVariable(value="hkid",required=true) String hkid) {

		log.debug("Customer HKID No is - " + hkid);
		Exchange result = fluentProducerTemplate
			    .withHeader("hkid", hkid)
			    .to("direct:customers").send();

		return R.data(result, result.getIn().getBody(), ResultCode.SUCCESS.getMessage());
	}

	/**
	 * Call MuleSoft API for mTLS testing
	 */
	@Operation(summary = "Get customer info", description = "Get customer info", tags = { "customer" })
	@GetMapping(path="/customer-info")
	public R getCustomerInfo() {

		Exchange result = fluentProducerTemplate
				.to("direct:customer-info").send();

		return R.data(result, result.getIn().getBody(), ResultCode.SUCCESS.getMessage());
	}

	/**
	 * Get customer detail for performance testing
	 */
	@Operation(summary = "Get customer detail", description = "Get customer detail", tags = { "customer" })
	@GetMapping(path="/John/detail")
	public R<CustomerDetail> getCustomerDetail() {

		DeliveryAddress deliveryAddress = new DeliveryAddress();
		deliveryAddress.setCity("Hong Kong");
		deliveryAddress.setCountry("China");
		deliveryAddress.setPostalCode("00000");
		deliveryAddress.setStreet("39 Queens Road Central");

		PostalAddress postalAddress = new PostalAddress();
		postalAddress.setCity("Hong Kong");
		postalAddress.setCountry("China");
		postalAddress.setPostalCode("00000");
		postalAddress.setStreet("168 Lockhart Road");

		CustomerDetail customerDetail = new CustomerDetail();
		customerDetail.setHkid("R12345678");
		customerDetail.setLastName("Chan");
		customerDetail.setFirstName("John");
		customerDetail.setEmail("john.chan@hkt.com");
		customerDetail.setPhone("+852 6688 1688");
		customerDetail.setDeliveryAddress(deliveryAddress);
		customerDetail.setPostalAddress(postalAddress);

		try {
			TimeUnit.SECONDS.sleep(1); // sleep 1s
		} catch (InterruptedException ie) {
			Thread.currentThread().interrupt();
		}

		return R.data(customerDetail);
	}

	/**
	 * Get Salesforce contacts via Mule (mTLS)
	 */
	@Operation(summary = "Salesforce contacts", description = "Get contacts from salesforce", tags = { "perftest" })
	@GetMapping(path="/salesforce-contacts")
	public R getSalesforceContacts() {

		Exchange result = fluentProducerTemplate
				.to("direct:salesforce-contacts").send();

		return R.data(result, result.getIn().getBody(), ResultCode.SUCCESS.getMessage());
	}

}