/**
 * * Copyright © 2020, Oracle and/or its affiliates. All rights reserved.
 * * Licensed under the Universal Permissive License v 1.0 as shown at http://oss.oracle.com/licenses/upl.
 **/
package mushop.orders.repositories;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.data.annotation.Join;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.jpa.repository.JpaRepository;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import mushop.orders.entities.CustomerOrder;

import javax.validation.constraints.NotNull;
import java.util.Optional;

/**
 * Customer order repository.
 */
@Repository
public interface CustomerOrderRepository extends JpaRepository<CustomerOrder, Long> {

    @Join(value = "customer", type = Join.Type.FETCH)
    Page<CustomerOrder> findByCustomerId(String name, Pageable p);
}

