/**
 ** Copyright © 2020, Oracle and/or its affiliates. All rights reserved.
 ** Licensed under the Universal Permissive License v 1.0 as shown at http://oss.oracle.com/licenses/upl.
 **/
package mushop.orders.resources;

import io.micronaut.serde.annotation.Serdeable;

/**
 * The messaging business object.
 */
@Serdeable
public class PaymentResponse {

    private final boolean authorised;
    private final String  message;

    public PaymentResponse(boolean authorised, String message) {
        this.authorised = authorised;
        this.message = message;
    }

    @Override
    public String toString() {
        return "PaymentResponse{" +
                "authorised=" + authorised +
                ", message=" + message +
                '}';
    }

    public boolean isAuthorised() {
        return authorised;
    }

    public String getMessage() {
        return message;
    }
}
