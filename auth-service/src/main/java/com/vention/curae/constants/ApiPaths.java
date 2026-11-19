package com.vention.curae.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ApiPaths {

	public static final String AUTH_ENDPOINT = "/auth";

	public static final String ACCOUNT_ENDPOINT = "/account";

	public static final String USER_ENDPOINT = "/users";

	public static final String DOCTOR_ENDPOINT = "/doctors";

	public static final String ADMIN_ENDPOINT = "/admin";

	public static final String REGISTRATION_ENDPOINT = "/registration";

	public static final String SET_PASSWORD_ENDPOINT = "/set-password";

	public static final String CHANGE_PASSWORD_ENDPOINT = "/change-password";

	public static final String RESET_PASSWORD_REQUEST_ENDPOINT = "password-reset/request";

	public static final String RESET_PASSWORD_CONFIRM_ENDPOINT = "password-reset/confirm";

	public static final String RESET_PASSWORD_VERIFY_ENDPOINT = "password-reset/verify";

	public static final String COUNTRY_ENDPOINT = "/countries";

}
