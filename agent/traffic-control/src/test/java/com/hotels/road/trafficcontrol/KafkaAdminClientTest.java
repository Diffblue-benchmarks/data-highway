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
package com.hotels.road.trafficcontrol;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import com.hotels.road.rest.model.RoadType;

import kafka.utils.ZkUtils;

public class KafkaAdminClientTest {

  private int normalPartitions;
  private int compactPartitions;
  private int replicationFactor;
  private @Mock ZkUtils zkUtils;
  private @Mock Properties defaultTopicConfig;

  private KafkaAdminClient underTest;

  private Map<RoadType, Integer> type2Partitions = new HashMap<>();

  @Before
  public void before() {
    normalPartitions = 6;
    compactPartitions = 120;
    type2Partitions.put(RoadType.NORMAL, normalPartitions);
    type2Partitions.put(RoadType.COMPACT, compactPartitions);

    replicationFactor = 3;
    underTest = new KafkaAdminClient(
        zkUtils, normalPartitions, compactPartitions, replicationFactor, defaultTopicConfig);
  }

  @Test
  public void testThatAllRoadTypesAreCovered() {
    for (RoadType type : RoadType.values()) {
      assertThat(underTest.getPartitions(type), is(type2Partitions.get(type)));
    }
  }
}
