//package net.thevpc.gaming.atom.examples.kombla.main.client.dal;
//
//import net.thevpc.gaming.atom.examples.kombla.main.shared.engine.AppConfig;
//import net.thevpc.gaming.atom.examples.kombla.main.shared.model.StartGameInfo;
//import net.thevpc.gaming.atom.examples.kombla.main.shared.model.DynamicGameModel;
//import net.thevpc.gaming.atom.examples.kombla.main.shared.dal.ProtocolConstants;
//import net.thevpc.gaming.atom.model.Player;
//import net.thevpc.gaming.atom.model.Sprite;
//import net.thevpc.gaming.atom.model.DefaultPlayer;
//import net.thevpc.gaming.atom.model.DefaultSprite;
//
//import java.io.*;
//import java.net.Socket;
//import java.util.HashMap;
//import java.util.Map;
//
//public class TcpMainClientDAO implements MainClientDAO {
//    private MainClientDAOListener listener;
//    private Socket socket;
//    private DataInputStream in;
//    private DataOutputStream out;
//    private boolean running = false;
//
//    @Override
//    public void start(MainClientDAOListener listener, AppConfig properties) {
//        this.listener = listener;
//        new Thread(this::onLoopReceiveModelChanged).start();
//    }
//
//    private void onLoopReceiveModelChanged() {
//        running = true;
//        while (running && socket != null && !socket.isClosed()) {
//            try {
//                DynamicGameModel model = new DynamicGameModel();
//                model.setFrame(in.readLong());
//                int playerCount = in.readInt();
//                Player[] players = new Player[playerCount];
//                for (int i = 0; i < playerCount; i++) {
//                    players[i] = readPlayer();
//                }
//                model.setPlayers(java.util.Arrays.asList(players));
//                int spriteCount = in.readInt();
//                Sprite[] sprites = new Sprite[spriteCount];
//                for (int i = 0; i < spriteCount; i++) {
//                    sprites[i] = readSprite();
//                }
//                model.setSprites(java.util.Arrays.asList(sprites));
//                if (model != null) {
//                    listener.onModelChanged(model);
//                }
//            } catch (IOException e) {
//                if (running) {
//                    System.err.println("Erreur de réception du modèle: ");
//                }
//                break;
//            }
//        }
//    }
//
//    @Override
//    public StartGameInfo connect() {
//        try {
//            socket = new Socket("localhost", 12345);
//            in = new DataInputStream(socket.getInputStream());
//            out = new DataOutputStream(socket.getOutputStream());
//            out.writeInt(ProtocolConstants.CONNECT);
//            out.writeUTF("Mouhaned");
//            return readStartGameInfo();
//        } catch (IOException e) {
//            throw new RuntimeException("Erreur de connexion au serveur", e);
//        }
//    }
//
//    private StartGameInfo readStartGameInfo() throws IOException {
//        int playerId = in.readInt();
//        int rows = in.readInt();
//        int cols = in.readInt();
//        int[][] maze = new int[rows][cols];
//        for (int i = 0; i < rows; i++) {
//            for (int j = 0; j < cols; j++) {
//                maze[i][j] = in.readInt();
//            }
//        }
//
//        return new StartGameInfo(playerId, maze);
//    }
//
//
//
//    private Player readPlayer() throws IOException {
//        int id = in.readInt();
//        String name = in.readUTF();
//        DefaultPlayer player = new DefaultPlayer();
//        player.setId(id);
//        player.setName(name);
//        int propertyCount = in.readInt();
//        for (int i = 0; i < propertyCount; i++) {
//            String key = in.readUTF();
//            String value = in.readUTF();
//            player.setProperty(key, value);
//        }
//        return player;
//    }
//    private Sprite readSprite() throws IOException {
//        int id = in.readInt();
//        String kind = in.readUTF();
//        String name = in.readUTF();
//        double x = in.readDouble();
//        double y = in.readDouble();
//        double direction = in.readDouble();
//        int playerId = in.readInt();
//        DefaultSprite sprite = new DefaultSprite(kind);
//        sprite.setId(id);
//        sprite.setName(name);
//        sprite.setLocation(x, y);
//        sprite.setDirection(direction);
//        sprite.setPlayerId(playerId);
//        int propertyCount = in.readInt();
//        for (int i = 0; i < propertyCount; i++) {
//            String key = in.readUTF();
//            String value = in.readUTF();
//            sprite.setProperty(key, value);
//        }
//        return sprite;
//    }
//
//    @Override
//    public void sendMoveLeft() {
//        if (out != null) {
//            try {
//                out.writeInt(ProtocolConstants.LEFT);
//                out.flush();
//            } catch (IOException e) {
//                System.err.println("Erreur envoi commande LEFT: ");
//            }
//        }
//    }
//
//    @Override
//    public void sendMoveRight() {
//        if (out != null) {
//            try {
//                out.writeInt(ProtocolConstants.RIGHT);
//                out.flush();
//            } catch (IOException e) {
//                System.err.println("Erreur envoi commande RIGHT: ");
//            }
//        }
//    }
//
//    @Override
//    public void sendMoveUp() {
//        if (out != null) {
//            try {
//                out.writeInt(ProtocolConstants.UP);
//                out.flush();
//            } catch (IOException e) {
//                System.err.println("Erreur envoi commande UP: ");
//            }
//        }
//    }
//
//    @Override
//    public void sendMoveDown() {
//        if (out != null) {
//            try {
//                out.writeInt(ProtocolConstants.DOWN);
//                out.flush();
//            } catch (IOException e) {
//                System.err.println("Erreur envoi commande DOWN: ");
//            }
//        }
//    }
//
//    @Override
//    public void sendFire() {
//        if (out != null) {
//            try {
//                out.writeInt(ProtocolConstants.FIRE);
//                out.flush();
//            } catch (IOException e) {
//                System.err.println("Erreur envoi commande FIRE: ");
//            }
//        }
//    }
//
//    public void stop() {
//        running = false;
//        if (socket != null) {
//            try {
//                socket.close();
//            } catch (IOException e) {
//                System.err.println("Erreur fermeture socket client: ");
//            }
//        }
//    }
//}