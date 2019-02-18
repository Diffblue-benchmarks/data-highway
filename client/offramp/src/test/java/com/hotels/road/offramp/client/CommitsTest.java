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
package com.hotels.road.offramp.client;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.time.Duration;
import java.util.Map;

import org.junit.Test;
import org.reactivestreams.Publisher;

import com.hotels.road.offramp.model.Message;

import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

public class CommitsTest {

  @Test(timeout = 50000)
  public void commit() throws Exception {
    Message<String> message1 = new Message<>(0, "k", 1L, 1, 1L, "a");
    Message<String> message2 = new Message<>(1, "k", 2L, 1, 1L, "b");
    Message<String> message3 = new Message<>(1, "k", 3L, 1, 1L, "c");
    Flux<Message<?>> messages = Flux.just(message1, message2, message3);

    Publisher<Map<Integer, Long>> batch = Commits.fromMessages(messages, Duration.ofSeconds(1));

    StepVerifier.create(batch).assertNext(offsets -> {
      assertThat(offsets.size(), is(2));
      assertThat(offsets.get(0), is(2L));
      assertThat(offsets.get(1), is(4L));
    }).verifyComplete();
  }

}
