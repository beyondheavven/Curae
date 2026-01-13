package com.vention.medfast.wrapper;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Setter
@Getter
public class ResponseWrapper<T> {

	private T data;

	private ResponseError error;

	private HttpStatus status;

	private int statusCode;

	public static <T> ResponseWrapper<T> ok(T data) {
		ResponseWrapper<T> wrapper = new ResponseWrapper<>();
		wrapper.setData(data);
		wrapper.setStatus(HttpStatus.OK);
		wrapper.setStatusCode(HttpStatus.OK.value());
		return wrapper;
	}

	public static <T> ResponseWrapper<T> withError(HttpStatus status, String message, ErrorCode code) {
		ResponseWrapper<T> wrapper = new ResponseWrapper<>();
		wrapper.setError(new ResponseError(code, message));
		wrapper.setStatus(status);
		wrapper.setStatusCode(status.value());
		return wrapper;
	}

	public static <T> ResponseWrapper<T> withStatus(HttpStatus status, T data) {
		ResponseWrapper<T> wrapper = new ResponseWrapper<>();
		wrapper.setData(data);
		wrapper.setStatus(status);
		wrapper.setStatusCode(status.value());
		return wrapper;
	}

}
