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
package com.hotels.road.weighbridge.model;

import static java.util.Collections.singletonList;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;

import java.io.IOException;
import java.time.Duration;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;

@RunWith(MockitoJUnitRunner.class)
public class BrokerSerializerTest {

  private @Spy ObjectMapper objectMapper;
  private @InjectMocks BrokerSerializer brokerSerializer;
  private @Mock ObjectDataOutput objectDataOutput;
  private @Mock ObjectDataInput objectDataInput;
  private @Captor ArgumentCaptor<byte[]> outByteArrayCaptor;

  private byte[] bytes = new byte[] { 0, 1, 2, 3, 4 };

  final PartitionReplica pr = new PartitionReplica(0, true, true, 50, 51, 0, 2, 2);
  final Topic topic = new Topic("our-topic", Duration.ofSeconds(18000), singletonList(pr));
  final LogDir logDir = new LogDir("/disk0", 100, 100, singletonList(topic));
  final Broker broker = new Broker(0, "us-west-2", singletonList(logDir));

  byte[] bytesBroker;

  @Before
  public void before() throws IOException {
    objectMapper.registerModule(new JavaTimeModule());
    bytesBroker = objectMapper.writeValueAsBytes(broker);
  }

  @Test
  public void correct_serialisation() throws IOException {
    doNothing().when(objectDataOutput).writeByteArray(outByteArrayCaptor.capture());
    brokerSerializer.write(objectDataOutput, broker);
    byte[] out = outByteArrayCaptor.getValue();

    assertTrue(Arrays.equals(out, bytesBroker));
  }

  @Test
  public void correct_deserialisation() throws IOException {
    doReturn(bytesBroker).when(objectDataInput).readByteArray();
    Broker outBroker = brokerSerializer.read(objectDataInput);

    assertThat(outBroker.getId(), is(broker.getId()));
    assertThat(outBroker.getRack(), is(broker.getRack()));
    assertThat(outBroker.getLogDirs().size(), is(1));
    assertThat(outBroker.getLogDirs().get(0), is(logDir));
  }
}
