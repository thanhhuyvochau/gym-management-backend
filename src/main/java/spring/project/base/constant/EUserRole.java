package spring.project.base.constant;

public enum EUserRole {
    ANONYMOUS("Ẩn danh", "anonymous", "ROLE_ANONYMOUS"),

    GYM_OWNER("Chủ phòng gym", "gym owner", "ROLE_GYM_OWNER"),

    ADMIN("Quản trị viên", "admin", "ROLE_ADMIN");

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