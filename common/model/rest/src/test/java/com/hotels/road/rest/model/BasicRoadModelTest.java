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
package com.hotels.road.rest.model;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;

import com.hotels.road.rest.model.validator.InvalidRoadNameException;

public class BasicRoadModelTest {
  ObjectMapper mapper = new ObjectMapper();

  @Test(expected = InvalidRoadNameException.class)
  public void disallowsRoadNamesStartingWithUnderscore() {
    new BasicRoadModel("_road", RoadType.NORMAL, null, null, null, true, null, null, null);
  }

  @Test(expected = InvalidRoadNameException.class)
  public void disallowsRoadNamesStartingWithADigit() {
    new BasicRoadModel("2road", RoadType.COMPACT, null, null, null, true, null, null, null);
  }

  @Test
  public void deserialisationWithNullsToDefaultValues() throws Exception {
    String serialisedBasicRoadModel = getSerialisedBasicRoadModel("my_road", null, null,
        null, null, null, null, null, null);

    BasicRoadModel derialisedBasicRoadModel = mapper.readValue(serialisedBasicRoadModel, BasicRoadModel.class);
    assertThat(derialisedBasicRoadModel.type, is(RoadType.NORMAL));
    assertNull(derialisedBasicRoadModel.description);
    assertNull(derialisedBasicRoadModel.teamName);
    assertNull(derialisedBasicRoadModel.contactEmail);
    assertThat(derialisedBasicRoadModel.isEnabled(), is(false));
    assertNull(derialisedBasicRoadModel.partitionPath);
    assertNull(derialisedBasicRoadModel.authorisation);
    assertNull(derialisedBasicRoadModel.metadata);
  }

  @Test(expected = InvalidFormatException.class)
  public void incorrectRoadType() throws Exception {
    String compactedBasicRoadModelString = getSerialisedBasicRoadModel("my_road", "", null,
        null, null, null, null, null, null);

    mapper.readValue(compactedBasicRoadModelString, BasicRoadModel.class);
  }

  @Test
  public void upperCaseRoadType() throws Exception {
    List<String> roadTypes = Arrays.asList(RoadType.values()).stream().map(Enum::name).collect(Collectors.toList());

    for (String roadType : roadTypes) {

      String jsonLowerType = getSerialisedBasicRoadModel("my_road", roadType.toUpperCase(),
          null, null, null, null, null, null,
          null);
      try{
        mapper.readValue(jsonLowerType, BasicRoadModel.class);
      } catch (Exception e) {
        fail(e.getMessage());
      }

      String jsonUpperType = getSerialisedBasicRoadModel("my_road", roadType.toLowerCase(),
          null, null, null, null, null, null,
          null);
      try{
        mapper.readValue(jsonUpperType, BasicRoadModel.class);
        fail("No exception raised on lower case de-serialisation of RoadType enum");
      } catch (Exception e) {
        assertThat(e.getMessage(), startsWith("Cannot deserialize value of type "
            + "`com.hotels.road.rest.model.RoadType` from String \"" + roadType.toLowerCase()
            + "\": value not one of declared Enum instance names: [COMPACT, NORMAL]"));
      }
    }
  }

  private String getSerialisedBasicRoadModel(
      String name, String type, String description, String teamName,
      String contactEmail, String enabled, String partitionPath,
      String authorisation, String metadata) {
    return "{"
        + "\"name\":"           + (name == null          ? "null" : "\"" + name + "\""         ) + ","
        + "\"type\":"           + (type == null          ? "null" : "\"" + type + "\""         ) + ","
        + "\"description\":"    + (description == null   ? "null" : "\"" + description + "\""  ) + ","
        + "\"teamName\":"       + (teamName == null      ? "null" : "\"" + teamName + "\""     ) + ","
        + "\"contactEmail\":"   + (contactEmail == null  ? "null" : "\"" + contactEmail + "\"" ) + ","
        + "\"enabled\":"        + (enabled == null       ? "null" : "\"" + enabled + "\""      ) + ","
        + "\"partitionPath\":"  + (partitionPath == null ? "null" : "\"" + partitionPath + "\"") + ","
        + "\"authorisation\":"  + (authorisation == null ? "null" : "\"" + authorisation + "\"") + ","
        + "\"metadata\":"       + (metadata == null      ? "null" : "\"" + metadata + "\""     ) + ""
        + "}";
  }
}
