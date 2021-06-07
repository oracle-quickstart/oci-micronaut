package user.model;

import edu.umd.cs.findbugs.annotations.NonNull;
import io.micronaut.data.annotation.Join;
import io.micronaut.data.repository.CrudRepository;

import javax.validation.constraints.NotNull;
import java.util.Optional;
import java.util.UUID;

@Join(value = "addresses", type = Join.Type.LEFT_FETCH)
@Join(value = "cards", type = Join.Type.LEFT_FETCH)
public interface UserRepository extends CrudRepository<User, UUID> {

    Optional<User> findByUsername(String username);

    @NonNull
    @Override
    Optional<User> findById(@NonNull @NotNull UUID uuid);
}
