package user.model;

import io.micronaut.data.annotation.AutoPopulated;
import io.micronaut.data.annotation.DateCreated;
import io.micronaut.data.annotation.DateUpdated;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.annotation.MappedProperty;
import io.micronaut.data.annotation.Relation;
import io.micronaut.data.annotation.Version;

import java.time.OffsetDateTime;
import java.util.UUID;

/*
 * Allows binding the results from the {@code USER_CARDS} database table.
 *
 * @see https://micronaut-projects.github.io/micronaut-data/latest/guide/#sqlMapping
 */
@MappedEntity("USER_CARDS")
public class UserCard {

    /**
     * The database ID column is represented as a UUID.
     * The {@code AutoPopulated} annotation indicates that Micronaut Data 
     * will generate a new UUID if none is already set and assign it to 
     * The entity prior to insert.
     * 
     * @see https://micronaut-projects.github.io/micronaut-data/latest/api/io/micronaut/data/annotation/AutoPopulated.html
     */
    @Id
    @AutoPopulated(updateable = false)
    private UUID id;

    /**
     * A version column used to help control concurrent updates using Optimistic Locking 
     * which can improve performance over Pessimistic locking.
     * 
     * @see https://micronaut-projects.github.io/micronaut-data/latest/guide/#optimisticLocking
     * @see https://en.wikipedia.org/wiki/Optimistic_concurrency_control
     */
    @Version
    private Long version;

    @MappedProperty("nmbr")
    private String number;

    //    @MinLength(10)
    private String longNum;

    private String expires;

    @DateCreated
    private OffsetDateTime createdAt;

    @DateUpdated
    private OffsetDateTime updatedAt;

    @Relation(value = Relation.Kind.MANY_TO_ONE)
    private User user;

    public UserCard() {
    }

    public UserCard(String number, String longNum, String expires) {
        this.number = number;
        this.longNum = longNum;
        this.expires = expires;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getLongNum() {
        return longNum;
    }

    public void setLongNum(String longNum) {
        this.longNum = longNum;
    }

    public String getExpires() {
        return expires;
    }

    public void setExpires(String expires) {
        this.expires = expires;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

}
