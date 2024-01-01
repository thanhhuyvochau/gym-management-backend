package spring.project.base.constant;

import lombok.Getter;

@Getter
public enum EAccountRole {
    ANONYMOUS("Ẩn danh", "anonymous", "ROLE_ANONYMOUS"),

    GYM_OWNER("Chủ phòng gym", "gym owner", "ROLE_GYM_OWNER"),

    ADMIN("Admin", "admin", "ROLE_ADMIN"),

    MANAGER("Quản lý", "manager", "ROLE_MANAGER");

    private final String label;
    private final String name;
    private final String securityCode;

    EAccountRole(String label, String name, String securityCode) {
        this.label = label;
        this.name = name;
        this.securityCode = securityCode;
    }

}