package org.infinispan.creson.server;

import org.infinispan.AdvancedCache;
import org.infinispan.protostream.ProtobufUtil;
import org.infinispan.query.Search;
import org.infinispan.query.dsl.Query;
import org.infinispan.query.dsl.QueryFactory;
import org.infinispan.query.dsl.impl.BaseQuery;
import org.infinispan.query.remote.client.QueryRequest;

import org.infinispan.server.core.QueryFacade;

import static org.infinispan.creson.Factory.CRESON_CACHE_NAME;

public class QueryFacadeImpl implements QueryFacade {

    @Override
    public byte[] query(AdvancedCache<byte[], byte[]> cache, byte[] query) {

//        BaseRemoteQueryEngine queryEngine = SecurityActions.getCacheComponentRegistry(cache).getComponent(BaseRemoteQueryEngine.class);
//        if (queryEngine == null) {
//            throw log.queryingNotEnabled(cache.getName());
//        }
//
//        try {
//            // decode the query request object
//            QueryRequest request = ProtobufUtil.fromByteArray(queryEngine.getSerializationContext(), query, 0, query.length, QueryRequest.class);
//
//            long startOffset = request.getStartOffset() == null ? -1 : request.getStartOffset();
//            int maxResults = request.getMaxResults() == null ? -1 : request.getMaxResults();
//
//            // create the query
//            BaseQuery q = queryEngine.makeQuery(request.getQueryString(), request.getNamedParametersMap(), startOffset, maxResults);
//        cm.getCache(CRESON_CACHE_NAME).put('a', new org.example.Room(123));
//        QueryFactory factory = Search.getQueryFactory(cm.getCache(CRESON_CACHE_NAME));
//        Query q = factory.from(org.example.Room.class).build();
//        System.out.println(q.list());
        System.out.println("Hello world!");
        return new byte[0];
    }

}
