/**
 * Copyright © 2020, Oracle and/or its affiliates. All rights reserved.
 * Licensed under the Universal Permissive License v 1.0 as shown at http://oss.oracle.com/licenses/upl.
 **/
package mushop

import io.micronaut.http.client.RxHttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.test.extensions.spock.annotation.MicronautTest

import javax.inject.Inject

@MicronautTest
class FulfillmentControllerSpec extends AbstractFulfillmentSpec {

    @Inject
    @Client("/fulfillment")
    RxHttpClient httpClient

    void 'test Fulfillment Controller'() {
        expect:
        httpClient.toBlocking().retrieve("/123") == "Order 123 is fulfilled"
    }
}
