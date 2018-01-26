package org.infinispan.creson.query;

import org.infinispan.AdvancedCache;

public class QueryFacadeImpl implements org.infinispan.server.core.QueryFacade{
    @Override
    public byte[] query(AdvancedCache<byte[], byte[]> cache, byte[] query) {
        System.out.println("cache " + cache);
        System.out.println("query " + query);
        System.out.println("Hello World");
        return new byte[0];
    }
}
