package spring.project.base.util.message;

import spring.project.base.entity.Account;
import spring.project.base.entity.Notification;
import spring.project.base.entity.Notifier;

import java.util.Objects;
import java.util.Optional;

public class NotificationUtil {
    public static Notifier findNotifier(Notification notification, Account account) {
        Optional<Notifier> optionalNotifier = notification.getNotifiers().stream().filter(notifier -> Objects.equals(notifier.getUser().getId(), account.getId())).findFirst();
        return optionalNotifier.orElse(null);
    }
}
