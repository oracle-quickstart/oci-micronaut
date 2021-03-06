package catalogue.repositories;

import catalogue.model.Category;
import io.micronaut.data.repository.CrudRepository;

import java.util.List;

public interface CategoryRepository extends CrudRepository<Category, Long> {
    /**
     * @return Lists the category names
     */
    List<String> listName();
}
