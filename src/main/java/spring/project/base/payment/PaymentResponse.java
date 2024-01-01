package spring.project.base.payment;

import lombok.Data;
import spring.project.base.payment.constant.ETransactionStatus;

import java.math.BigDecimal;

@Data
public class PaymentResponse<T> {
    private Long appPlanId;
    private Long transactionId;
    private ETransactionStatus transactionStatus;
    private BigDecimal amount = BigDecimal.ZERO;
    private String description;
    private T paymentData;
}
