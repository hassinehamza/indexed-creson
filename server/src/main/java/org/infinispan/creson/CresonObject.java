package org.infinispan.creson;


public class CresonObject {

    @Shared
    public Obj o;

    public CresonObject(Obj o) {
        this.o = o ;
    }

    public void changeX(int newX){
        this.o.setX(newX);
    }

}
