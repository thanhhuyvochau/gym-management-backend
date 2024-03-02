package spring.project.base.util.mapper;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import spring.project.base.dto.request.UpdateEquipmentRequest;
import spring.project.base.dto.response.EquipmentResponse;
import spring.project.base.dto.response.RoleResponse;
import spring.project.base.dto.response.UserResponse;
import spring.project.base.dto.response.VnPayResponse;
import spring.project.base.entity.*;
import spring.project.base.payment.PaymentResponse;
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

    public static UserResponse convertUsertoUserResponse(Account account) {
        UserResponse userResponse = ObjectUtil.copyproperties(account, new UserResponse(), UserResponse.class, true);
        RoleResponse roleResponse = ConvertUtil.convertRoleToRoleDto(account.getRole());
        userResponse.setRoleResponse(roleResponse);
        return userResponse;
    }

    public static PaymentResponse<VnPayResponse> convertPaymentResponse(AppPlan appPlan, Transaction transaction) {
        PaymentResponse<VnPayResponse> paymentResponse = new PaymentResponse<VnPayResponse>();
        paymentResponse.setAppPlanId(appPlan.getId());
        paymentResponse.setTransactionStatus(transaction.getStatus());
        paymentResponse.setTransactionId(transaction.getId());
        paymentResponse.setDescription(appPlan.getName());
        paymentResponse.setAmount(appPlan.getPrice());
        return paymentResponse;
    }

    public static EquipmentResponse convertEquipmentToResponse(Equipment equipment) {
        return ObjectUtil.copyproperties(equipment, new EquipmentResponse(), EquipmentResponse.class, true);
    }

    public static Equipment convertEquipmentRequestToEntity(UpdateEquipmentRequest updateEquipmentRequest) {
        return ObjectUtil.copyproperties(updateEquipmentRequest, new Equipment(), Equipment.class, true);
    }
}