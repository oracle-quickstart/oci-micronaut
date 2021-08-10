package mushop.carts.repositories;

import io.micrometer.core.annotation.Counted;
import io.micrometer.core.annotation.Timed;
import io.micronaut.context.annotation.Primary;
import io.micronaut.context.annotation.Requires;
import io.micronaut.context.annotation.Value;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.http.MediaType;
import io.micronaut.http.codec.CodecException;
import io.micronaut.http.codec.MediaTypeCodec;
import io.micronaut.http.codec.MediaTypeCodecRegistry;
import io.micronaut.transaction.annotation.ReadOnly;
import mushop.carts.entities.Cart;
import oracle.soda.OracleCursor;
import oracle.soda.OracleDatabase;
import oracle.soda.OracleDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import javax.json.Json;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implements CartRepository using Oracle Database JSON collections. SODA is the
 * simple CRUD-based API that allows the application to interact with document
 * collections in the autonomous database.
 */
@Singleton
@Primary
@Requires(property = "datasources.default.url")
public class CartRepositoryDatabaseImpl implements CartRepository {

    private static final Logger LOG = LoggerFactory.getLogger(CartRepositoryDatabaseImpl.class);

    /**
     * Used to automatically convert a Cart object to and from JSON
     */
    private final MediaTypeCodecRegistry codecRegistry;
    private final OracleDatabase db;
    private final String collectionName;

    public CartRepositoryDatabaseImpl(
            @NonNull @Value("${carts.collection}") String collectionName,
            @NonNull OracleDatabase db,
            @NonNull MediaTypeCodecRegistry codecRegistry) {
        this.collectionName = collectionName;
        this.codecRegistry = codecRegistry;
        this.db = db;
    }

    @Override
    @Transactional
    @Counted("carts.deleted")
    public boolean deleteCart(String id) {
        try {
            int ct = db.openCollection(collectionName).find().key(id).remove();
            return ct > 0;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @ReadOnly
    public Cart getById(String id) {
        try {
            OracleDocument doc = db.openCollection(collectionName).findOne(id);
            return doc == null ? null : getCodec(doc)
                    .decode(Cart.class, doc.getContentAsByteArray());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @ReadOnly
    public List<Cart> getByCustomerId(String custId) {
        if (custId == null) {
            throw new IllegalArgumentException("The customer id must be specified");
        }
        // Create query by example like {"customerId" : "123"}
        String filter = Json.createObjectBuilder().add("customerId", custId).build().toString();

        return getCarts(filter);
    }

    @Override
    @Transactional
    @Counted("carts.created.counter")
    @Timed("carts.created.timer")
    public void save(Cart cart) {
        doSave(cart);
    }

    @Override
    @Transactional
    @Counted("carts.updated.counter")
    @Timed("carts.updated.timer")
    public void update(Cart cart) {
        doSave(cart);
    }

    // TODO: SODA healthcheck endpoint
    @Override
    @ReadOnly
    public boolean healthCheck() {
        try {
            String name = db.openCollection(collectionName).admin().getName();
            return name != null;
        } catch (Exception e) {
            LOG.info("DB health-check failed.", e);
            return false;
        }
    }

    private void doSave(Cart cart) {
        try {
            OracleDocument cartDoc = db.createDocumentFromByteArray(
                    cart.getId(), getCodec(MediaType.APPLICATION_JSON_TYPE).encode(cart)
            );
            db.openCollection(collectionName).save(cartDoc);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Selects carts based on a "query by example"
     */
    private List<Cart> getCarts(String filter) {
        try{
            OracleDocument filterDoc = db.createDocumentFromString(filter);
            try (OracleCursor carts = db.openCollection(collectionName).find().filter(filterDoc).getCursor()) {
                List<Cart> result = new ArrayList<>();
                while (carts.hasNext()) {
                    OracleDocument doc = carts.next();
                    Cart cart = getCodec(doc).decode(Cart.class, doc.getContentAsByteArray());
                    result.add(cart);
                }
                return result;
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    private MediaTypeCodec getCodec(OracleDocument doc) {
        final MediaType mediaType = Optional
                .ofNullable(doc.getMediaType())
                .map(MediaType::new)
                .orElse(MediaType.APPLICATION_JSON_TYPE);
        return getCodec(mediaType);
    }

    private MediaTypeCodec getCodec(MediaType mediaType) {
        return codecRegistry
                .findCodec(mediaType)
                .orElseThrow(() -> new CodecException("No codec for type: " + mediaType));
    }
}
