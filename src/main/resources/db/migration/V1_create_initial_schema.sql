CREATE TABLE IF NOT EXISTS `card_details`
(
    cvv         VARCHAR(255) NOT NULL,
    card_number VARCHAR(255),
    expiry_date VARCHAR(255),
    CONSTRAINT pk_carddetails PRIMARY KEY (cvv)
);
CREATE TABLE IF NOT EXISTS merchant
(
    merchant_id      BIGINT NOT NULL,
    api_key          VARCHAR(255),
    first_name       VARCHAR(255),
    last_name        VARCHAR(255),
    email            VARCHAR(255),
    phone_number     VARCHAR(255),
    merchant_balance INTEGER,
    CONSTRAINT pk_merchant PRIMARY KEY (merchant_id)
);
CREATE TABLE IF NOT EXISTS payer
(
    payer_id     BIGINT NOT NULL,
    first_name   VARCHAR(255),
    last_name    VARCHAR(255),
    email        VARCHAR(255),
    phone_number VARCHAR(255),
    CONSTRAINT pk_payer PRIMARY KEY (payer_id)
);

ALTER TABLE IF NOT EXISTS payer
    ADD CONSTRAINT uc_payer_email UNIQUE (email);
CREATE TABLE payment_method
(
    method_id   BIGINT NOT NULL,
    method_name VARCHAR(255),
    CONSTRAINT pk_paymentmethod PRIMARY KEY (method_id)
);
CREATE TABLE IF NOT EXISTS payments
(
    payment_id     BIGINT NOT NULL,
    payer_id       BIGINT NOT NULL,
    merchant_id    BIGINT NOT NULL,
    method_id      BIGINT,
    currency       VARCHAR(255),
    amount         INTEGER,
    transaction_id VARCHAR(255),
    reference_id   VARCHAR(255),
    status         VARCHAR(255),
    CONSTRAINT pk_payments PRIMARY KEY (payment_id)
);

ALTER TABLE payments
    ADD CONSTRAINT FK_PAYMENTS_ON_MERCHANT FOREIGN KEY (merchant_id) REFERENCES merchant (merchant_id);

ALTER TABLE payments
    ADD CONSTRAINT FK_PAYMENTS_ON_METHOD FOREIGN KEY (method_id) REFERENCES payment_method (method_id);

ALTER TABLE payments
    ADD CONSTRAINT FK_PAYMENTS_ON_PAYER FOREIGN KEY (payer_id) REFERENCES payer (payer_id);