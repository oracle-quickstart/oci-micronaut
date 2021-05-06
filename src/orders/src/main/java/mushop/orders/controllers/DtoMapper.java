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
package mushop.orders.controllers;

import io.micronaut.data.model.Page;
import mushop.orders.controllers.dto.CustomerDto;
import mushop.orders.controllers.dto.CustomerOrderDto;
import mushop.orders.controllers.dto.CustomerOrdersDto;
import mushop.orders.controllers.dto.ItemDto;
import mushop.orders.entities.Customer;
import mushop.orders.entities.CustomerOrder;
import mushop.orders.entities.Item;
import org.mapstruct.Mapper;

import javax.inject.Singleton;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "jsr330")
public abstract class DtoMapper {

    public abstract CustomerDto toCustomerDto(Customer customer);

    public abstract ItemDto toItemDto(Item item);

    public abstract List<ItemDto> toItemDtoList(Collection<Item> items);

    public CustomerOrderDto toCustomerOrderDto(CustomerOrder customerOrder) {
        return new CustomerOrderDto(
                toCustomerDto(customerOrder.getCustomer()),
                customerOrder.getAddress(),
                customerOrder.getCard(),
                toItemDtoList(customerOrder.getItems()),
                customerOrder.getShipment(),
                customerOrder.getOrderDate(),
                customerOrder.getTotal()
        );
    }

    public List<CustomerOrderDto> toCustomerOrderDtoList(List<CustomerOrder> customerOrderList) {
        return customerOrderList.stream()
                .map(this::toCustomerOrderDto)
                .collect(Collectors.toList());
    }

    public CustomerOrdersDto toCustomerOrdersDto(Page<CustomerOrder> page) {
        return new CustomerOrdersDto(toCustomerOrderDtoList(page.getContent()), page);
    }

}
