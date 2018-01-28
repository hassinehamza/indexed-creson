package org.infinispan.creson.object;



import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;


import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;

@Indexed
@Entity
public class Obj implements Serializable{

    public int getX() {
        return x;
    }

    @Id
    @Field
    int x ;

    String name;

    public Obj(String name, int x) {
        this.name = name;
        this.x = x;
    }

    public void setX(int x) {
        this.x = x;
    }

    @Override
    public String toString() {
        return name + ", x =" + x;
    }
}
