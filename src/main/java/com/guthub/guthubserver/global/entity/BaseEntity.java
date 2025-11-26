package com.guthub.guthubserver.global.entity;

import com.github.f4b6a3.tsid.TsidCreator;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import lombok.Getter;

@Getter
@MappedSuperclass
public abstract class BaseEntity {

    @Id
    private Long id;

    @PrePersist
    private void generateId() {
        if (this.id == null) {
            this.id = TsidCreator.getTsid().toLong();
        }
    }
}