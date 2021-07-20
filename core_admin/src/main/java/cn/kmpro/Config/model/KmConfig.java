package cn.kmpro.Config.model;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "km_config", schema = "kmtest", catalog = "")
public class KmConfig {

    @Id
    private String field;
    private String vlaue;

    @Basic
    @Column(name = "field", nullable = true, length = 255)
    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    @Basic
    @Column(name = "vlaue", nullable = true, length = 255)
    public String getVlaue() {
        return vlaue;
    }

    public void setVlaue(String vlaue) {
        this.vlaue = vlaue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        KmConfig kmConfig = (KmConfig) o;
        return Objects.equals(field, kmConfig.field) && Objects.equals(vlaue, kmConfig.vlaue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(field, vlaue);
    }
}
