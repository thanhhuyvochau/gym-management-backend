package spring.project.base.entity;

import javax.persistence.*;

@Entity
@Table(name = "verification")
public class Verification extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "code")
    private String code;
    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.REFRESH, CascadeType.PERSIST})
    @JoinColumn(name = "user_id")
    private Account account;
    @Column(name = "is_used")
    private boolean isUsed = false;

    public Long getId() {
        return id;
    }

    private void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    private void setCode(String code) {
        this.code = code;
    }

    public Account getUser() {
        return account;
    }

    private void setUser(Account account) {
        this.account = account;
    }

    public boolean getIsUsed() {
        return isUsed;
    }

    public void setIsUsed(boolean used) {
        isUsed = used;
    }

    public static class Builder {
        private String code;
        private Account account;
        private Verification verification;

        public static Builder getBuilder() {
            return new Builder();
        }

        public Builder withCode(String code) {
            this.code = code;
            return this;
        }

        public Builder withUser(Account account) {
            this.account = account;
            return this;
        }

        public Builder build() {
            verification = new Verification();
            verification.setCode(code);
            verification.setUser(account);
            return this;
        }

        public Verification getObject() {
            if (verification == null) {
                throw new RuntimeException("Cannot get object before build!");
            }
            return verification;
        }

    }
}
