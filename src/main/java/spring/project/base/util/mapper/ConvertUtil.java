package spring.project.base.util.mapper;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import spring.project.base.entity.Account;
import spring.project.base.entity.Role;
import spring.project.base.dto.response.RoleResponse;
import spring.project.base.dto.response.UserResponse;
import spring.project.base.util.message.MessageUtil;

@Component
public class ConvertUtil {
    private static MessageUtil messageUtil;

    private static String successIcon;

    private static String failIcon;


    @Value("${icon.success}")
    public void setSuccessIconUrl(String url) {
        successIcon = url;
    }

    @Value("${icon.fail}")
    public void setFailIconUrl(String url) {
        failIcon = url;
    }


    public static RoleResponse convertRoleToRoleDto(Role role) {
        RoleResponse roleResponse = ObjectUtil.copyProperties(role, new RoleResponse(), RoleResponse.class);
        roleResponse.setName(role.getCode().getName());
        return roleResponse;
    }

    public static UserResponse convertUsertoUserDto(Account account) {
        return ObjectUtil.copyProperties(account, new UserResponse(), UserResponse.class, true);
    }
}