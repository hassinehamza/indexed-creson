package org.infinispan.creson;


public class CresonObject {


   @Shared
    public Obj obj;

    public CresonObject(Obj obj) {
        this.obj = obj;

    }

    public void changeX(int x){
        obj.setX(x);
    }

}
