package spring.project.base.director;

import org.springframework.stereotype.Component;
import spring.project.base.util.message.MessageUtil;

@Component
public class NotificationDirector {
    public static MessageUtil staticMessageUtil;

    public NotificationDirector(MessageUtil messageUtil) {
        staticMessageUtil = messageUtil;
    }

 /**
  *    public static Notification buildPaymentNotification(Order order, Transaction transaction) {
  *         String title = staticMessageUtil.getLocalMessage(NotificationConstant.TRANSACTION_PAYMENT_TITLE);
  *         String content = staticMessageUtil.getLocalMessage(NotificationConstant.TRANSACTION_PAYMENT_CONTENT);
  *         Map<String, String> parameters = new HashMap<>();
  *         parameters.put("totalPrice", transaction.getAmount().toString());
  *         content = TextUtil.format(content, parameters);
  *         Notification.NotificationBuilder builder = Notification.getBuilder();
  *         return builder
  *                 .viTitle(title)
  *                 .viContent(content)
  *                 .notifiers(order.getUser())
  *                 .type(ENotificationType.PERSONAL)
  *                 .entity(ENotificationEntity.TRANSACTION)
  *                 .entityId(transaction.getId())
  *                 .build();
  *     }
  *
  * */
}
