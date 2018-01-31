package org.infinispan.creson;


import org.infinispan.configuration.cache.CacheMode;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.cache.Index;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.context.Flag;
import org.infinispan.creson.server.StateMachineInterceptor;
import org.infinispan.creson.utils.ConfigurationHelper;
import org.infinispan.interceptors.impl.CallInterceptor;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;
import org.infinispan.server.hotrod.HotRodServer;
import org.infinispan.server.hotrod.configuration.HotRodServerConfigurationBuilder;
import org.infinispan.util.logging.Log;
import org.infinispan.util.logging.LogFactory;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import sun.misc.Signal;
import sun.misc.SignalHandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import static org.infinispan.creson.Factory.CRESON_CACHE_NAME;

/**
 * @author Pierre Sutra
 */
public class Server {

    private static final Log log = LogFactory.getLog(Server.class);
    private static final String defaultServer = "localhost:11222";
    private static final String userLibraries = "/tmp";

    @Option(name = "-server", usage = "ip:port or ip of the server")
    private String server = defaultServer;

    @Option(name = "-proxy", usage = "proxy server as seen by clients")
    private String proxyServer = null;

    @Option(name = "-rf", usage = "replication factor")
    private int replicationFactor = 1;

    @Option(name = "-me", usage = "max #entries in the object cache (implies -p)")
    private long maxEntries = Long.MAX_VALUE;

    @Option(name = "-ec2", usage = "use AWS EC2 jgroups configuration")
    private boolean useEC2 = false;

    @Option(name = "-userLibs", usage = "directory containing the user libraries")
    private String userLib = userLibraries;

    private volatile boolean running = false;

    private static ArrayList<Class<?>> indexedClasses = new ArrayList<>();

    public Server() {
    }

    public Server(String server, String proxyServer, int replicationFactor, boolean usePersistence) {
        this.server = server;
        this.proxyServer = proxyServer;
        this.replicationFactor = replicationFactor;
    }

    public static void main(String args[]) {
        new Server().doMain(args);
    }

