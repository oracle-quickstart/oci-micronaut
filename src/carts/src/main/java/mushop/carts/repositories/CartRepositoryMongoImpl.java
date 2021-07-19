package mushop.carts.repositories;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.DeleteResult;
import io.micrometer.core.annotation.Counted;
import io.micrometer.core.annotation.Timed;
import io.micronaut.context.annotation.Replaces;
import io.micronaut.context.annotation.Requires;
import io.micronaut.context.annotation.Value;
import io.micronaut.transaction.annotation.ReadOnly;
import mushop.carts.entities.Cart;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static com.mongodb.client.model.Filters.eq;

/**
 * Implements CartRepository using MongoDB collections.
 */
@Singleton
@Replaces(CartRepositoryDatabaseImpl.class)
@Requires(property = "mongodb.uri")
public class CartRepositoryMongoImpl implements CartRepository {

    private static final Logger LOG = LoggerFactory.getLogger(CartRepositoryMongoImpl.class);

    /**
     * Used to automatically convert a Cart object to and from JSON
     */

    private final MongoClient mongoClient;
    private final String collectionName;
    private final String databaseName;

    public CartRepositoryMongoImpl(
            @Value("${carts.database}") String databaseName,
            @Value("${carts.collection}") String collectionName,
            MongoClient mongoClient) {
        this.collectionName = collectionName;
        this.databaseName = databaseName;
        this.mongoClient = mongoClient;

    }

//    @EventListener
//    public void initCollection(ServiceReadyEvent event) {
//        mongoClient.getDatabase(databaseName).getCollection(collectionName);
//
//    }

    @Override
    @Counted("carts.deleted")
    public boolean deleteCart(String id) {
        try {
            Bson filter = eq("id", id);
            DeleteResult ct = getCollection().deleteMany(filter);
            return ct.getDeletedCount() > 0;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Cart getById(String id) {
        try {
            return getCollection().find(eq("_id", id)).first();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Cart> getByCustomerId(String custId) {
        if (custId == null) {
            throw new IllegalArgumentException("The customer id must be specified");
        }
        FindIterable<Cart> iterable  = getCollection().find(eq("customerId", custId));
        return StreamSupport
                .stream(iterable.spliterator(), false)
                .collect(Collectors.toList());
    }

    @Override
    @Counted("carts.created.counter")
    @Timed("carts.created.timer")
    public void save(Cart cart) {
        getCollection().insertOne(cart);

    }

    @Override
    @Counted("carts.updated.counter")
    @Timed("carts.updated.timer")
    public void update(Cart cart) {
        getCollection().replaceOne(eq("id", cart.getId()), cart);
    }


    private MongoCollection<Cart> getCollection() {
        return mongoClient
                .getDatabase(databaseName)
                .getCollection(collectionName, Cart.class);
    }

    @Override
    public boolean healthCheck() {
        return true;
    }
}
