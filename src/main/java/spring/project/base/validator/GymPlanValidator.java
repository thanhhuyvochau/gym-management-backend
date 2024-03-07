package spring.project.base.validator;

import spring.project.base.entity.Account;
import spring.project.base.entity.Equipment;
import spring.project.base.entity.GymPlan;

public class GymPlanValidator {
    public static boolean isGymOwnerOfGymPlan(Account account, GymPlan gymPlan) {
        return account.getId().equals(gymPlan.getGymOwner().getId());
    }
}
