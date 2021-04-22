/*
 * Copyright 2021 original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package mushop;

import io.micronaut.test.support.TestPropertyProvider
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.wait.strategy.LogMessageWaitStrategy
import spock.lang.Specification

import javax.annotation.Nonnull

abstract class AbstractFulfillmentSpec extends Specification implements TestPropertyProvider {

    static GenericContainer natsContainer =
            new GenericContainer("nats:latest")
                    .withExposedPorts(4222)
                    .waitingFor(new LogMessageWaitStrategy().withRegEx("(?s).*Server is ready.*"))

    static {
        natsContainer.start()
    }

    @Nonnull
    Map<String, String> getProperties() {
        return ["nats.addresses": "nats://localhost:${natsContainer.getMappedPort(4222)}"]
    }
}
