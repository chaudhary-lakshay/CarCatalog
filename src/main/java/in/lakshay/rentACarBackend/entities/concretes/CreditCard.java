package in.lakshay.rentACarBackend.entities.concretes;

// lombok stuff
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// hibernate stuff
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

// jpa stuff
import jakarta.persistence.*;

// entity for credit cards
// stores payment methods for customers
// should probably encrypt this in prod lol
@Data // lombok ftw
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "credit_cards") // db table name
public class CreditCard {

    // primary key
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // auto increment
    @Column(name = "credit_card_id")
    private int creditCardId;

    // card details - should be encrypted in prod
    @Column(name = "card_number", unique = true, length = 16, nullable = false) // must be unique
    private String cardNumber;  // 16 digits, no spaces

    // cardholder name
    @Column(name = "card_owner", nullable = false)
    private String cardOwner;  // as appears on card

    // expiration date - stored as string like MM/YY
    @Column(name = "card_expiration_date",nullable = false)
    private String cardExpirationDate;  // format MM/YY or MM/YYYY

    // many cards can belong to one customer
    @ManyToOne
    @Cascade(CascadeType.ALL)  // if customer deleted, delete their cards too
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;  // card owner

}