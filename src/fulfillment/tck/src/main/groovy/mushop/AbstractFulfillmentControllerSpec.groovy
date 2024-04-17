/**
 * Copyright © 2020, Oracle and/or its affiliates. All rights reserved.
 * Licensed under the Universal Permissive License v 1.0 as shown at http://oss.oracle.com/licenses/upl.
 **/
package mushop

import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import jakarta.inject.Inject
import spock.lang.Specification

abstract class AbstractFulfillmentControllerSpec extends Specification {

    @Inject
    @Client("/fulfillment")
    HttpClient httpClient

    void 'test Fulfillment Controller'() {
        expect:
        httpClient.toBlocking().retrieve("/123") == "Order 123 is fulfilled"
    }
}
