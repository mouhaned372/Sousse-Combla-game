package net.thevpc.gaming.atom.examples.kombla.main.server.dal;

import net.thevpc.gaming.atom.examples.kombla.main.shared.engine.AppConfig;
import net.thevpc.gaming.atom.examples.kombla.main.shared.model.DynamicGameModel;

import java.io.IOException;

/**
 * Created by vpc on 10/7/16.
 */
public interface MainServerDAO {
    /**
     * stats in non-blocking mode the DAO
     * @param listener dao listener to catch dal events
     * @param properties extra properties such as "serverPort", "serverAddress"
     */

    void start(MainServerDAOListener listener, AppConfig properties) throws IOException;

    void sendModelChanged(DynamicGameModel dynamicGameModel);

}
