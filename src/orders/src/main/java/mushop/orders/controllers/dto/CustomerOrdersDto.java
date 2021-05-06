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
package mushop.orders.controllers.dto;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.data.model.Page;
import io.micronaut.http.hateoas.AbstractResource;
import io.micronaut.http.hateoas.Resource;
import mushop.orders.entities.CustomerOrder;

import java.util.Collection;
import java.util.Map;

@Introspected
public class CustomerOrdersDto extends AbstractResource<CustomerOrdersDto> {

    private final Map<String, Integer> page;

    public CustomerOrdersDto(Collection<CustomerOrderDto> customerOrders, Page<CustomerOrder> page) {
        embedded("customerOrders", customerOrders.toArray(new Resource[0]));
        this.page = Map.of(
                "size", page.getSize(),
                "totalElements", page.getNumberOfElements(),
                "totalPages", page.getTotalPages(),
                "number", page.getPageNumber()
        );
    }

    public Map<String, Integer> getPage() {
        return page;
    }
}
