import java.awt.*;
import javax.swing.*;

public class Player {
int x;
int y;
int speed;
int health;
Image picture;

Player(int x, int y, String pictureFile){
    this.x = x;
    this.y = y;
    picture = new ImageIcon(pictureFile).getImage();
    health = 100;
    speed = 0;
}

void move(){
    x = x + speed;
    if(x < 0){
        x = 0;
    }
    int screenWidth = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth();
    if(x > screenWidth - 40){
        x = screenWidth - 40;
    }
}

void setSpeed(int newSpeed){
   speed = newSpeed;
}

int getX(){
    return x;
}

int getY(){
    return y;
}

Rectangle getBox(){
    return new Rectangle(x, y, 40, 20);
}

void draw(Graphics g){
    g.drawImage(picture, x, y, 80, 40, null);
}

int getHealth(){
    return health;
}

void loseHealth(){
    health = health - 10;
}

void gainHealth(int amount){
    health = health + amount;
    if(health > 100){
        health = 100;
    }
}
}
