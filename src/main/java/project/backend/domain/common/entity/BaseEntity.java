package project.backend.domain.common.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.time.LocalDate;
import java.time.LocalDateTime;

@MappedSuperclass // todo : 이거 왜 쓰는가
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class) // todo : 이건 또 왜 쓰는가
public abstract class BaseEntity { // todo : abstract 에 대해서 좀 더 알아보기

    @CreatedDate
    @Column(name = "created_date", updatable = false) // todo : 이건 머임
    private LocalDateTime createdDate;

    @LastModifiedDate
    @Column(name = "updated_date", updatable = false)
    private LocalDateTime updatedDate; //todo : 왜 private 인 것일까?

}
