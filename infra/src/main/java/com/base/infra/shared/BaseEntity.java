package com.base.infra.shared;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public abstract class BaseEntity {

    @Id
    private String id;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Version
    private Long version;

    @PrePersist
    protected void prePersist() {
        if (id == null) {
            id = UUID.randomUUID().toString();
        }
    }

    public void markAsUpdated() {
        this.updatedAt = LocalDateTime.now();
        if (this.version == null) {
            this.version = 0L;
        }
        this.version++;
    }

    public boolean isNew() {
        return version == null || version == 0L;
    }

    public boolean isModified() {
        return version != null && version > 0L;
    }

    public long getAgeInDays() {
        if (createdAt == null) return 0;
        return java.time.Duration.between(createdAt, LocalDateTime.now()).toDays();
    }

    public long getTimeSinceLastUpdateInMinutes() {
        if (updatedAt == null) return 0;
        return java.time.Duration.between(updatedAt, LocalDateTime.now()).toMinutes();
    }
}