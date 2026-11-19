package com.vention.curae.country.model;

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
@Table(name = "countries")
public class Country {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Long id;

	@Column(name = "iso_code", nullable = false, length = 2)
	private String isoCode;

	@Column(name = "name", nullable = false)
	private String name;

	@Column(name = "citizenship", nullable = false)
	private String citizenship;

	@Column(name = "zip_regex", nullable = false)
	private String zipRegex;

	@Column(name = "zip_example", length = 20)
	private String zipExample;

}
