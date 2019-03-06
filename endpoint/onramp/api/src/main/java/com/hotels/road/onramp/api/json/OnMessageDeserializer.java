/**
 * Copyright (C) 2016-2019 Expedia Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hotels.road.onramp.api.json;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import com.hotels.road.onramp.api.OnMessage;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class OnMessageDeserializer extends JsonDeserializer<OnMessage> {
  @Override
  public OnMessage deserialize(JsonParser p, DeserializationContext ctx)
      throws IOException, JsonProcessingException {
    ObjectCodec oc = p.getCodec();
    JsonNode node = oc.readTree(p);

    if (node.size() != 2) {
      log.error("Failed to deserialize OnMessage object: unknown field.");
      throw new OnMessageDeserializationException("Failed to deserialize OnMessage object: unknown field.");
    }

    try {
      JsonNode jsonKey = node.get("key");
      String key = jsonKey.isNull() ? null : jsonKey.asText();
      JsonNode jsonValue = node.get("value");
      ObjectNode value = jsonValue.isNull() ? null : node.get("value").deepCopy();
      return new OnMessage(key, value);
    } catch (Exception e) {
      log.error("Failed to deserialize OnMessage object.", e);
      throw new OnMessageDeserializationException(e);
    }
  }
}
