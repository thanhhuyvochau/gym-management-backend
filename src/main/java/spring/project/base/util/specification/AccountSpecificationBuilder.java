package spring.project.base.util.specification;

import org.springframework.data.jpa.domain.Specification;
import spring.project.base.constant.EAccountRole;
import spring.project.base.entity.Account;
import spring.project.base.entity.Account_;
import spring.project.base.entity.Role;
import spring.project.base.entity.Role_;
import spring.project.base.util.formater.StringUtil;
import spring.project.base.util.other.SpecificationUtil;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class AccountSpecificationBuilder {
    public static AccountSpecificationBuilder specificationBuilder() {
        return new AccountSpecificationBuilder();
    }

    private final List<Specification<Account>> specifications = new ArrayList<>();

    public AccountSpecificationBuilder searchByEmail(String email) {
        if (StringUtil.isNullOrEmpty(email)) {
            return this;
        }
        specifications.add(((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(Account_.EMAIL), email)));
        return this;
    }

    public AccountSpecificationBuilder searchByName(String name) {
        if (StringUtil.isNullOrEmpty(name)) {
            return this;
        }
        specifications.add((root, query, criteriaBuilder) -> {
            String search = name.replaceAll("\\s\\s+", " ").trim();
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("%").append(search).append("%");
            return criteriaBuilder.like(root.get(Account_.FULL_NAME), stringBuilder.toString());
        });
        return this;
    }

    public AccountSpecificationBuilder queryLike(String q) {
        if (StringUtil.isNullOrEmpty(q)) {
            return this;
        }
        specifications.add((root, query, criteriaBuilder) -> {
            Expression<String> name = root.get(Account_.FULL_NAME);
            Expression<String> email = root.get(Account_.EMAIL);
            Expression<String> stringExpression = SpecificationUtil.concat(criteriaBuilder, " ", name, email);
            String search = q.replaceAll("\\s\\s+", " ").trim();
            return criteriaBuilder.like(stringExpression, '%' + search + '%');
        });
        return this;
    }

    public AccountSpecificationBuilder searchByAddress(String address) {
        if (StringUtil.isNullOrEmpty(address)) {
            return this;
        }
        specifications.add((root, query, criteriaBuilder) -> {
            String search = address.replaceAll("\\s\\s+", " ").trim();
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("%").append(search).append("%");
            return criteriaBuilder.like(root.get(Account_.ADDRESS), stringBuilder.toString());
        });
        return this;
    }

    public AccountSpecificationBuilder searchByPhone(String phone) {
        if (StringUtil.isNullOrEmpty(phone)) {
            return this;
        }
        specifications.add((root, query, criteriaBuilder) -> {
            String search = phone.replaceAll("\\s\\s+", " ").trim();
            return criteriaBuilder.like(root.get(Account_.PHONE), "%" + search + "%");
        });
        return this;
    }

    public AccountSpecificationBuilder hasRole(EAccountRole role) {
        specifications.add((root, query, criteriaBuilder) -> {
            Join<Account, Role> userRoleJoin = root.join(Account_.ROLE);
            List<EAccountRole> roles = new ArrayList<>();
            boolean isValidRoleQuery = role != null
                    && (role.equals(EAccountRole.ADMIN) || role.equals(EAccountRole.GYM_OWNER));
            if (isValidRoleQuery) {
                roles.add(role);
            } else {
                roles = Arrays.asList(EAccountRole.ADMIN, EAccountRole.GYM_OWNER);
            }
            return criteriaBuilder.and(userRoleJoin.get(Role_.CODE).in(roles));
        });
        return this;
    }

    public AccountSpecificationBuilder isVerified(Boolean isVerified) {
        if (isVerified == null) {
            return this;
        }
        specifications.add((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(Account_.VERIFIED),
                isVerified));
        return this;
    }

    public Specification<Account> build() {
        return specifications.stream()
                .filter(Objects::nonNull)
                .reduce(all(), Specification::and);
    }

    private Specification<Account> all() {
        return Specification.where(null);
    }
}


