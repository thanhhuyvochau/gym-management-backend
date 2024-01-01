package spring.project.base.payment;


import spring.project.base.entity.AppPlan;

import java.io.UnsupportedEncodingException;

public interface PaymentGateway<T> {
    PaymentResponse<T> pay(AppPlan paidAppPlan) throws UnsupportedEncodingException;
}
