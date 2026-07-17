package in.lakshay.rentACarBackend.business.adapters.posAdapters;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import in.lakshay.rentACarBackend.core.utilities.exceptions.businessExceptions.PosServiceExceptions.MakePaymentFailedException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.Map;

// Stripe-backed PosService. Confirms a PaymentIntent synchronously so the
// existing @Transactional payment flow keeps working unchanged.
// Raw card numbers are never sent to Stripe from the server: known Stripe
// test PANs map to test payment-method tokens, anything else is rejected.
// Production card entry must tokenize client-side (Stripe.js/Elements) and
// confirm asynchronously via webhook - tracked in issue #2.
@Service
@Primary
public class StripePosAdapter implements PosService {

    // ponytail: test-mode PAN->token map; replaced by client-side tokenization when a frontend exists
    private static final Map<String, String> TEST_CARD_TOKENS = Map.of(
            "4242424242424242", "pm_card_visa",
            "5555555555554444", "pm_card_mastercard",
            "4000000000000002", "pm_card_visa_chargeDeclined"
    );

    private final String currency;

    public StripePosAdapter(@Value("${stripe.secret-key:}") String secretKey,
                            @Value("${stripe.currency:usd}") String currency) {
        Stripe.apiKey = secretKey;
        this.currency = currency;
    }

    @Override
    public boolean payment(String cardNumber, String cardOwner, String cardCvv, String cardExpirationDate, double totalPrice) throws MakePaymentFailedException {
        if (Stripe.apiKey == null || Stripe.apiKey.isBlank()) {
            throw new MakePaymentFailedException("Stripe is not configured - set the STRIPE_SECRET_KEY environment variable");
        }
        String paymentMethod = TEST_CARD_TOKENS.get(cardNumber);
        if (paymentMethod == null) {
            throw new MakePaymentFailedException("card cannot be charged server-side - use a Stripe test card (e.g. 4242424242424242) or client-side tokenization");
        }
        try {
            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setAmount(Math.round(totalPrice * 100))
                    .setCurrency(currency)
                    .setPaymentMethod(paymentMethod)
                    .setDescription("CarCatalog rental payment - " + cardOwner)
                    .setConfirm(true)
                    .setAutomaticPaymentMethods(
                            PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                    .setEnabled(true)
                                    .setAllowRedirects(PaymentIntentCreateParams.AutomaticPaymentMethods.AllowRedirects.NEVER)
                                    .build())
                    .build();
            PaymentIntent intent = PaymentIntent.create(params);
            if (!"succeeded".equals(intent.getStatus())) {
                throw new MakePaymentFailedException("Stripe payment not completed, status: " + intent.getStatus());
            }
            return true;
        } catch (StripeException e) {
            throw new MakePaymentFailedException("Stripe payment failed: " + e.getMessage());
        }
    }
}
