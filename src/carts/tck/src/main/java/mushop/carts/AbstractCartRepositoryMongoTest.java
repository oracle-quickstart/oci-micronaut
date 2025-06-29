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
package mushop.carts;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import jakarta.inject.Inject;
import mushop.carts.entities.Cart;
import mushop.carts.entities.Item;
import mushop.carts.repositories.CartRepository;

abstract class AbstractCartRepositoryMongoTest  {

    @Inject
    CartRepository cartRepository;


    @Test
    void testCartRepositoryResolution(){
        assertNotNull(cartRepository);
    }

    @Test
    void testCartRepository() {
        Item item = new Item("ab", "item id", 3, new BigDecimal(101));
        Cart cart = new Cart("1234", "abcd", List.of(item));
        cart.getItems().add(item);
        cartRepository.save(cart);

        Optional<Cart> acart = cartRepository.findById("1234");
        assertTrue(acart.isPresent());
        assertEquals(cart.customerId(), acart.get().customerId());

        List<Cart> customerCart = cartRepository.getByCustomerId("abcd");
        assertNotNull(customerCart);
        assertEquals(1, customerCart.size());
        assertEquals(cart.customerId(), customerCart.get(0).customerId());
    }
}
