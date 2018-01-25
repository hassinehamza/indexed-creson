package org.infinispan.creson.server;


import org.infinispan.AdvancedCache;

import org.infinispan.configuration.cache.Configuration;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.creson.utils.ConfigurationHelper;
import org.infinispan.factories.ComponentRegistry;
import org.infinispan.protostream.ProtobufUtil;

import org.infinispan.query.dsl.impl.BaseQuery;
import org.infinispan.query.remote.client.QueryRequest;


import org.infinispan.server.core.QueryFacade;

import java.io.IOException;


public class QueryFacadeImpl implements QueryFacade {

    @Override
    public byte[] query(AdvancedCache<byte[], byte[]> cache, byte[] query) {
        System.out.println("query" + query);
        System.out.println("cache" + cache);


        BaseRemoteQueryEngine queryEngine = SecurityActions.getCacheComponentRegistry(cache).getComponent(BaseRemoteQueryEngine.class);
        ComponentRegistry s =SecurityActions.getCacheComponentRegistry(cache); // for debug
        Configuration bl =  cache.getCacheConfiguration(); // for debug


        System.out.println("Engine " + queryEngine);
        if (queryEngine == null) {
          //throw log.queryingNotEnabled(cache.getName());
            throw new NullPointerException("query Engine not enabled");
        }

        try {
            // decode the query request object
            QueryRequest request = ProtobufUtil.fromByteArray(queryEngine.getSerializationContext(), query, 0, query.length, QueryRequest.class);

            long startOffset = request.getStartOffset() == null ? -1 : request.getStartOffset();
            int maxResults = request.getMaxResults() == null ? -1 : request.getMaxResults();

            // create the query
            BaseQuery q = queryEngine.makeQuery(request.getQueryString(), request.getNamedParametersMap(), startOffset, maxResults);

            System.out.println(q.list());

        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Hello world!");
        return new byte[0];
    }

}
