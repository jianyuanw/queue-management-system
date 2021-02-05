package io.azuremicroservices.qme.qme.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserQueueNumberDto {
    private User user;
    private String queueNumber;
    private String fullname;

}
