CREATE TABLE post_likes
(
    user_id    BIGINT       NOT NULL,
    post_id    BIGINT       NOT NULL,
    is_like    TINYINT(1)   NOT NULL,
    created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),

    CONSTRAINT post_likes_pk PRIMARY KEY (user_id, post_id),
    CONSTRAINT post_likes_users_fk FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT post_likes_posts_fk FOREIGN KEY (post_id) REFERENCES posts (id)
);
