package com.vention.medfast.wrapper;

import com.fasterxml.jackson.annotation.JsonInclude;
import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@JsonInclude(NON_NULL)
@Getter
@Setter
public class ResponseWrapper<T> {

	private T data;

	private ResponseError error;

	private HttpStatus status;

	private int statusCode;

	private static OffsetDateTime now() {
		return OffsetDateTime.now(ZoneOffset.UTC);
	}

	public static <T> ResponseWrapper<T> ok(T data) {
		ResponseWrapper<T> wrapper = new ResponseWrapper<>();
		wrapper.setData(data);
		wrapper.setStatus(HttpStatus.OK);
		wrapper.setStatusCode(HttpStatus.OK.value());
		return wrapper;
	}

	public static <T> ResponseWrapper<T> withError(HttpStatus status, String message, ErrorCode code) {
		ResponseWrapper<T> wrapper = new ResponseWrapper<>();
		wrapper.setError(new CommonError(code, message, now()));
		wrapper.setStatus(status);
		wrapper.setStatusCode(status.value());
		return wrapper;
	}

	public static <T> ResponseWrapper<T> withValidationError(HttpStatus status, String message,
			List<FieldValidationError> validationErrors) {
		ResponseWrapper<T> wrapper = new ResponseWrapper<>();
		wrapper.setError(new ValidationError(message, validationErrors, now()));
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
