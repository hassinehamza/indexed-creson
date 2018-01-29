package org.infinispan.creson.query;

import org.infinispan.AdvancedCache;
import org.infinispan.creson.server.Marshalling;
import org.infinispan.query.Search;
import org.infinispan.query.dsl.Query;
import org.infinispan.query.dsl.QueryFactory;

import java.util.List;

public class QueryFacadeImpl implements org.infinispan.server.core.QueryFacade{
    @Override
    public byte[] query(AdvancedCache<byte[], byte[]> cache, byte[] query) {
        System.out.println(cache.keySet());
        System.out.println(cache.values());
        CresonRequest request = (CresonRequest) Marshalling.unmarshall(query);
        QueryFactory qf =  Search.getQueryFactory(cache);
        Query q = qf.create(request.getQueryString());
        List<Object> list= q.list();
        CresonResponse response = new CresonResponse(list.size(), list);
        return Marshalling.marshall(response);
    }
}
