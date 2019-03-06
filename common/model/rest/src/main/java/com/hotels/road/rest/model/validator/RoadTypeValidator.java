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
package com.hotels.road.rest.model.validator;

import static java.util.stream.Collectors.toList;

import java.util.Arrays;
import java.util.List;

import com.hotels.road.rest.model.RoadType;

public class RoadTypeValidator implements ModelValidator<String> {
  private static final List<String> ROAD_TYPES =
      Arrays.asList(RoadType.values()).stream()
          .map(Enum::name)
          .flatMap(type -> Arrays.asList(type.toLowerCase(), type.toUpperCase()).stream())
          .collect(toList());
  private static final RoadTypeValidator VALIDATOR = new RoadTypeValidator();
  private static final String ERROR_MESSAGE = "Road type should be one of the following ["
      + String.join(",", ROAD_TYPES) + "]";

  @Override
  public String validate(String roadType) throws InvalidRoadTypeException {
    if (roadType == null || !ROAD_TYPES.contains(roadType)) {
      throw new InvalidRoadTypeException(ERROR_MESSAGE);
    }
    return roadType;
  }

  public static RoadType validateRoadType(String roadType) throws InvalidRoadTypeException {
    roadType = VALIDATOR.validate(roadType);
    return RoadType.valueOf(roadType.toUpperCase());
  }
}
