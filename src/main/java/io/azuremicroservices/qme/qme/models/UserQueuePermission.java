package io.azuremicroservices.qme.qme.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString.Exclude;

@Entity
@NoArgsConstructor
@Data
public class UserQueuePermission {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
	@ManyToOne
	@Exclude
    private Queue queue;
    
	@ManyToOne
	@Exclude
    private User user;
    
    public UserQueuePermission(User user, Queue queue) {
    	this.user = user;
    	this.queue = queue;
    }
}