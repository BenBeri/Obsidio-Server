package com.benberi.cadesim.server;

import com.benberi.cadesim.server.service.GameService;
import com.sun.security.ntlm.Server;
import jdk.nashorn.internal.runtime.linker.Bootstrap;

import java.util.concurrent.*;
import java.util.logging.Logger;

/**
 * Bootstrap
 * @author Ben Beri <benberi545@gmail.com>
 *                  <https://github.com/benberi>
 */
public class GameServerBootstrap {

    private Logger logger = Logger.getLogger("Bootstrap");

    private ScheduledExecutorService executorService = Executors.newScheduledThreadPool(2);

    /**
     * Server's configuration
     */
    private ServerConfiguration config;

    /**
     * The server context
     */
    private ServerContext context;

    /**
     * The start time of the server
     */
    private long start;
    public GameServerBootstrap(ServerConfiguration cfg) {
        this.config = cfg;
        context = new ServerContext(cfg);
    }

    private void startServer() throws InterruptedException {
        start = System.currentTimeMillis();

        logger.info("Starting the Game Server, configuration: " + config.toString());
        logger.info("Starting up the host server....");
        CadeServer server = new CadeServer(context, this); // to notify back its done
        executorService.execute(server);

    }

    public void startServices() {

        logger.info("Starting up the game service....");
        GameService service = new GameService(context);
        executorService.scheduleAtFixedRate(service, 0, Constants.SERVICE_LOOP_DELAY, TimeUnit.MILLISECONDS);

        long time = System.currentTimeMillis() - start;

        logger.info("Game Server loaded successfully in " + (int) (time / 1000) + " seconds.");
    }

    /**
     * Main method
     * @param args The arguments  for the simulator server
     */
    public static void main(String... args) throws InterruptedException {
        ServerConfiguration config = new ServerConfiguration();
        config.setPlayerLimit(2);
        config.setMapType(0);

        GameServerBootstrap bootstrap = new GameServerBootstrap(config);
        bootstrap.startServer();
    }
}
