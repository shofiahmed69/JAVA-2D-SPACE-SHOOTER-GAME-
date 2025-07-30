import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class NetworkManager {
    private Socket socket;
    private ServerSocket serverSocket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private boolean isServer;
    private boolean isConnected;
    private GameState gameState;
    
    public NetworkManager() {
        isConnected = false;
        gameState = new GameState();
    }
    
    public boolean startServer(int port) {
        try {
            serverSocket = new ServerSocket(port);
            isServer = true;
            System.out.println("Server started on port " + port);
            return true;
        } catch (IOException e) {
            System.out.println("Server start error: " + e);
            return false;
        }
    }
    
    public boolean waitForClient() {
        try {
            socket = serverSocket.accept();
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            isConnected = true;
            System.out.println("Client connected!");
            return true;
        } catch (IOException e) {
            System.out.println("Client connection error: " + e);
            return false;
        }
    }
    
    public boolean connectToServer(String host, int port) {
        try {
            socket = new Socket(host, port);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            isServer = false;
            isConnected = true;
            System.out.println("Connected to server!");
            return true;
        } catch (IOException e) {
            System.out.println("Server connection error: " + e);
            return false;
        }
    }
    
    public void sendGameState(GameState state) {
        if (!isConnected) return;
        try {
            out.writeObject(state);
            out.flush();
        } catch (IOException e) {
            System.out.println("Send error: " + e);
        }
    }
    
    public GameState receiveGameState() {
        if (!isConnected) return null;
        try {
            return (GameState) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Receive error: " + e);
            return null;
        }
    }
    
    public boolean isConnected() {
        return isConnected;
    }
    
    public boolean isServer() {
        return isServer;
    }
    
    public void disconnect() {
        try {
            if (socket != null) socket.close();
            if (serverSocket != null) serverSocket.close();
            isConnected = false;
        } catch (IOException e) {
            System.out.println("Disconnect error: " + e);
        }
    }
}

class GameState implements Serializable {
    public int player1X, player1Y, player1Health;
    public int player2X, player2Y, player2Health;
    public ArrayList<BulletData> bullets;
    public ArrayList<EnemyData> enemies;
    public int score, level;
    public boolean gameRunning;
    
    public GameState() {
        bullets = new ArrayList<>();
        enemies = new ArrayList<>();
    }
}

class BulletData implements Serializable {
    public int x, y;
    public boolean isEnemy;
    
    public BulletData(int x, int y, boolean isEnemy) {
        this.x = x;
        this.y = y;
        this.isEnemy = isEnemy;
    }
}

class EnemyData implements Serializable {
    public int x, y, health;
    public boolean isBoss;
    
    public EnemyData(int x, int y, int health, boolean isBoss) {
        this.x = x;
        this.y = y;
        this.health = health;
        this.isBoss = isBoss;
    }
}

