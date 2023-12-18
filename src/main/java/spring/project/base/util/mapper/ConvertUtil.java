package spring.project.base.util.mapper;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import spring.project.base.entity.Account;
import spring.project.base.entity.Role;
import spring.project.base.entity.dto.RoleDto;
import spring.project.base.entity.dto.UserDto;
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



    public static RoleDto convertRoleToRoleDto(Role role) {
        RoleDto roleDto = ObjectUtil.copyProperties(role, new RoleDto(), RoleDto.class);
        roleDto.setName(role.getCode().getName());
        return roleDto;
    }
    public static UserDto convertUsertoUserDto(Account account) {
        return new UserDto();
    }
}