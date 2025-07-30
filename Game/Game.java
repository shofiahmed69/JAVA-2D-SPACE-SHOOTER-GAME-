import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.sound.sampled.*;
import java.io.File;

public class Game extends JPanel implements ActionListener, KeyListener{
    Timer gameTimer;
    Player player1;
    Player player2;
    boolean isMultiplayer;
    boolean isLANMode;
    NetworkManager networkManager;
    ArrayList<Enemy> enemies;
    ArrayList<Bullet> playerLasers;
    ArrayList<Bullet> enemyLasers;
    int level;
    int score;
    Image backgroundPicture;
    Image scaledBackground;
    boolean isGameRunning;
    int enemySpeed;
    int difficulty;
    boolean soundOn;
    Clip music;
    boolean bossExists;
    Enemy boss;
    Image menuPicture;
    int screenWidth;
    int screenHeight;
    Random random;
    JFrame parentWindow;
    boolean showCharacterSelection;
    int selectedShip1 = 0;
    int selectedShip2 = 0;
    String[] shipImages = {
        "C:\\Users\\sayak\\Downloads\\Game\\images\\download-removebg-preview.png",
        "C:\\Users\\sayak\\Downloads\\Game\\images\\47-477586_spaceship-sprite-transparent-background-hd-png-download-removebg-preview-removebg-preview.png", 
        "C:\\Users\\sayak\\Downloads\\Game\\images\\png-transparent-spacecraft-sprite-spaceshipone-computer-software-space-invaders-angle-monochrome-symmetry-removebg-preview.png"
    };
    String[] shipNames = {"Fighter", "Cruiser", "Destroyer"};
    
    Game(){
        screenWidth = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth();
        screenHeight = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight();
        setFocusable(true);
        setPreferredSize(new Dimension(screenWidth, screenHeight));
        addKeyListener(this);
        backgroundPicture = new ImageIcon("C:\\Users\\sayak\\Downloads\\game 2\\images\\e23703e9-5ecc-4a38-9475-6d03fc6e4581_scaled.jpg").getImage();
        scaledBackground = backgroundPicture.getScaledInstance(screenWidth, screenHeight, Image.SCALE_SMOOTH);
        menuPicture = new ImageIcon("C:\\Users\\sayak\\Downloads\\game 2\\images\\RQ03IX.png").getImage();
        player1 = new Player(screenWidth / 2, screenHeight - 100, shipImages[0]);
        player2 = new Player(screenWidth / 2 - 100, screenHeight - 100, shipImages[0]);
        isMultiplayer = false;
        isLANMode = false;
        networkManager = new NetworkManager();
        enemies = new ArrayList<>();
        playerLasers = new ArrayList<>();
        enemyLasers = new ArrayList<>();
        level = 1;
        score = 0;
        enemySpeed = 1;
        isGameRunning = false;
        difficulty = 1;
        soundOn = true;
        bossExists = false;
        showCharacterSelection = false;
        random = new Random();
        playMusic("C:\\Users\\sayak\\Downloads\\game 2\\game-music-loop-6-144641.wav");
    }
    
    public void setParentWindow(JFrame window) {
        this.parentWindow = window;
    }
    
    void minimizeWindow() {
        if (parentWindow != null) {
            parentWindow.setState(JFrame.ICONIFIED);
        }
    }
    
    void playMusic(String musicFile){
      if(!soundOn){
        return;
      }
      try{
        File file = new File(musicFile);
        AudioInputStream audio = AudioSystem.getAudioInputStream(file);
        music = AudioSystem.getClip();
        music.open(audio);
        music.loop(Clip.LOOP_CONTINUOUSLY);
      }
      catch(Exception e){
        System.out.println("Music error: " + e);
      }
    }
    
    void startGame(){
        makeEnemies();
        gameTimer = new Timer(25, this);
        gameTimer.start();
        isGameRunning = true;
    }
    
