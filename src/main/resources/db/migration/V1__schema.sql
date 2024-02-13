CREATE TABLE users
(
    id                       BIGINT AUTO_INCREMENT,
    nickname                 VARCHAR(20) UNIQUE,
    sex                      CHAR(8),
    birth_date               DATE,
    height                   INTEGER,
    weight                   INTEGER,
    exercise_intensity       VARCHAR(15),
    total_distance           INTEGER,
    total_count              INTEGER,
    is_public                TINYINT(1) DEFAULT 1,
    is_allowing_notification TINYINT(1) DEFAULT 1,
    provider                 VARCHAR(20),
    social_id                VARCHAR(50),
    created_at               TIMESTAMP(6),
    updated_at               TIMESTAMP(6),

    CONSTRAINT user_pk PRIMARY KEY (id)
);

CREATE TABLE user_authorities
(
    id        BIGINT AUTO_INCREMENT,
    user_id   BIGINT,
    authority VARCHAR(20),

    CONSTRAINT user_authorities_pk PRIMARY KEY (id),
    CONSTRAINT user_id_fk FOREIGN KEY (user_id) REFERENCES users (id)
)
