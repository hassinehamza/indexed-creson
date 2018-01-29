package org.infinispan.creson;



import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;


import javax.persistence.Entity;
import javax.persistence.Id;


@Entity
@Indexed
public class Obj{



    @Id
    @Field
    int x ;

    String name;

    public Obj() {}

    public Obj(String name, int x) {
        this.name = name;
        this.x = x;
    }

    public void setX(int x) {
        this.x = x;
    }
    public int getX() {
        return x;
    }
    @Override
    public String toString() {
        return name + ", x =" + x;
    }
}
