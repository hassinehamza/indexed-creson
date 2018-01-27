package org.infinispan.creson.search;

import org.infinispan.client.hotrod.impl.RemoteCacheImpl;
import org.infinispan.client.hotrod.impl.operations.OperationsFactory;
import org.infinispan.creson.Obj;
import org.infinispan.creson.query.CresonResponse;
import org.infinispan.creson.server.Marshalling;
import org.infinispan.query.dsl.Query;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RemoteQuery implements Query{

    protected RemoteCacheImpl cache;
    protected String jpqlString;
    protected long startOffset; //FIXME can this really be long or it has to be int due to limitations in query module?
    protected InetSocketAddress location;
    protected List results;
    protected int numResults;
    protected int maxResults;

    public RemoteQuery(RemoteCacheImpl cache, String jpqlString, long startOffset, int maxResults) {
        this.cache = cache;
        this.jpqlString = jpqlString;
        this.startOffset = startOffset;
        this.maxResults = maxResults;
    }

    @Override
    public List<Object> list() {
        List<Object> results ;
        QueryOperation op = ((OperationsFactory)cache.getOperationsFactory()).newCresonQueryOperation(this);
        CresonResponse response = op.execute();
        return response.getResults();
    }

    @Override
    public int getResultSize() {
        return 0;
    }

    @Override
    public Query startOffset(long startOffset) {
        return null;
    }

    @Override
    public Query maxResults(int maxResults) {
        return null;
    }

    @Override
    public Map<String, Object> getParameters() {
        return null;
    }

    @Override
    public Query setParameter(String paramName, Object paramValue) {
        return null;
    }

    @Override
    public Query setParameters(Map<String, Object> paramValues) {
        return null;
    }
}
