package net.thevpc.gaming.atom.examples.kombla.main.client.dal;

import net.thevpc.gaming.atom.examples.kombla.main.shared.dal.RMIClientService;
import net.thevpc.gaming.atom.examples.kombla.main.shared.dal.RMIServerService;
import net.thevpc.gaming.atom.examples.kombla.main.shared.engine.AppConfig;
import net.thevpc.gaming.atom.examples.kombla.main.shared.model.StartGameInfo;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class RMIMainClientDAO implements MainClientDAO {
    private MainClientDAOListener listener;
    private RMIServerService serverService;
    private RMIClientService clientService;
    private int playerId;

    @Override
    public void start(MainClientDAOListener listener, AppConfig properties) {
        this.listener = listener;
    }

    @Override
    public StartGameInfo connect() {
        try {
            clientService = new RMIClientServiceImpl(listener);
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            serverService = (RMIServerService) registry.lookup("GameServer");
            StartGameInfo startInfo = serverService.connect("Player", clientService);
            this.playerId = startInfo.getPlayerId();

            return startInfo;

        } catch (RemoteException | NotBoundException e) {
            throw new RuntimeException("Erreur de connexion au serveur RMI");
        }
    }

    @Override
    public void sendMoveLeft() {
        try {
            if (serverService != null) {
                serverService.moveLeft(playerId);
            }
        } catch (RemoteException e) {
            System.err.println("Erreur envoi commande LEFT: ");
        }
    }

    @Override
    public void sendMoveRight() {
        try {
            if (serverService != null) {
                serverService.moveRight(playerId);
            }
        } catch (RemoteException e) {
            System.err.println("Erreur envoi commande RIGHT: ");
        }
    }

    @Override
    public void sendMoveUp() {
        try {
            if (serverService != null) {
                serverService.moveUp(playerId);
            }
        } catch (RemoteException e) {
            System.err.println("Erreur envoi commande UP: ");
        }
    }

    @Override
    public void sendMoveDown() {
        try {
            if (serverService != null) {
                serverService.moveDown(playerId);
            }
        } catch (RemoteException e) {
            System.err.println("Erreur envoi commande DOWN: ");
        }
    }

    @Override
    public void sendFire() {
        try {
            if (serverService != null) {
                serverService.fire(playerId);
            }
        } catch (RemoteException e) {
            System.err.println("Erreur envoi commande FIRE: ");
        }
    }
}