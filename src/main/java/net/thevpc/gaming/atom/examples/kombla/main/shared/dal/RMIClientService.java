package net.thevpc.gaming.atom.examples.kombla.main.shared.dal;

import net.thevpc.gaming.atom.examples.kombla.main.shared.model.DynamicGameModel;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RMIClientService extends Remote {
    void modelChanged(DynamicGameModel model) throws RemoteException;
}