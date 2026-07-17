-- PCI-DSS: card verification data must never be stored post-authorization
ALTER TABLE credit_cards DROP COLUMN card_cvv;
