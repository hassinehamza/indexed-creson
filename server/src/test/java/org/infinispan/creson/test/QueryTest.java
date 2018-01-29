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
        Obj obj1 = new Obj("obj1",5);
        Obj obj2 = new Obj("obj2",7);
        Obj obj3 = new Obj("obj3",5);
        Obj obj4 = new Obj("obj4",10);
        obj1.setX(5);
        obj2.toString();


//        CresonObject cr1 = new CresonObject(obj4);
//        cache.put(6, obj1);
//        cache.put(7, obj2);
        // cache.put(9, obj3);


        System.out.println(cache.keySet());

        QueryFactory qf = Search.getQueryFactory(cache);
//        Query q1 = qf.create("from org.infinispan.creson.Obj o where o.x = 10");
//        System.out.println("creson query 1ist" + q1.list());

        Query q2 = qf.create("from org.infinispan.creson.Obj");
        System.out.println("infinispan query 1ist" + q2.list());


        System.out.println("query sent");
    }

}
