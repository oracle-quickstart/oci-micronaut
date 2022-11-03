package mushop.carts.repositories;

import java.util.List;

import io.micronaut.data.mongodb.annotation.MongoRepository;
import io.micronaut.data.repository.CrudRepository;
import mushop.carts.entities.Cart;

/**
 * An interface to a collection of shopping carts (Cart objects).
 */
@MongoRepository(databaseName = "${carts.database}")
public interface CartRepository extends CrudRepository<Cart, String> {

    /**
     * Selects carts that have the same customer id
     */
    List<Cart> getByCustomerId(String customerId);
}
