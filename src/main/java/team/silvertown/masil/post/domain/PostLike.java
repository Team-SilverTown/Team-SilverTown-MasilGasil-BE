package team.silvertown.masil.post.domain;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import team.silvertown.masil.common.BaseEntity;

@Entity
@Table(name = "post_likes")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class PostLike extends BaseEntity {

    @EmbeddedId
    private PostLikeId id;

    @Column(name = "is_like", nullable = false)
    private boolean isLike;

    @Transient
    private boolean isCreated = false;

    public void setIsLike(Boolean isLike) {
        this.isLike = Objects.nonNull(isLike) && isLike;
    }

    public boolean hasChange(boolean newState) {
        return isCreated || isLike != newState;
    }

    public void setCreatedTrue() {
        isCreated = true;
    }

}
