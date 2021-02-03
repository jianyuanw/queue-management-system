package io.azuremicroservices.qme.qme.models;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Data
@Table
public class UserBranchPermission implements Serializable {
    @Id
    @ManyToOne
    private User user;

    @Id
    @ManyToOne
    private Branch branch;
}
