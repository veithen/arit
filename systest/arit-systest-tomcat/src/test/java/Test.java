import java.io.File;

import org.apache.catalina.Context;
import org.apache.catalina.Engine;
import org.apache.catalina.Host;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.realm.MemoryRealm;
import org.apache.catalina.startup.Embedded;

public class Test {

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
        Embedded container = new Embedded();
        container.setCatalinaHome(new File("target/catalina").getAbsolutePath());
//        container.setRealm(new MemoryRealm());
        Engine engine = container.createEngine();
        engine.setName("engine");
        Host host = container.createHost("localhost", new File("target/webapps").getAbsolutePath());
        engine.addChild(host);
        engine.setDefaultHost(host.getName());
        Context context = container.createContext("/arit", new File("target/webapps/arit-war-war").getAbsolutePath());
        host.addChild(context);
        container.addEngine(engine);
        Connector httpConnector = container.createConnector("localhost", 8888, false);
        container.addConnector(httpConnector);
//        container.setAwait(true);
        container.start();
        Object object = new Object();
        synchronized (object) {
            object.wait();
        }
    }

}
