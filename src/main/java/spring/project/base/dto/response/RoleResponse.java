package spring.project.base.dto.response;


import spring.project.base.constant.EAccountRole;

public class RoleResponse {

    private Long id;
    private String name;

    private EAccountRole code;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public EAccountRole getCode() {
        return code;
    }

    public void setCode(EAccountRole code) {
        this.code = code;
    }
}
