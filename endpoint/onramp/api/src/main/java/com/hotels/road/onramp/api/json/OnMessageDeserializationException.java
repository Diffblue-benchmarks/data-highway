package com.hotels.road.onramp.api.json;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.fasterxml.jackson.core.JsonProcessingException;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Unable to handle incoming request.")
public class OnMessageDeserializationException extends JsonProcessingException {
  protected OnMessageDeserializationException(Throwable rootCause) {
    super(rootCause);
  }

  protected OnMessageDeserializationException(String msg) {
    super(msg);
  }
}
