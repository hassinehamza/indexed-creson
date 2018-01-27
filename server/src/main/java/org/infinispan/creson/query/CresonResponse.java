package org.infinispan.creson.query;

import org.infinispan.creson.Obj;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.List;

public class CresonResponse implements Serializable {
    private int numResults;
    private List<Object> results;

    public CresonResponse(int numResults, List<Object> results) {
        this.numResults = numResults;
        this.results = results;
    }


    public int getNumResults() {
        return numResults;
    }

    public void setNumResults(int numResults) {
        this.numResults = numResults;
    }

    public List<Object> getResults() {
        return results;
    }

    public void setResults(List<Object> results) {
        this.results = results;
    }

}
