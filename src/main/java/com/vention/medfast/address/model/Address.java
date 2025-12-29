package com.vention.medfast.address.model;

import jakarta.persistence.*;
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

	@Column(name = "street_name", nullable = false, length = 255)
	private String street;

	@Column(name = "city_name", nullable = false, length = 255)
	private String city;

	@Column(name = "house_number", nullable = false, length = 255)
	private String houseNumber;

	@Column(name = "state_name", nullable = false, length = 255)
	private String state;

	@Column(name = "apartment_number", nullable = false, length = 255)
	private String apartment;

	@Column(name = "zip_code", nullable = false, length = 255)
	private String zipCode;

	@Column(name = "phone", nullable = false, length = 255)
	private String phone;

}
