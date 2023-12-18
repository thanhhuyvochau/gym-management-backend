package spring.project.base.util.specification;

import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class EntitySpecificationBuilder<T> {
    public abstract EntitySpecificationBuilder getBuilder();


    private List<Specification<T>> specifications = new ArrayList<>();

    public Specification<T> build() {
        return specifications.stream()
                .filter(Objects::nonNull)
                .reduce(all(), Specification::and);
    }

    private Specification<T> all() {
        return Specification.where(null);
    }

}
