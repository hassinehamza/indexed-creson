package org.infinispan.creson.test;

import com.google.protobuf.Descriptors;
import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import org.infinispan.creson.Factory;
import org.infinispan.creson.IndexedObject;
import org.infinispan.creson.query.Search;
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
    public void shouldQueryObjects() throws IOException, Descriptors.DescriptorValidationException {

        ConfigurationBuilder builder = new ConfigurationBuilder();
        builder.addServer().host("127.0.0.1").port(11222);


        RemoteCacheManager cacheManager = new RemoteCacheManager(builder.build());

        RemoteCache cache = cacheManager.getCache(CRESON_CACHE_NAME);

        IndexedObject obj1 = new IndexedObject(1);
        IndexedObject obj2 = new IndexedObject(2);
        IndexedObject obj3 = new IndexedObject(3);
        obj1.setField(8);
        obj2.setField(7);
        obj3.setField(7);

        QueryFactory qf = Search.getQueryFactory(cache);
        Query q = qf.create("from org.infinispan.creson.IndexedObject o where o.value = 7");
        assert (q.list().size() == 2);
    }

}