    void startLANGame(boolean isServer) {
        if (isServer) {
            if (networkManager.startServer(12345)) {
                JOptionPane.showMessageDialog(this, "Waiting for client to connect...");
                new Thread(() -> {
                    if (networkManager.waitForClient()) {
                        SwingUtilities.invokeLater(() -> {
                            removeAll();
                            isLANMode = true;
                            isMultiplayer = true;
                            startGame();
                        });
                    }
                }).start();
            }
        } else {
            String host = JOptionPane.showInputDialog("Enter server IP address:");
            if (host != null && !host.trim().isEmpty()) {
                new Thread(() -> {
                    if (networkManager.connectToServer(host.trim(), 12345)) {
                        SwingUtilities.invokeLater(() -> {
                            removeAll();
                            isLANMode = true;
                            isMultiplayer = true;
                            startGame();
                        });
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed to connect to server!");
                    }
                }).start();
            }
        }
    }
    
    void makeEnemies(){
        enemies.clear();
        for (int i = 0; i < level * 5 ; i++){
            enemies.add(new Enemy(100 + (i * 50) % (screenWidth - 100), 50 + (i / 10) * 50, "C:\\Users\\sayak\\Downloads\\game 2\\images\\download__1_-removebg-preview.png", enemySpeed));
        }
    }
    
