package org.juel.model;


import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "predictive_serial", schema = "juel")
public class PredictiveModel {

    @Id
    @GeneratedValue
    private Long id;

    @OneToOne
    @JoinColumn(name = "sign")
    private SerialMeta sign;

    @Column(name = "model")
    private String serializedModel;

    @Column(name = "score")
    private Double score;

    @Column(name = "date_created")
    private LocalDateTime date;

    public Long getId() {
        return id;
    }

    public PredictiveModel setId(Long id) {
        this.id = id;
        return this;
    }

    public SerialMeta getSign() {
        return sign;
    }

    public PredictiveModel setSign(SerialMeta sign) {
        this.sign = sign;
        return this;
    }

    public String getSerializedModel() {
        return serializedModel;
    }

    public PredictiveModel setSerializedModel(String serializedModel) {
        this.serializedModel = serializedModel;
        return this;
    }

    public Double getScore() {
        return score;
    }

    public PredictiveModel setScore(Double score) {
        this.score = score;
        return this;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public PredictiveModel setDate(LocalDateTime date) {
        this.date = date;
        return this;
    }

    @Override
    public String toString() {
        return "PredictiveModel{" +
                "id=" + id +
                ", sign=" + sign +
                ", serializedModel='" + serializedModel + '\'' +
                ", score=" + score +
                ", date=" + date +
                '}';
    }
}
