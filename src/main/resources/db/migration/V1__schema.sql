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
    is_public                TINYINT(1)            DEFAULT 1,
    is_allowing_notification TINYINT(1)            DEFAULT 1,
    provider                 VARCHAR(20),
    social_id                VARCHAR(50),
    created_at               TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at               TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),

    CONSTRAINT users_pk PRIMARY KEY (id)
);

CREATE TABLE user_authorities
(
    id        BIGINT AUTO_INCREMENT,
    user_id   BIGINT,
    authority VARCHAR(20),

    CONSTRAINT user_authorities_pk PRIMARY KEY (id),
    CONSTRAINT user_authorities_users_fk FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE masils
(
    id             BIGINT AUTO_INCREMENT,
    user_id        BIGINT        NOT NULL,
    post_id        BIGINT        NULL,
    address_depth1 VARCHAR(20)   NOT NULL,
    address_depth2 VARCHAR(20),
    address_depth3 VARCHAR(20)   NOT NULL,
    path           LINESTRING    NOT NULL,
    title          VARCHAR(30)   NOT NULL,
    content        TEXT          NULL,
    thumbnail_url  VARCHAR(1024) NULL,
    distance       INTEGER       NOT NULL,
    total_time     INTEGER       NOT NULL,
    started_at     TIMESTAMP(6)  NOT NULL,
    created_at     TIMESTAMP(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at     TIMESTAMP(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),

    CONSTRAINT masils_pk PRIMARY KEY (id),
    CONSTRAINT masils_users_fk FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE masil_pins
(
    id            BIGINT AUTO_INCREMENT,
    masil_id      BIGINT        NOT NULL,
    user_id       BIGINT        NULL,
    point         POINT         NOT NULL,
    content       TEXT          NULL,
    thumbnail_url VARCHAR(1024) NULL,
    created_at    TIMESTAMP(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at    TIMESTAMP(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),

    CONSTRAINT masil_pins_pk PRIMARY KEY (id),
    CONSTRAINT masil_pins_masils_fk FOREIGN KEY (masil_id) REFERENCES masils (id)
);

CREATE TABLE posts
(
    id             BIGINT AUTO_INCREMENT,
    user_id        BIGINT        NOT NULL,
    address_depth1 VARCHAR(20)   NOT NULL,
    address_depth2 VARCHAR(20)   NOT NULL,
    address_depth3 VARCHAR(20)   NOT NULL,
    path           LINESTRING    NOT NULL,
    title          VARCHAR(30)   NOT NULL,
    content        TEXT          NULL,
    thumbnail_url  VARCHAR(1024) NULL,
    distance       INTEGER       NOT NULL,
    total_time     INTEGER       NOT NULL,
    is_public      TINYINT(1)    NOT NULL DEFAULT 1,
    view_count     INTEGER       NOT NULL DEFAULT 0,
    like_count     INTEGER       NOT NULL DEFAULT 0,
    created_at     TIMESTAMP(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at     TIMESTAMP(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),

    CONSTRAINT posts_pk PRIMARY KEY (id)
);

CREATE TABLE post_pins
(
    id            BIGINT AUTO_INCREMENT,
    post_id       BIGINT        NOT NULL,
    user_id       BIGINT        NULL,
    point         POINT         NOT NULL,
    content       TEXT          NULL,
    thumbnail_url VARCHAR(1024) NULL,
    created_at    TIMESTAMP(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at    TIMESTAMP(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),

    CONSTRAINT post_pins_pk PRIMARY KEY (id),
    CONSTRAINT post_pins_posts_fk FOREIGN KEY (post_id) REFERENCES posts (id)
);
