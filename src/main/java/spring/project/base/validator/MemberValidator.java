package spring.project.base.validator;

import spring.project.base.entity.Account;
import spring.project.base.entity.Member;

public class MemberValidator {
    public static boolean isGymOwnerOfMember(Account account, Member member) {
        return account.getId().equals(member.getGymOwner().getId());
    }
}
