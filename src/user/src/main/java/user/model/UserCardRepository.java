package user.model;

import io.micronaut.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserCardRepository extends CrudRepository<UserCard, UUID> {

    Optional<UserCard> findByIdAndUserId(UUID id, UUID userId);

    List<UserCard> findByUserId(UUID id);

}
