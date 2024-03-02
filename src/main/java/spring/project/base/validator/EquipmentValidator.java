package spring.project.base.validator;

import spring.project.base.entity.Account;
import spring.project.base.entity.Equipment;

import java.time.Instant;

public class EquipmentValidator {

    public static boolean isExpectedDateValid(Instant expectedDateFrom, Instant expectedDateTo) {
        Instant currentDate = Instant.now();
        return expectedDateFrom.compareTo(currentDate) >= 0 &&
                expectedDateTo.compareTo(expectedDateFrom) > 0;
    }

    public static boolean isQuantityValid(int quantity) {
        return quantity > 0;
    }

    public static boolean isGymOwnerOfEquipment(Account account, Equipment equipment) {
        return account.getId().equals(equipment.getGymOwner().getId());
    }
}
