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

import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.test.support.TestPropertyProvider;
import mushop.carts.entities.Cart;
import mushop.carts.entities.Item;
import mushop.carts.repositories.CartRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import jakarta.inject.Inject;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
@MicronautTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CartRepositoryMongoTest implements TestPropertyProvider {

    @Container
    final MongoDBContainer mongoDBContainer = new MongoDBContainer(DockerImageName.parse("mongo:4.0.10"));

    @Inject
    CartRepository cartRepository;

    @Override
    public Map<String, String> getProperties() {
        mongoDBContainer.start();
        return Map.of(
                "mongodb.uri", mongoDBContainer.getReplicaSetUrl("mushop"),
                "mongodb.package-names", "mushop.carts",
                "micronaut.data.mongodb.create-collections", "true",
                "carts.database", "mushop"
        );
    }

    @Test
    void testCartRepositoryResolution(){
        assertNotNull(cartRepository);
    }

    @Test
    void testCartRepository() {
        Cart cart = new Cart("1234");
        cart.setCustomerId("abcd");
        Item item = new Item();
        item.setItemId("item id");
        item.setId("ab");
        item.setQuantity(3);
        item.setUnitPrice(new BigDecimal(101));
        cart.getItems().add(item);
        cartRepository.save(cart);

        Optional<Cart> acart = cartRepository.findById("1234");
        assertTrue(acart.isPresent());
        assertEquals(cart.getCustomerId(), acart.get().getCustomerId());

        List<Cart> customerCart = cartRepository.getByCustomerId("abcd");
        assertNotNull(customerCart);
        assertEquals(1, customerCart.size());
        assertEquals(cart.getCustomerId(), customerCart.get(0).getCustomerId());
    }
}
