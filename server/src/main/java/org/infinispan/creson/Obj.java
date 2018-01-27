package org.infinispan.creson;

import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;

import java.io.Serializable;

@Indexed
public class Obj implements Serializable{

    @Field
    int x ;
    public Obj(int x) {
        this.x = x;
    }

    @Override
    public String toString() {
        return "I have " + x;
    }
}
