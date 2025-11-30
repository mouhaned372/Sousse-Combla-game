package net.thevpc.gaming.atom.examples.kombla.main.server.dal;

import net.thevpc.gaming.atom.examples.kombla.main.shared.dal.RMIClientService;
import net.thevpc.gaming.atom.examples.kombla.main.shared.dal.RMIServerService;
import net.thevpc.gaming.atom.examples.kombla.main.shared.model.StartGameInfo;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;

public class RMIServerServiceImpl extends UnicastRemoteObject implements RMIServerService {
    private final MainServerDAOListener listener;
    private final Map<Integer, RMIClientService> playerMap = new HashMap<>();

    public RMIServerServiceImpl(MainServerDAOListener listener) throws RemoteException {
        this.listener = listener;
    }

    @Override
    public StartGameInfo connect(String playerName, RMIClientService cli) throws RemoteException {
        StartGameInfo info = listener.onReceivePlayerJoined(playerName);
        playerMap.put(info.getPlayerId(), cli);
        System.out.println("Joueur " + playerName + " (ID: " + info.getPlayerId() + ") connecté");
        return info;
    }

    @Override
    public void moveRight(int playerId) throws RemoteException {
        listener.onReceiveMoveRight(playerId);
    }

    @Override
    public void moveLeft(int playerId) throws RemoteException {
        listener.onReceiveMoveLeft(playerId);
    }

    @Override
    public void moveUp(int playerId) throws RemoteException {
        listener.onReceiveMoveUp(playerId);
    }

    @Override
    public void moveDown(int playerId) throws RemoteException {
        listener.onReceiveMoveDown(playerId);
    }

    @Override
    public void fire(int playerId) throws RemoteException {
        listener.onReceiveReleaseBomb(playerId);
    }

    public Map<Integer, RMIClientService> getPlayerMap() {
        return playerMap;
    }
}