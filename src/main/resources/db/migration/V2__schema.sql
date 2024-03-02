CREATE TABLE user_agreements
(
    id                             BIGINT AUTO_INCREMENT,
    user_id                        BIGINT       NOT NULL,
    is_allowing_marketing          TINYINT      NOT NULL,
    is_personal_info_consented     TINYINT      NOT NULL,
    is_location_info_consented     TINYINT      NOT NULL,
    is_under_age_consent_confirmed TIMESTAMP(6),
    created_at                     TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at                     TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),

    CONSTRAINT user_agreements_pk PRIMARY KEY (id),
    CONSTRAINT user_agreements_users_fk FOREIGN KEY (user_id) REFERENCES users (id)
)
