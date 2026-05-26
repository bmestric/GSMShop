package hr.bmestric.gsmshop.service;

import java.math.BigDecimal;

public interface PayPalService {

    String createOrder(BigDecimal amount, String returnUrl, String cancelUrl);

    String captureOrder(String orderId);
}
