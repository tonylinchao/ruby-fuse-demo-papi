package com.hkt.ruby.fuse.demo.model;

import lombok.Data;

import java.util.Date;

@Data
public class CustomerDetail {
    private String hkid;
    private String lastName;
    private String firstName;
    private String email;
    private String phone;
    private PostalAddress postalAddress;
    private DeliveryAddress deliveryAddress;
    private String requestDate;
}
