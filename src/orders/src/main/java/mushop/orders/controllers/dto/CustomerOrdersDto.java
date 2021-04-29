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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Emulates spring RepositoryRestResource
 */
@Introspected
public class CustomerOrdersDto {

    private final Map<String, Object> _embedded;
    private final Map<String, Integer> page;

    public CustomerOrdersDto(Collection<CustomerOrderDto> customerOrders, int size, int totalElements, int totalPages, int number) {
        this._embedded = new HashMap<>(2);
        this._embedded.put("customerOrders", customerOrders);
        this.page = Map.of(
                "size", size,
                "totalElements", totalElements,
                "totalPages", totalPages,
                "number", number
        );
    }

    public Map<String, Object> get_embedded() {
        return _embedded;
    }

    public Map<String, Integer> getPage() {
        return page;
    }
}
