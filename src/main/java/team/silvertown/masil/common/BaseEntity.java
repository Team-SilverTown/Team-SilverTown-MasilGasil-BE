package team.silvertown.masil.common;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import java.time.OffsetDateTime;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.TimeZoneStorage;
import org.hibernate.annotations.TimeZoneStorageType;
import org.hibernate.annotations.UpdateTimestamp;

@MappedSuperclass
@Getter
public class BaseEntity {

    @Column(
        name = "created_at", nullable = false,
        columnDefinition = "TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6)"
    )
    @CreationTimestamp
    @TimeZoneStorage(TimeZoneStorageType.NORMALIZE)
    private OffsetDateTime createdAt;

    @Column(
        name = "updated_at", nullable = false,
        columnDefinition = "TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6)"
    )
    @UpdateTimestamp
    @TimeZoneStorage(TimeZoneStorageType.NORMALIZE)
    private OffsetDateTime updatedAt;

}
