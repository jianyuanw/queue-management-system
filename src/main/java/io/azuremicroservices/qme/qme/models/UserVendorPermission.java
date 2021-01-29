package io.azuremicroservices.qme.qme.models;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.io.Serializable;

@Entity
@NoArgsConstructor
@Data
public class UserVendorPermission implements Serializable {
    @Id
    @ManyToOne
    private User user;

    @Id
    @ManyToOne
    private Vendor vendor;
}
