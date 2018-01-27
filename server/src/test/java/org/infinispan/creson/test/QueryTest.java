package org.infinispan.creson.test;

import com.google.protobuf.Descriptors;
import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import org.infinispan.creson.Factory;
import org.infinispan.creson.Obj;
import org.infinispan.creson.search.Search;
import org.infinispan.query.dsl.Query;
import org.infinispan.query.dsl.QueryFactory;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.infinispan.creson.Factory.CRESON_CACHE_NAME;

public class QueryTest {

    @BeforeMethod
    public void setUp() throws Exception {
        Factory.get("127.0.0.1:11222");
    }

    @Test
    public void work() {
        assert (true);
    }

    @Test
    public void should_rank_heros() throws IOException, Descriptors.DescriptorValidationException {

        ConfigurationBuilder builder = new ConfigurationBuilder();
        builder.addServer().host("127.0.0.1").port(11222);


        RemoteCacheManager cacheManager = new RemoteCacheManager(builder.build());

        RemoteCache cache = cacheManager.getCache(CRESON_CACHE_NAME);
        cache.put(5, new Obj(5));
        cache.put(6, new Obj(5));
        cache.put(7, new Obj(5));
        cache.put(9, new Obj(7));

        QueryFactory qf = Search.getQueryFactory(cache);
        System.out.println("get " + cache.get(5));

        Query q = qf.create("from org.infinispan.creson.Obj o where o.x = 5");
        System.out.println("list" + q.list());

        System.out.println("query sent");
    }

}
