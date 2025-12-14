package com.vention.medfast.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ApiPaths {

	public static final String BASE = "/api";

	public static final String USER_ENDPOINT = BASE + "/users";

	public static final String AUTH_ENDPOINT = BASE + "/auth";

	public static final String TEST_ENDPOINT = BASE + "/test";

    public static final String ACCOUNT_ENDPOINT =  BASE + "/account";

}
