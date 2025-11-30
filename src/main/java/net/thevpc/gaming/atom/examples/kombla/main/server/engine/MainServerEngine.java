/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.gaming.atom.examples.kombla.main.server.engine;

import net.thevpc.gaming.atom.examples.kombla.main.server.dal.RMIMainServerDAO;
import net.thevpc.gaming.atom.examples.kombla.main.shared.engine.BaseMainEngine;
import net.thevpc.gaming.atom.examples.kombla.main.server.dal.MainServerDAOListener;
import net.thevpc.gaming.atom.examples.kombla.main.shared.model.DynamicGameModel;
import net.thevpc.gaming.atom.examples.kombla.main.shared.model.StartGameInfo;
import net.thevpc.gaming.atom.annotations.AtomSceneEngine;
import net.thevpc.gaming.atom.model.*;
import net.thevpc.gaming.atom.examples.kombla.main.server.dal.MainServerDAO;
//import net.thevpc.gaming.atom.examples.kombla.main.server.dal.TcpMainServerDAO;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.stream.Collectors;

/**
 * @author Taha Ben Salah (taha.bensalah@gmail.com)
 */
@AtomSceneEngine(id = "mainServer", columns = 12, rows = 12)
public class MainServerEngine extends BaseMainEngine {

//    private MainServerDAO dal = new TcpMainServerDAO();
private MainServerDAO dal = new RMIMainServerDAO();

    @Override
    protected void sceneActivating() {
        super.sceneActivating();

        if (dal == null) {
            dal = new RMIMainServerDAO();
            System.err.println("WARNING: dal était null, initialisation manuelle");
        }

        try {
            dal.start(new MainServerDAOListener() {
                @Override
                public StartGameInfo onReceivePlayerJoined(String name) {
                    Sprite sprite = addBomberPlayer(name);
                    int playerId = sprite.getPlayerId();
                    return new StartGameInfo(playerId, maze);
                }

                @Override
                public void onReceiveMoveLeft(int playerId) {
                    move(playerId, Orientation.WEST);
                }

                @Override
                public void onReceiveMoveRight(int playerId) {
                    move(playerId, Orientation.EAST);
                }

                @Override
                public void onReceiveMoveUp(int playerId) {
                    move(playerId, Orientation.NORTH);
                }

                @Override
                public void onReceiveMoveDown(int playerId) {
                    move(playerId, Orientation.SOUTH);
                }

                @Override
                public void onReceiveReleaseBomb(int playerId) {
                    releaseBomb(playerId);
                }
            }, getAppConfig(getGameEngine()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * each frame broadcast shared data to players. This method is called by
     * ATOM to READ model (R/O mode)
     */
    @Override
    protected void modelUpdated() {
        // Vérification de sécurité avant utilisation
        if (dal == null) {
            System.err.println("WARNING: dal est null dans modelUpdated");
            return;
        }

        switch ((String) getModel().getProperty("Phase")) {
            case "WAITING": {
                //do nothing
                break;
            }
            case "GAMING":
            case "GAMEOVER": {
                try {
                    dal.sendModelChanged(new DynamicGameModel(getFrame(),
                            //copy to fix ObjectOutputStream issue!
                            getSprites().stream().map(Sprite::copy).collect(Collectors.toList())
                            , getPlayers().stream().map(Player::copy).collect(Collectors.toList())
                    ));
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
                break;
            }
        }
    }
}





///*
// * To change this template, choose Tools | Templates
// * and open the template in the editor.
// */
//package net.thevpc.gaming.atom.examples.kombla.main.server.engine;
//
//import net.thevpc.gaming.atom.examples.kombla.main.shared.engine.BaseMainEngine;
//import net.thevpc.gaming.atom.examples.kombla.main.server.dal.MainServerDAOListener;
//import net.thevpc.gaming.atom.examples.kombla.main.shared.model.DynamicGameModel;
//import net.thevpc.gaming.atom.examples.kombla.main.shared.model.StartGameInfo;
//import net.thevpc.gaming.atom.annotations.AtomSceneEngine;
//import net.thevpc.gaming.atom.model.*;
//import net.thevpc.gaming.atom.examples.kombla.main.server.dal.MainServerDAO;
//
//import java.io.IOException;
//import java.util.stream.Collectors;
//
///**
// * @author Taha Ben Salah (taha.bensalah@gmail.com)
// */
//@AtomSceneEngine(id = "mainServer", columns = 12, rows = 12)
//public class MainServerEngine extends BaseMainEngine {
//
//    private MainServerDAO dal;
//
//
//    @Override
//    protected void sceneActivating() {
//        super.sceneActivating();
//        //put here your MainClientDAO instance
////        dal = new TCPMainServerDAO();
////        dal = new UDPMainServerDAO();
//        try {
//            dal.start(new MainServerDAOListener() {
//                @Override
//                public StartGameInfo onReceivePlayerJoined(String name) {
//                    Sprite sprite = addBomberPlayer(name);
//                    int playerId = sprite.getPlayerId();
//                    return new StartGameInfo(playerId, maze);
//                }
//
//                @Override
//                public void onReceiveMoveLeft(int playerId) {
//                    move(playerId, Orientation.WEST);
//                }
//
//                @Override
//                public void onReceiveMoveRight(int playerId) {
//                    move(playerId, Orientation.EAST);
//                }
//
//                @Override
//                public void onReceiveMoveUp(int playerId) {
//                    move(playerId, Orientation.NORTH);
//
//                }
//
//                @Override
//                public void onReceiveMoveDown(int playerId) {
//                    move(playerId, Orientation.SOUTH);
//                }
//
//                @Override
//                public void onReceiveReleaseBomb(int playerId) {
//                    releaseBomb(playerId);
//                }
//            }, getAppConfig(getGameEngine()));
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//
//
//
//    /**
//     * each frame broadcast shared data to players. This method is called by
//     * ATOM to READ model (R/O mode)
//     */
//    @Override
//    protected void modelUpdated() {
//        switch ((String) getModel().getProperty("Phase")) {
//            case "WAITING": {
//                //do nothing
//                break;
//            }
//            case "GAMING":
//            case "GAMEOVER": {
//                //do nothing
//                dal.sendModelChanged(new DynamicGameModel(getFrame(),
//                        //copy to fix ObjectOutputStream issue!
//                        getSprites().stream().map(Sprite::copy).collect(Collectors.toList())
//                        , getPlayers().stream().map(Player::copy).collect(Collectors.toList())
//                ));
//                break;
//            }
//        }
//    }
//}
