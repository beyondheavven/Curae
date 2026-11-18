package com.vention.medfast.address.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "addresses")
public class Address {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable = false)
	private Long id;

	@Column(name = "street_name", nullable = false)
	private String street;

	@Column(name = "city_name", nullable = false)
	private String city;

	@Column(name = "house_number", nullable = false)
	private String houseNumber;

	@Column(name = "state_name", nullable = false)
	private String state;

	@Column(name = "apartment_number", nullable = false)
	private String apartment;

	@Column(name = "zip_code", nullable = false)
	private String zipCode;

	@Column(name = "phone")
	private String phone;

}
