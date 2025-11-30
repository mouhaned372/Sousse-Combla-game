package net.thevpc.gaming.atom.examples.kombla.main.server.dal;

import net.thevpc.gaming.atom.examples.kombla.main.shared.dal.RMIClientService;
import net.thevpc.gaming.atom.examples.kombla.main.shared.engine.AppConfig;
import net.thevpc.gaming.atom.examples.kombla.main.shared.model.DynamicGameModel;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.HashMap;
import java.util.Map;

public class RMIMainServerDAO implements MainServerDAO {

    private RMIServerServiceImpl serverService;

    @Override
    public void start(MainServerDAOListener listener, AppConfig properties) throws IOException {
        try {
            serverService = new RMIServerServiceImpl(listener);
            Registry registry;
            try {
                registry = LocateRegistry.createRegistry(1099);
            } catch (RemoteException e) {
                registry = LocateRegistry.getRegistry(1099);
            }
            registry.rebind("GameServer", serverService);
            System.out.println("Serveur RMI prêt sur le port 1099");

        } catch (RemoteException e) {
            throw new IOException("Erreur démarrage serveur RMI", e);
        }
    }

    @Override
    public void sendModelChanged(DynamicGameModel dynamicGameModel) throws RemoteException {
        Map<Integer, RMIClientService> currentPlayers = serverService.getPlayerMap();
        for (Map.Entry<Integer, RMIClientService> entry : currentPlayers.entrySet()) {
            try {
                RMIClientService client = entry.getValue();
                client.modelChanged(dynamicGameModel);
            } catch (RemoteException e) {
                System.err.println("Erreur envoi modèle au client " + entry.getKey() + ": " + e.getMessage());
            }
        }
    }
}