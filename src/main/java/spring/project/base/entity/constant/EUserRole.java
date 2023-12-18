package spring.project.base.entity.constant;

public enum EUserRole {
    ANONYMOUS("Ẩn danh", "anonymous", "ROLE_ANONYMOUS");

    private final String label;
    private final String name;
    private final String securityCode;

    EUserRole(String label, String name, String securityCode) {
        this.label = label;
        this.name = name;
        this.securityCode = securityCode;
    }

    public String getLabel() {
        return label;
    }

    public String getName() {
        return name;
    }

    public String getSecurityCode() {
        return securityCode;
    }
}