    void makeBoss(){
        boss = new Enemy(screenWidth / 2, 50, "C:\\Users\\sayak\\Downloads\\game 2\\images\\33ebc2c2a8ca24a8e96f269fdc352e38-removebg-preview.png", 2);
        boss.setHealth(1000 * level);
        boss.makeBoss(true);
        bossExists = true;
    }
    
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        if (showCharacterSelection) {
            drawCharacterSelection(g);
        } else if(isGameRunning){
            g.drawImage(scaledBackground, 0, 0, null);
            player1.draw(g);
            if(isMultiplayer){
                player2.draw(g);
            }
            for(Bullet laser : playerLasers){
                laser.draw(g);
            }
            for(Bullet laser : enemyLasers){
                laser.draw(g);
            }
            for(Enemy enemy : enemies){
                enemy.draw(g);
            }
            if(bossExists){
                boss.draw(g);
            }
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 18));
            g.drawString("Player 1 health: " + player1.getHealth(), 10, 20);
            if(isMultiplayer) {
            g.drawString("player 2 health: " + player2.getHealth(), 10, 45);
            }
          g.drawString("Score: " + score, screenWidth - 100, 20);
          g.drawString("level: " + level, screenWidth / 2 - 30, 20);
          
          if (isLANMode) {
              g.drawString("LAN Mode", 10, 70);
          }
        }
        else {
            drawMenu(g);
        }
    }
    
    void drawCharacterSelection(Graphics g) {
        g.drawImage(menuPicture, 0, 0, screenWidth, screenHeight, null);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 36));
        g.drawString("Choose Your Spaceship", screenWidth / 2 - 200, 100);
        
        setLayout(null);
        
        g.setFont(new Font("Arial", Font.BOLD, 24));
        g.drawString("Player 1", screenWidth / 4 - 50, 200);
        
        for (int i = 0; i < 3; i++) {
            JButton shipButton = new JButton(shipNames[i]);
            shipButton.setBounds(screenWidth / 4 - 75, 250 + i * 80, 150, 60);
            if (i == selectedShip1) {
                shipButton.setBackground(Color.white);
            }
            final int shipIndex = i;
            shipButton.addActionListener(e -> {
                selectedShip1 = shipIndex;
                player1 = new Player(screenWidth / 2, screenHeight - 100, shipImages[selectedShip1]);
                repaint();
            });
            add(shipButton);
        }
        if (isMultiplayer && !isLANMode) {
            g.drawString("Player 2", 3 * screenWidth / 4 - 50, 200);
            
            for (int i = 0; i < 3; i++) {
                JButton shipButton = new JButton(shipNames[i]);
                shipButton.setBounds(3 * screenWidth / 4 - 75, 250 + i * 80, 150, 60);
                if (i == selectedShip2) {
                    shipButton.setBackground(Color.white);
                }
                final int shipIndex = i;
                shipButton.addActionListener(e -> {
                    selectedShip2 = shipIndex;
                    player2 = new Player(screenWidth / 2 - 100, screenHeight - 100, shipImages[selectedShip2]);
                    repaint();
                });
                add(shipButton);
            }
        }
        
        JButton startButton = new JButton("Start Game");
        startButton.setBounds(screenWidth / 2 - 100, screenHeight - 200, 200, 60);
        startButton.addActionListener(e -> {
            removeAll();
            showCharacterSelection = false;
            enemySpeed = difficulty;
            startGame();
        });
        add(startButton);
        
        JButton backButton = new JButton("Back");
        backButton.setBounds(screenWidth / 2 - 100, screenHeight - 120, 200, 60);
        backButton.addActionListener(e -> {
            removeAll();
            showCharacterSelection = false;
            repaint();
        });
        add(backButton);
    }
    
    void drawMenu(Graphics g){
     g.drawImage(menuPicture, 0, 0, screenWidth, screenHeight, null);
     g.setColor(Color.WHITE);
     g.setFont(new Font("Arial", Font.BOLD, 36));
     g.drawString("Galactic Ascent", screenWidth / 2 - 100, 50);
     setLayout(null);
      
     JButton startButton = new JButton("Start Game");
     startButton.setBounds(screenWidth / 2 - 100, screenHeight / 2 - 300, 200, 50);
     startButton.addActionListener(e -> {
        removeAll();
        isMultiplayer = false;
        isLANMode = false;
        showCharacterSelection = true;
        repaint();
     });
     add(startButton);
     
     JButton multiplayerButton = new JButton("Local Multiplayer");
        multiplayerButton.setBounds(screenWidth / 2 - 100, screenHeight / 2 - 230, 200, 50);
        multiplayerButton.addActionListener(e -> {
            removeAll();
            isMultiplayer = true;
            isLANMode = false;
            showCharacterSelection = true;
            repaint();
        });
        add(multiplayerButton);
        JButton lanHostButton = new JButton("LAN Host");
        lanHostButton.setBounds(screenWidth / 2 - 100, screenHeight / 2 - 160, 200, 50);
        lanHostButton.addActionListener(e -> {
            removeAll();
            showCharacterSelection = true;
            isMultiplayer = true;
            isLANMode = true;
            repaint();
            startLANGame(true);
        });
        add(lanHostButton);
        
        JButton lanJoinButton = new JButton("LAN Join");
        lanJoinButton.setBounds(screenWidth / 2 - 100, screenHeight / 2 - 90, 200, 50);
        lanJoinButton.addActionListener(e -> {
            startLANGame(false);
        });
        add(lanJoinButton);

        JButton easyButton = new JButton("Easy");
        easyButton.setBounds(screenWidth / 2 - 100, screenHeight / 2 - 20, 200, 50);
        easyButton.addActionListener(e -> difficulty = 1);
        add(easyButton);

        JButton normalButton = new JButton("Normal");
        normalButton.setBounds(screenWidth / 2 - 100, screenHeight / 2 + 50, 200, 50);
        normalButton.addActionListener(e -> difficulty = 2);
        add(normalButton);

        JButton hardButton = new JButton("Hard");
        hardButton.setBounds(screenWidth / 2 - 100, screenHeight / 2 + 120, 200, 50);
        hardButton.addActionListener(e -> difficulty = 4);
        add(hardButton);

        JButton soundButton = new JButton(soundOn ? "Sound: On" : "Sound: Off");
        soundButton.setBounds(screenWidth / 2 - 100, screenHeight / 2 + 190, 200, 50);
        soundButton.addActionListener(e -> {
            soundOn = !soundOn; 
            soundButton.setText(soundOn ? "Sound: On" : "Sound: Off");
            if (!soundOn && music != null && music.isRunning()) {
                music.stop(); 
            } else if (soundOn) {
                playMusic("C:\\Users\\sayak\\Downloads\\game 2\\game-music-loop-6-144641.wav"); 
            }
        });
        add(soundButton);

        JButton exitButton = new JButton("Exit");
        exitButton.setBounds(screenWidth / 2 - 100, screenHeight / 2 + 260, 200, 50);
        exitButton.addActionListener(e -> System.exit(0)); 
        add(exitButton);

        repaint();
     }
      
     void playSound(String soundFile){
        if(!soundOn){
            return;
        }
        try{
            File file = new File(soundFile);
            AudioInputStream audio = AudioSystem.getAudioInputStream(file);
            Clip clip = AudioSystem.getClip();
            clip.open(audio);
            clip.start(); 
        }
        catch(Exception e){
            System.out.println("Sound error: " + e);
        }
      }
        
    void syncNetworkGame() {
          if (!isLANMode || !networkManager.isConnected()) return;
          
          if (networkManager.isServer()) {
             
              GameState state = new GameState();
              state.player1X = player1.getX();
              state.player1Y = player1.getY();
              state.player1Health = player1.getHealth();
              state.player2X = player2.getX();
              state.player2Y = player2.getY();
              state.player2Health = player2.getHealth();
              state.score = score;
              state.level = level;
              state.gameRunning = isGameRunning;
              
            
              for (Bullet bullet : playerLasers) {
                  state.bullets.add(new BulletData(bullet.x, bullet.y, bullet.isEnemyLaser));
              }
              for (Bullet bullet : enemyLasers) {
                  state.bullets.add(new BulletData(bullet.x, bullet.y, bullet.isEnemyLaser));
              }
              
              for (Enemy enemy : enemies) {
                  state.enemies.add(new EnemyData(enemy.getX(), enemy.getY(), enemy.getHealth(), enemy.isBoss()));
              }
              
              networkManager.sendGameState(state);
          } else {
              
              GameState state = networkManager.receiveGameState();
              if (state != null) {
                  
                  player1.x = state.player1X;
                  player1.y = state.player1Y;
                  player2.x = state.player2X;
                  player2.y = state.player2Y;
                  score = state.score;
                  level = state.level;
                  isGameRunning = state.gameRunning;
              }
          }
      }
        
    public void actionPerformed(ActionEvent e){
        player1.move();
        if(isMultiplayer){
            player2.move();
        }
        
        if (isLANMode) {
            syncNetworkGame();
        }
        
        ArrayList<Bullet> lasersToRemove = new ArrayList<>();
        for(Bullet laser : playerLasers){
            laser.move();
            if(laser.getY() < 0){
                lasersToRemove.add(laser);
            }
        }
        playerLasers.removeAll(lasersToRemove);
        
        lasersToRemove.clear();
        for(Bullet laser : enemyLasers){
            laser.move();
            if(laser.getY() > screenHeight){
                lasersToRemove.add(laser);
            }
            else if(laser.getBox().intersects(player1.getBox())){
                lasersToRemove.add(laser);
                player1.loseHealth();
                if(player1.getHealth() <= 0 && (!isMultiplayer || player2.getHealth() <= 0)){
                    isGameRunning = false;
                    gameTimer.stop();
                }
            }
            else if(isMultiplayer && laser.getBox().intersects(player2.getBox())){
                lasersToRemove.add(laser);
                player2.loseHealth();
                if(player2.getHealth() <= 0 && player1.getHealth() <= 0){
                    isGameRunning = false;
                    gameTimer.stop();
                }
            }
        }
        enemyLasers.removeAll(lasersToRemove);
        
        for (Enemy enemy : enemies) {
            enemy.move();
            if (random.nextDouble() < 0.01 * enemySpeed) {
                enemyLasers.add(new Bullet(enemy.getX() + 20, enemy.getY() + 20, true));
            }
        }
        
        if (bossExists) {
            boss.move();
            if (random.nextDouble() < 0.1) {
                enemyLasers.add(new Bullet(boss.getX() + 50, boss.getY() + 50, true)); 
            }
            if (random.nextDouble() < 0.2) {
                enemies.add(new Enemy(boss.getX(), boss.getY() + 50, 
                    "C:\\Users\\sayak\\Downloads\\game 2\\images\\download__1_-removebg-preview.png", enemySpeed));
            }
        }
        
        checkHits();
        
        if (enemies.isEmpty() && !bossExists) {
            score = score + (level * 100); 
            level = level + 1; 
            if (level > 3 && level % 3 == 1) {
                makeBoss(); 
            } else {
                makeEnemies(); 
            }
        }
        
        repaint();
      }
        
    void checkHits(){
        ArrayList<Bullet> lasersToRemove = new ArrayList<>();
        ArrayList<Enemy> enemiesToRemove = new ArrayList<>();
        
        for (Bullet laser : playerLasers) {
            boolean laserHit = false;
            for (Enemy enemy : enemies) {
                if (laser.getBox().intersects(enemy.getBox())) {
                    lasersToRemove.add(laser); 
                    laserHit = true;
                    enemy.loseHealth();
                    if (enemy.getHealth() <= 0) {
                        enemiesToRemove.add(enemy); 
                        score = score + 50; 
                    }
                    playSound("C:\\Users\\sayak\\Downloads\\game 2\\laser-pistol-shooting-95497.wav"); 
                    break;
                }
            }
            if (!laserHit && bossExists && laser.getBox().intersects(boss.getBox())) {
                lasersToRemove.add(laser); 
                boss.loseHealth();
                playSound("C:\\Users\\sayak\\Downloads\\game 2\\laser-pistol-shooting-95497.wav"); 
                if (boss.getHealth() <= 0) {
                    bossExists = false; 
                    score = score + (1000 * level); 
                }
            }
        }
        playerLasers.removeAll(lasersToRemove);
        enemies.removeAll(enemiesToRemove);
    }