    public void doMain(String[] args) {

        CmdLineParser parser = new CmdLineParser(this);

        parser.setUsageWidth(80);

        try {
            parser.parseArgument(args);
        } catch (CmdLineException e) {
            System.err.println(e.getMessage());
            parser.printUsage(System.err);
            System.err.println();
            return;
        }

        String host = server.split(":")[0];
        int port = Integer.valueOf(server.split(":").length == 2 ? server.split(":")[1] : "11222");

        GlobalConfigurationBuilder gbuilder = GlobalConfigurationBuilder.defaultClusteredBuilder();
        gbuilder.transport().clusterName("creson-cluster");
        gbuilder.transport().nodeName("creson-server-" + host);

        if (useEC2)
            gbuilder.transport().addProperty("configurationFile", "jgroups-creson-ec2.xml");

        ConfigurationBuilder builder = ConfigurationHelper.buildConfiguration(
                CacheMode.DIST_ASYNC,
                replicationFactor,
                maxEntries,
                System.getProperty("store-creson-server" + host),
                true);
        builder.persistence().clearStores().passivation(false);

        final EmbeddedCacheManager cm = new DefaultCacheManager(gbuilder.build(), builder.build(),
                true);

        HotRodServerConfigurationBuilder hbuilder = new HotRodServerConfigurationBuilder();
        hbuilder.topologyStateTransfer(true);
        hbuilder.host(host);
        hbuilder.port(port);

        if (proxyServer != null && !proxyServer.equals(defaultServer)) {
            String proxyHost = proxyServer.split(":")[0];
            int proxyPort = Integer
                    .valueOf(proxyServer.split(":").length == 2 ? proxyServer.split(":")[1] : "11222");
            hbuilder.proxyHost(proxyHost);
            hbuilder.proxyPort(proxyPort);
        }

        hbuilder.workerThreads(100);
        hbuilder.tcpNoDelay(true);

        final HotRodServer server = new HotRodServer();
        server.start(hbuilder.build(), cm);

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        File folder = new File(userLib);
        File[] listOfFiles = folder.listFiles();
        for (File file : listOfFiles) {
            if (file.isFile() && file.getName().matches(".*\\.jar")) {
                loadLibrary(file);
            }
        }

        builder.read(cm.getDefaultCacheConfiguration());
        StateMachineInterceptor stateMachineInterceptor = new StateMachineInterceptor();
        builder.compatibility().enabled(true); // for HotRod
        builder.customInterceptors().addInterceptor().before(CallInterceptor.class).interceptor(stateMachineInterceptor);
        builder.indexing().index(Index.LOCAL)
               .addProperty("default.directory_provider", "ram")
               .addProperty("lucene_version", "LUCENE_CURRENT");
        // builder.indexing().disable();
//        for(Class clazz : indexedClasses) {
//            System.out.println("clazz " + clazz);
//            builder.indexing().addIndexedEntity(clazz);
//        }
     //  builder.indexing().addIndexedEntity(Obj.class);
      // builder.indexing().disable();
        builder.persistence().clearStores().passivation(false);

        builder.clustering().stateTransfer().chunkSize(100);

        cm.defineConfiguration(CRESON_CACHE_NAME, builder.build());
        stateMachineInterceptor.setup(Factory.forCache(cm.getCache(CRESON_CACHE_NAME)));

        System.out.println("LAUNCHED");
        scheduler.schedule((Callable<Void>) () -> {
            while(true) {
                System.out.println("size=" + cm.getCache(CRESON_CACHE_NAME).getAdvancedCache().withFlags(Flag.CACHE_MODE_LOCAL).size());
                Thread.sleep(1000);            }
            }, 1, TimeUnit.SECONDS);

            //        scheduler.schedule((Callable<Void>) () -> {
//            while (true) {
//                File folder = new File(userLib);
//                File[] listOfFiles = folder.listFiles();
//                for (File file : listOfFiles) {
//                    if (file.isFile() && file.getName().matches(".*\\.jar")) {
//                        loadLibrary(file);
//                    }
//                }
//                Thread.sleep(1000);
//            }
//        }, 1, TimeUnit.SECONDS);
//
//        try {
//            Thread.sleep(3000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

        SignalHandler sh = s -> {
            System.out.println("CLOSING");
            try {
                scheduler.shutdown();
                Factory factory = Factory.forCache(cm.getCache(CRESON_CACHE_NAME));
                if (factory != null)
                    factory.close();
                server.stop();
                cm.stop();
                System.exit(0);
            } catch (Throwable t) {
                System.exit(-1);
            }
        };
        Signal.handle(new Signal("INT"), sh);
        Signal.handle(new Signal("TERM"), sh);

//    Thread.currentThread().interrupt();

        //   System.exit(0);

    }

    private static ArrayList<Class<?>> findIndexedClass(java.io.File jar) {
        JarInputStream jarFile;
        JarEntry jarEntry;
        ArrayList<Class<?>> indexed = new ArrayList<>();
        try {
            jarFile = new JarInputStream(new FileInputStream(jar));
            while ((jarEntry = jarFile.getNextJarEntry()) != null) {
                if (jarEntry.getName().contains("example") && jarEntry.getName().endsWith(".class")) {
                    String str = jarEntry.getName().replace('/', '.').substring(0,
                            jarEntry.getName().length() - 6);
                    Class<?> clazz;
                    try {
                        clazz = Class.forName(str);

                        if (clazz.isAnnotationPresent(org.hibernate.search.annotations.Indexed.class)) {
                            indexed.add(clazz);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            jarFile.close();
            for (Class cls : indexed)
                System.out.println("indexed class " + cls);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return indexed;
    }

    public static synchronized void loadLibrary(java.io.File jar) {
        try {
            java.net.URLClassLoader loader = (java.net.URLClassLoader) ClassLoader.getSystemClassLoader();
            java.net.URL url = jar.toURI().toURL();
            for (java.net.URL it : java.util.Arrays.asList(loader.getURLs())) {
                if (it.equals(url)) {
                    return;
                }
            }
            System.out.println("Loading " + jar.getName());
            java.lang.reflect.Method method = java.net.URLClassLoader.class.getDeclaredMethod("addURL",
                    new Class[]{java.net.URL.class});
            method.setAccessible(true); /* promote the method to public access */
            method.invoke(loader, new Object[]{url});
            indexedClasses = findIndexedClass(jar);
        } catch (final java.lang.NoSuchMethodException | java.lang.IllegalAccessException
                | java.net.MalformedURLException | java.lang.reflect.InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<Class<?>> getIndexedClass() {
        return indexedClasses;
    }
}
