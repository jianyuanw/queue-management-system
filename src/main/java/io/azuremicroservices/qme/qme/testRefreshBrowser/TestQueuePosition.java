package io.azuremicroservices.qme.qme.testRefreshBrowser;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class TestQueuePosition {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private String queueNumber;
    private int position;

    public TestQueuePosition() {}

    public TestQueuePosition(String queueNumber, int position) {
        this.queueNumber = queueNumber;
        this.position = position;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getQueueNumber() {
        return queueNumber;
    }

    public void setQueueNumber(String queueNumber) {
        this.queueNumber = queueNumber;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
