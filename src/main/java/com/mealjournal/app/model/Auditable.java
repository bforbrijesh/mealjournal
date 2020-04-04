package com.mealjournal.app.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@JsonIdentityInfo(
        generator = ObjectIdGenerators.StringIdGenerator.class,
        property = "id"
)
public abstract class Auditable {

    @Id
    @GeneratedValue(generator = "sequence", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "generator", allocationSize = 10)
    @Getter
    @Setter
    private Long Id;

    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    @Column(nullable = false, updatable = false) // why not @NotBlank @NotUpdatable?
    @Getter
    @Setter
    private Date createdAt = new Date();

    @Temporal(TemporalType.TIMESTAMP)
    @LastModifiedDate
    @Column(nullable = false, updatable = false) // why not @NotBlank @NotUpdatable?
    @Getter
    @Setter
    private Date lastModifiedAt = new Date();

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Auditable) {
            return ((Auditable) obj).getId().equals(getId());
        }
        return super.equals(obj); // better than returning false
    }
}