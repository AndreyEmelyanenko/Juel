package org.juel.model;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "supported_serial", schema = "juel")
public class SerialMeta {

    @Id
    @Column(name = "sign")
    private String sign;

    @Column(name = "enable")
    private Boolean enable;

    public String getSign() {
        return sign;
    }

    public SerialMeta setSign(String sign) {
        this.sign = sign;
        return this;
    }

    public Boolean getEnable() {
        return enable;
    }

    public SerialMeta setEnable(Boolean enable) {
        this.enable = enable;
        return this;
    }

    @Override
    public String toString() {
        return "SerialMeta{" +
                "sign='" + sign + '\'' +
                ", enable=" + enable +
                '}';
    }
}
