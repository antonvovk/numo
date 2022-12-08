package com.numo.server.db.entities;

import com.numo.proto.Gender;
import com.numo.proto.PhysicalFitness;
import lombok.*;
import org.hibernate.Hibernate;
import org.hibernate.annotations.NaturalId;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "app_user", indexes = @Index(name = "IDX_EMAIL", columnList = "email", unique = true))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@ToString
public class User {

    @Id
    private String id;

    @NaturalId
    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "name", nullable = false)
    @Builder.Default
    private String name = "Unverified User";

    @Column(name = "gender", nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Gender gender = Gender.GENDER_UNSPECIFIED;

    @Column(name = "age")
    private Integer age;

    @Column(name = "weight")
    private Integer weight;

    @Column(name = "physical_fitness", nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private PhysicalFitness physicalFitness = PhysicalFitness.PHYSICAL_FITNESS_UNSPECIFIED;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        User user = (User) o;
        return email != null && Objects.equals(email, user.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email);
    }
}
