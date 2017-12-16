package org.juel.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import javax.persistence.OneToOne;
import javax.persistence.Id;

@Entity
@Table(name = "goal_serial", schema = "juel")
public class GoalMeta {

    @Id
    private int id;

    @JoinColumn(name = "sign")
    @OneToOne
    private SerialMeta sign;

    @Column(name = "enabled")
    private boolean isEnabled;

    @Column(name = "priority")
    private int priority;


    public int getPriority() {
        return priority;
    }

    public GoalMeta setPriority(int priority) {
        this.priority = priority;
        return this;
    }

    public SerialMeta getSign() {
        return sign;
    }

    public GoalMeta setSign(SerialMeta sign) {
        this.sign = sign;
        return this;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public GoalMeta setEnabled(boolean enabled) {
        isEnabled = enabled;
        return this;
    }

    @Override
    public String toString() {
        return "GoalMeta{" +
                "id=" + id +
                ", sign=" + sign +
                ", isEnabled=" + isEnabled +
                ", priority=" + priority +
                '}';
    }
}
