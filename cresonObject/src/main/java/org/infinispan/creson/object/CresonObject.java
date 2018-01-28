package org.infinispan.creson.object;


import java.util.ArrayList;
import java.util.List;

public class CresonObject {


   // @Shared
    public Obj obj;

    public CresonObject(Obj obj) {
        this.obj = obj;

    }

    public void changeX(int x){
        obj.setX(x);
    }

}
