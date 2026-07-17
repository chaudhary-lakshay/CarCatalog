package in.lakshay.rentACarBackend.business.dtos.creditCardDtos.lists;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// dto for listing credit cards
// used in getAll() responses
// pretty much identical to GetCreditCardDto - could prob refactor
@Data // lombok annotations r nice
@AllArgsConstructor
@NoArgsConstructor
public class CreditCardListDto { // for lists of cards

    // same fields as GetCreditCardDto
    private int creditCardId;   // pk
    private String cardNumber;   // should mask in prod
    private String cardOwner;    // name on card
    private String cardExpirationDate;  // MM/YY
    private int customerId;      // fk to customer

    // TODO: maybe add a "masked" version of card number for display?

}
