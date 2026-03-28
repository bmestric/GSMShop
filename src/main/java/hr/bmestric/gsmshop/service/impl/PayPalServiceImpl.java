package hr.bmestric.gsmshop.service.impl;

import tools.jackson.databind.JsonNode;
import hr.bmestric.gsmshop.service.PayPalService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class PayPalServiceImpl implements PayPalService {

    private final RestClient restClient;
    private final String clientId;
    private final String clientSecret;

    public PayPalServiceImpl(@Value("${paypal.base-url}") String baseUrl,
                             @Value("${paypal.client-id}") String clientId,
                             @Value("${paypal.client-secret}") String clientSecret) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.restClient = RestClient.builder().baseUrl(baseUrl).build();
    }

    @Override
    public String createOrder(BigDecimal amount, String returnUrl, String cancelUrl) {
        String accessToken = getAccessToken();

        Map<String, Object> orderRequest = Map.of(
                "intent", "CAPTURE",
                "purchase_units", List.of(Map.of(
                        "amount", Map.of(
                                "currency_code", "EUR",
                                "value", amount.toPlainString()
                        )
                )),
                "application_context", Map.of(
                        "return_url", returnUrl,
                        "cancel_url", cancelUrl
                )
        );

        JsonNode response = restClient.post()
                .uri("/v2/checkout/orders")
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .body(orderRequest)
                .retrieve()
                .body(JsonNode.class);

        if (response != null) {
            for (JsonNode link : response.get("links")) {
                if ("approve".equals(link.get("rel").asText())) {
                    return link.get("href").asText();
                }
            }
        }
        throw new IllegalStateException("No approval URL returned from PayPal");
    }

    @Override
    public String captureOrder(String orderId) {
        String accessToken = getAccessToken();

        JsonNode response = restClient.post()
                .uri("/v2/checkout/orders/{orderId}/capture", orderId)
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(JsonNode.class);

        if (response != null && "COMPLETED".equals(response.get("status").asText())) {
            return response.get("id").asText();
        }
        throw new IllegalStateException("PayPal capture failed");
    }

    private String getAccessToken() {
        String credentials = Base64.getEncoder()
                .encodeToString((clientId + ":" + clientSecret).getBytes());

        JsonNode response = restClient.post()
                .uri("/v1/oauth2/token")
                .header("Authorization", "Basic " + credentials)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body("grant_type=client_credentials")
                .retrieve()
                .body(JsonNode.class);

        if (response != null) {
            return response.get("access_token").asText();
        }
        throw new IllegalStateException("Failed to obtain PayPal access token");
    }
}
