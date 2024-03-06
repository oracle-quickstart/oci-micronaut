package catalogue.model;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.annotation.GeneratedValue;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;

import jakarta.validation.constraints.Size;
import java.util.Objects;

@MappedEntity("CATEGORIES")
public class Category {

    @GeneratedValue(GeneratedValue.Type.IDENTITY)
    @Id
    @Nullable
    private final Long categoryId;

    @Size(max = 30)
    private final String name;

    public Category(Long categoryId, String name) {
        this.categoryId = categoryId;
        this.name = name;
    }

    @Nullable
    public Long getCategoryId() {
        return categoryId;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Category category = (Category) o;
        return name.equals(category.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
