//package net.thevpc.gaming.atom.examples.kombla.main.server.dal;
//
//import net.thevpc.gaming.atom.examples.kombla.main.shared.engine.AppConfig;
//import net.thevpc.gaming.atom.examples.kombla.main.shared.model.DynamicGameModel;
//import net.thevpc.gaming.atom.examples.kombla.main.shared.model.StartGameInfo;
//import net.thevpc.gaming.atom.examples.kombla.main.shared.dal.*;
//import net.thevpc.gaming.atom.model.Player;
//import net.thevpc.gaming.atom.model.Sprite;
//import net.thevpc.gaming.atom.model.Point;
//import java.io.*;
//import java.net.ServerSocket;
//import java.net.Socket;
//import java.net.SocketException;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//
//public class TcpMainServerDAO implements MainServerDAO {
//    boolean running = false;
//    MainServerDAOListener listener;
//    AppConfig appConfig;
//
//    Map<Integer, ClientSession> playerToSocketMap = new HashMap<>();
//
//    @Override
//    public void start(MainServerDAOListener listener, AppConfig properties) throws IOException {
//        this.appConfig = properties;
//        this.listener = listener;
//        int port = appConfig.getServerPort();
//        new Thread(() -> {
//            ServerSocket s = null;
//            try {
//                s = new ServerSocket(port);
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//            running = true;
//            while (running) {
//                ServerSocket finalS = s;
//                new Thread(() -> {
//                    Socket clientSocket = null;
//                    try {
//                        clientSocket = finalS.accept();
//                    } catch (IOException e) {
//                        throw new RuntimeException(e);
//                    }
//                    ClientSession cs = new ClientSession();
//                    cs.socket = clientSocket;
//                    try {
//                        cs.in = new DataInputStream(clientSocket.getInputStream());
//                    } catch (IOException e) {
//                        throw new RuntimeException(e);
//                    }
//                    try {
//                        cs.out = new DataOutputStream(clientSocket.getOutputStream());
//                    } catch (IOException e) {
//                        throw new RuntimeException(e);
//                    }
//                    String n;
//                    try {
//                        n = cs.in.readUTF();
//                    } catch (IOException e) {
//                        throw new RuntimeException(e);
//                    }
//                    StartGameInfo i = listener.onReceivePlayerJoined(n);
//                    cs.playerId = i.getPlayerId();
//                    try {
//                        writeStartGameInfo(i, cs);
//                    } catch (IOException e) {
//                        throw new RuntimeException(e);
//                    }
//                    playerToSocketMap.put(cs.playerId, cs);
//
//                    while (true) {
//                        traiterReq(cs);
//                    }
//                }).start();
//
//            }
//
//        }).start();
//    }
//
//    public static class ClientSession {
//        public int playerId;
//        public Socket socket;
//        public DataInputStream in;
//        public DataOutputStream out;
//    }
//
//    public void writeStartGameInfo(StartGameInfo i, ClientSession cs) throws IOException {
//        cs.out.writeInt(i.getPlayerId());
//        cs.out.writeInt(i.getMaze().length);
//        cs.out.writeInt(i.getMaze()[0].length);
//        for (int j = 0; j < i.getMaze().length; j++) {
//            for (int k = 0; k < i.getMaze()[j].length; k++) {
//                cs.out.writeInt(j);
//            }
//
//        }
//    }
//
//    public void traiterReq(ClientSession cs) {
//        try {
//            int command = cs.in.readInt();
//
//            switch (command) {
//                case ProtocolConstants.CONNECT:
//                    String playerName = cs.in.readUTF();
//                    StartGameInfo startInfo = listener.onReceivePlayerJoined(playerName);
//                    cs.playerId = startInfo.getPlayerId();
//                    writeStartGameInfo(startInfo, cs);
//                    playerToSocketMap.put(cs.playerId, cs);
//                    break;
//                case ProtocolConstants.LEFT:
//                    listener.onReceiveMoveLeft(cs.playerId);
//                    break;
//                case ProtocolConstants.RIGHT:
//                    listener.onReceiveMoveRight(cs.playerId);
//                    break;
//                case ProtocolConstants.UP:
//                    listener.onReceiveMoveUp(cs.playerId);
//                    break;
//                case ProtocolConstants.DOWN:
//                    listener.onReceiveMoveDown(cs.playerId);
//                    break;
//                case ProtocolConstants.FIRE:
//                    listener.onReceiveReleaseBomb(cs.playerId);
//                    break;
//                default:
//                    System.err.println("Commande inconnue: ");
//            }
//        } catch (IOException e) {
//            System.err.println("Client déconnecté: ");
//            playerToSocketMap.remove(cs.playerId);
//            Thread.currentThread().interrupt();
//        }
//    }
//
//
//    private void writePlayer(Player player, DataOutputStream out) throws IOException {
//        out.writeInt(player.getId());
//        out.writeUTF(player.getName() != null ? player.getName() : "");
//
//        Map<String, Object> properties = player.getProperties();
//        out.writeInt(properties.size());
//        for (Map.Entry<String, Object> entry : properties.entrySet()) {
//            out.writeUTF(entry.getKey());
//            Object value = entry.getValue();
//            out.writeUTF(value != null ? value.toString() : "");
//        }
//    }
//
//
//    private void writeSprite(Sprite sprite, DataOutputStream out) throws IOException {
//        out.writeInt(sprite.getId());
//        String kind = sprite.getKind();
//        out.writeInt(Integer.parseInt(kind));
//        out.writeUTF(sprite.getName() != null ? sprite.getName() : "");
//        out.writeDouble(sprite.getLocation().getX());
//        out.writeDouble(sprite.getLocation().getY());
//        out.writeDouble(sprite.getDirection());
//        out.writeInt(sprite.getPlayerId());
//
//        Map<String, Object> properties = sprite.getProperties();
//        out.writeInt(properties.size());
//        for (Map.Entry<String, Object> entry : properties.entrySet()) {
//            out.writeUTF(entry.getKey());
//            Object value = entry.getValue();
//            out.writeUTF(value != null ? value.toString() : "");
//        }
//    }
//
//    @Override
//    public void sendModelChanged(DynamicGameModel dynamicGameModel) {
//        for (ClientSession cs : playerToSocketMap.values()) {
//            try {
//                cs.out.writeLong(dynamicGameModel.getFrame());
//
//                List<Player> players = dynamicGameModel.getPlayers();
//                cs.out.writeInt(players.size());
//                for (Player player : players) {
//                    writePlayer(player, cs.out);
//                }
//
//                List<Sprite> sprites = dynamicGameModel.getSprites();
//                cs.out.writeInt(sprites.size());
//                for (Sprite sprite : sprites) {
//                    writeSprite(sprite, cs.out);
//                }
//                cs.out.flush();
//
//            } catch (IOException e) {
//                System.err.println("Erreur envoi modèle au client ");
//            }
//        }
//    }
//    public void stop() {
//        running = false;
//        for (ClientSession cs : playerToSocketMap.values()) {
//            try {
//                cs.socket.close();
//            } catch (IOException e) {
//                System.err.println("Erreur fermeture socket: " );
//            }
//        }
//        playerToSocketMap.clear();
//    }
//}
//
//
//
//
//
//
//
//
//
//
//
//
////  Correction pour cette erreur
/////home/mouhaned/.jdks/ms-21.0.8/bin/java -javaagent:/home/mouhaned/Téléchargements/idea-IU-252.26830.84/lib/idea_rt.jar=36597 -Dfile.encoding=UTF-8 -Dsun.stdout.encoding=UTF-8 -Dsun.stderr.encoding=UTF-8 -classpath /home/mouhaned/atom/sample-games/game-sousse-kombla/target/classes:/home/mouhaned/.m2/repository/net/thevpc/gaming/atom-game-engine/1.5.4.2/atom-game-engine-1.5.4.2.jar:/home/mouhaned/.m2/repository/net/thevpc/common/thevpc-common-classpath/1.3.2/thevpc-common-classpath-1.3.2.jar net.thevpc.gaming.atom.examples.kombla.KomblaGame
////[4,014s][warning][os,thread] Failed to start thread "Unknown thread" - pthread_create failed (EAGAIN) for attributes: stacksize: 1024k, guardsize: 4k, detached.
////[4,014s][warning][os,thread] Failed to start the native thread for java.lang.Thread "Thread-8313"
////Exception in thread "Thread-0" java.lang.OutOfMemoryError: unable to create native thread: possibly out of memory or process/resource limits reached
////at java.base/java.lang.Thread.start0(Native Method)
////at java.base/java.lang.Thread.start(Thread.java:1526)
////at net.thevpc.gaming.atom.examples.kombla.main.server.dal.TcpMainServerDAO.lambda$start$1(TcpMainServerDAO.java:79)
////at java.base/java.lang.Thread.run(Thread.java:1583)
//
//
////
////
////package net.thevpc.gaming.atom.examples.kombla.main.server.dal;
////
////import net.thevpc.gaming.atom.examples.kombla.main.shared.engine.AppConfig;
////import net.thevpc.gaming.atom.examples.kombla.main.shared.model.DynamicGameModel;
////import net.thevpc.gaming.atom.examples.kombla.main.shared.model.StartGameInfo;
////import net.thevpc.gaming.atom.examples.kombla.main.shared.dal.*;
////        import net.thevpc.gaming.atom.model.Player;
////import net.thevpc.gaming.atom.model.Sprite;
////
////import java.io.*;
////        import java.net.ServerSocket;
////import java.net.Socket;
////import java.util.HashMap;
////import java.util.List;
////import java.util.Map;
////import java.util.concurrent.ExecutorService;
////import java.util.concurrent.Executors;
////
////public class TcpMainServerDAO implements MainServerDAO {
////    private volatile boolean running = false;
////    private MainServerDAOListener listener;
////    private AppConfig appConfig;
////    private ServerSocket serverSocket;
////    private ExecutorService threadPool;
////    private Map<Integer, ClientSession> playerToSocketMap = new HashMap<>();
////
////    @Override
////    public void start(MainServerDAOListener listener, AppConfig properties) throws IOException {
////        this.listener = listener;
////        this.appConfig = properties;
////        this.threadPool = Executors.newFixedThreadPool(10);
////
////        int port = appConfig.getServerPort();
////        serverSocket = new ServerSocket(port);
////        running = true;
////
////        new Thread(this::acceptConnections, "Server-Accept-Thread").start();
////    }
////
////    private void acceptConnections() {
////        try {
////            while (running && !serverSocket.isClosed()) {
////                Socket clientSocket = serverSocket.accept();
////                threadPool.submit(() -> processClient(clientSocket));
////            }
////        } catch (IOException e) {
////            if (running) {
////                System.err.println("Erreur acceptation connexion: " + e.getMessage());
////            }
////        }
////    }
////
////    private void processClient(Socket clientSocket) {
////        ClientSession cs = new ClientSession();
////        cs.socket = clientSocket;
////
////        try {
////            cs.in = new DataInputStream(clientSocket.getInputStream());
////            cs.out = new DataOutputStream(clientSocket.getOutputStream());
////
////            int command = cs.in.readInt();
////            if (command != ProtocolConstants.CONNECT) {
////                System.err.println("Commande initiale attendue: CONNECT, reçu: " + command);
////                clientSocket.close();
////                return;
////            }
////
////            String playerName = cs.in.readUTF();
////            StartGameInfo startInfo = listener.onReceivePlayerJoined(playerName);
////            cs.playerId = startInfo.getPlayerId();
////
////            writeStartGameInfo(startInfo, cs);
////
////            playerToSocketMap.put(cs.playerId, cs);
////
////            System.out.println("Joueur " + playerName + " (ID: " + cs.playerId + ") connecté");
////
////            processClientCommands(cs);
////
////        } catch (IOException e) {
////            System.err.println("Erreur traitement client: " + e.getMessage());
////        } finally {
////            if (cs.playerId != 0) {
////                playerToSocketMap.remove(cs.playerId);
////                System.out.println("Joueur " + cs.playerId + " déconnecté");
////            }
////            try {
////                clientSocket.close();
////            } catch (IOException ex) {
////            }
////        }
////    }
////
////    private void processClientCommands(ClientSession cs) {
////        try {
////            while (running && !cs.socket.isClosed()) {
////                int command = cs.in.readInt();
////
////                switch (command) {
////                    case ProtocolConstants.LEFT:
////                        listener.onReceiveMoveLeft(cs.playerId);
////                        break;
////                    case ProtocolConstants.RIGHT:
////                        listener.onReceiveMoveRight(cs.playerId);
////                        break;
////                    case ProtocolConstants.UP:
////                        listener.onReceiveMoveUp(cs.playerId);
////                        break;
////                    case ProtocolConstants.DOWN:
////                        listener.onReceiveMoveDown(cs.playerId);
////                        break;
////                    case ProtocolConstants.FIRE:
////                        listener.onReceiveReleaseBomb(cs.playerId);
////                        break;
////                    default:
////                        System.err.println("Commande inconnue: " + command);
////                }
////            }
////        } catch (IOException e) {
////            if (running) {
////                System.err.println("Client " + cs.playerId + " déconnecté: " + e.getMessage());
////            }
////        }
////    }
////
////    public static class ClientSession {
////        public int playerId;
////        public Socket socket;
////        public DataInputStream in;
////        public DataOutputStream out;
////    }
////
////    public void writeStartGameInfo(StartGameInfo info, ClientSession cs) throws IOException {
////        cs.out.writeInt(info.getPlayerId());
////        int[][] maze = info.getMaze();
////        cs.out.writeInt(maze.length);
////        cs.out.writeInt(maze[0].length);
////        for (int i = 0; i < maze.length; i++) {
////            for (int j = 0; j < maze[i].length; j++) {
////                cs.out.writeInt(maze[i][j]);
////            }
////        }
////        cs.out.flush();
////    }
////
////    private void writePlayer(Player player, DataOutputStream out) throws IOException {
////        out.writeInt(player.getId());
////        out.writeUTF(player.getName() != null ? player.getName() : "");
////
////        Map<String, Object> properties = player.getProperties();
////        out.writeInt(properties.size());
////        for (Map.Entry<String, Object> entry : properties.entrySet()) {
////            out.writeUTF(entry.getKey());
////            Object value = entry.getValue();
////            out.writeUTF(value != null ? value.toString() : "");
////        }
////    }
////
////    private void writeSprite(Sprite sprite, DataOutputStream out) throws IOException {
////        out.writeInt(sprite.getId());
////        String kind = sprite.getKind();
////        try {
////            out.writeInt(Integer.parseInt(kind));
////        } catch (NumberFormatException e) {
////            out.writeInt(0);
////        }
////        out.writeUTF(sprite.getName() != null ? sprite.getName() : "");
////        out.writeDouble(sprite.getLocation().getX());
////        out.writeDouble(sprite.getLocation().getY());
////        out.writeDouble(sprite.getDirection());
////        out.writeInt(sprite.getPlayerId());
////
////        Map<String, Object> properties = sprite.getProperties();
////        out.writeInt(properties.size());
////        for (Map.Entry<String, Object> entry : properties.entrySet()) {
////            out.writeUTF(entry.getKey());
////            Object value = entry.getValue();
////            out.writeUTF(value != null ? value.toString() : "");
////        }
////    }
////
////    @Override
////    public void sendModelChanged(DynamicGameModel dynamicGameModel) {
////        for (ClientSession cs : playerToSocketMap.values()) {
////            try {
////                if (!cs.socket.isClosed()) {
////                    cs.out.writeLong(dynamicGameModel.getFrame());
////
////                    List<Player> players = dynamicGameModel.getPlayers();
////                    cs.out.writeInt(players.size());
////                    for (Player player : players) {
////                        writePlayer(player, cs.out);
////                    }
////
////                    List<Sprite> sprites = dynamicGameModel.getSprites();
////                    cs.out.writeInt(sprites.size());
////                    for (Sprite sprite : sprites) {
////                        writeSprite(sprite, cs.out);
////                    }
////                    cs.out.flush();
////                }
////            } catch (IOException e) {
////                System.err.println("Erreur envoi modèle au client " + cs.playerId);
////            }
////        }
////    }
////
////    public void stop() {
////        running = false;
////        if (threadPool != null) {
////            threadPool.shutdown();
////        }
////        if (serverSocket != null) {
////            try {
////                serverSocket.close();
////            } catch (IOException e) {
////                System.err.println("Erreur fermeture server socket: " + e.getMessage());
////            }
////        }
////        for (ClientSession cs : playerToSocketMap.values()) {
////            try {
////                cs.socket.close();
////            } catch (IOException e) {
////            }
////        }
////        playerToSocketMap.clear();
////    }
////}