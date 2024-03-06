CREATE TABLE mates
(
    id                     BIGINT AUTO_INCREMENT,
    author_id              BIGINT       NOT NULL,
    post_id                BIGINT       NOT NULL,
    address_depth1         VARCHAR(20)  NOT NULL,
    address_depth2         VARCHAR(20)  NOT NULL,
    address_depth3         VARCHAR(20)  NOT NULL,
    address_depth4         VARCHAR(20)  NOT NULL,
    title                  VARCHAR(30)  NOT NULL,
    content                TEXT         NOT NULL,
    gathering_place_point  POINT        NOT NULL,
    gathering_place_detail VARCHAR(50)  NOT NULL,
    gathering_at           TIMESTAMP(6) NOT NULL,
    capacity               INTEGER      NOT NULL,
    created_at             TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at             TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),

    CONSTRAINT mates_pk PRIMARY KEY (id),
    CONSTRAINT mates_users_fk FOREIGN KEY (author_id) REFERENCES users (id),
    CONSTRAINT mates_posts_fk FOREIGN KEY (post_id) REFERENCES posts (id)
);

CREATE TABLE mate_participants
(
    id         BIGINT AUTO_INCREMENT,
    user_id    BIGINT       NOT NULL,
    mate_id    BIGINT       NOT NULL,
    status     VARCHAR(12)  NOT NULL DEFAULT 'REQUESTED',
    created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),

    CONSTRAINT mate_participants_pk PRIMARY KEY (id),
    CONSTRAINT mate_participants_users_fk FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT mate_participants_mates_fk FOREIGN KEY (mate_id) REFERENCES mates (id)
);