public void keyPressed(KeyEvent e) {
    if(e.getKeyCode() == KeyEvent.VK_ESCAPE){
        if (isLANMode) {
            networkManager.disconnect();
        }
        System.exit(0);
    }
    
    if (e.getKeyCode() == KeyEvent.VK_Q) {
        minimizeWindow();
    }
    
    if (e.getKeyCode() == KeyEvent.VK_LEFT) {
        player1.setSpeed(-5); 
    } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
        player1.setSpeed(5); 
    } else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
        playerLasers.add(new Bullet(player1.getX() + 15, player1.getY(), false)); 
        playSound("C:\\Users\\sayak\\Downloads\\game 2\\laser-pistol-shooting-95497.wav");
    }
    
    if (isMultiplayer && !isLANMode) {
        if (e.getKeyCode() == KeyEvent.VK_A) {
            player2.setSpeed(-5); 
        } else if (e.getKeyCode() == KeyEvent.VK_S) {
            player2.setSpeed(5); 
        } else if (e.getKeyCode() == KeyEvent.VK_W) {
            playerLasers.add(new Bullet(player2.getX() + 15, player2.getY(), false)); 
            playSound("C:\\Users\\sayak\\Downloads\\game 2\\laser-pistol-shooting-95497.wav");
        }
    }
}

public void keyReleased(KeyEvent e) {
    if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_RIGHT) {
        player1.setSpeed(0);
    }
    if (isMultiplayer && !isLANMode && (e.getKeyCode() == KeyEvent.VK_A || e.getKeyCode() == KeyEvent.VK_S)) {
        player2.setSpeed(0);
    }
}

public void keyTyped(KeyEvent e) {}

public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
        JFrame window = new JFrame("Space Shooter");
        Game game = new Game();
        game.setParentWindow(window);
        
        window.add(game);
        window.setExtendedState(JFrame.MAXIMIZED_BOTH); 
        window.setUndecorated(true); 
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setVisible(true); 
    });
}
}