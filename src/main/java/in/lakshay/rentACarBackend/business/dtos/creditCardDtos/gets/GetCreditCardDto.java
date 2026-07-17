package in.lakshay.rentACarBackend.business.dtos.creditCardDtos.gets;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// dto for credit card info
// SECURITY ISSUE: we're returning full card details!!
// TODO: fix this before prod - mask numbers, remove CVV!!
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetCreditCardDto {

    // card stuff
    private int creditCardId;  // pk

    private String cardNumber;  // should mask this!! (****1234)
    private String cardOwner;   // name on card
    private String cardExpirationDate;  // MM/YY format
    private int customerId;     // fk to customer

    // need to add validation before this goes live
}
