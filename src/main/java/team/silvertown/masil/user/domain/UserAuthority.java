package team.silvertown.masil.user.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_authorities")
@NoArgsConstructor
@Getter
public class UserAuthority {

    private static final String ROLE_PREFIX = "ROLE_";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @Column(name = "authority", columnDefinition = "VARCHAR(20)")
    @Enumerated(EnumType.STRING)
    private Authority authority;

    @Builder
    private UserAuthority(User user, Authority authority) {
        this.user = user;
        this.authority = authority;
    }

    public String getRole() {
        return ROLE_PREFIX + authority.getAuthority();
    }

}
