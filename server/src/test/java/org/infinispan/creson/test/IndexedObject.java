package org.infinispan.creson.test;

import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;


@Indexed
public class IndexedObject {

    @Field
    private int x;

    public IndexedObject(int x) {
        this.x = 0;
    }

}
