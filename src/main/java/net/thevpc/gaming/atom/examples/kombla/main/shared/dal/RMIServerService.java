package net.thevpc.gaming.atom.examples.kombla.main.shared.dal;

import net.thevpc.gaming.atom.examples.kombla.main.shared.model.StartGameInfo;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RMIServerService extends Remote {
    public StartGameInfo connect(String playerName, RMIClientService cli)  throws RemoteException;
    public void moveRight(int playerId)  throws RemoteException;
    public void moveLeft(int playerId)  throws RemoteException;
    public void moveUp(int playerId)  throws RemoteException;
    public void moveDown(int playerId)  throws RemoteException;
    public void fire(int playerId)  throws RemoteException;
}
