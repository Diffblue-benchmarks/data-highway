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
