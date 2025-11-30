package net.thevpc.gaming.atom.examples.kombla.main.client.dal;

import net.thevpc.gaming.atom.examples.kombla.main.shared.dal.RMIClientService;
import net.thevpc.gaming.atom.examples.kombla.main.shared.model.DynamicGameModel;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class RMIClientServiceImpl extends UnicastRemoteObject implements RMIClientService {
    private final MainClientDAOListener listener;

    protected RMIClientServiceImpl(MainClientDAOListener listener) throws RemoteException {
        this.listener = listener;
    }

    @Override
    public void modelChanged(DynamicGameModel model) throws RemoteException {
        if (listener != null) {
            listener.onModelChanged(model);
        }
    }
}