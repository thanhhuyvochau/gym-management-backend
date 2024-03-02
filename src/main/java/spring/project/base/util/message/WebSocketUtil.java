package spring.project.base.util.message;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import spring.project.base.common.ApiResponse;
import spring.project.base.dto.response.ResponseMessage;
import spring.project.base.entity.Notification;

@Component
public class WebSocketUtil {

    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public WebSocketUtil(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public static final String BASE_TOPIC = "/topic/message";
    public static final String QUEUE_PRIVATE = "/queue/message";

    public void sendNotification(String topic, ResponseMessage message) {
        if (topic == null || topic.isEmpty()) {
            topic = BASE_TOPIC;
        }
        ApiResponse<ResponseMessage> apiResponse = ApiResponse.success(message);
        messagingTemplate.convertAndSend(topic, apiResponse);
    }

    public void sendPrivateNotification(Notification notification) {
        /**
         * sendPrivateNotification(receivedUser.getEmail(), responseMessage);
         * */
    }

    public void sendPrivateNotification(String userID, ResponseMessage message) {
        /**
         *         messagingTemplate.convertAndSend(QUEUE_PRIVATE + userID, apiResponse);
         *         ApiResponse<ResponseMessage> apiResponse = ApiResponse.success(message);
         * */

    }
}
