import java.awt.*;
import javax.swing.*;

public class Enemy {
int x;
int y;
int direction;
int speed;
int health;
Image picture;
boolean isBoss;

Enemy(int x, int y, String pictureFile, int enemySpeed){
    this.x = x;
    this.y = y;
    picture = new ImageIcon(pictureFile).getImage();
    speed = enemySpeed;
    health = 40;
    direction = 1;
    isBoss = false;
}

void move(){
    x = x + (direction * speed);
    int screenWidth = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth();
    if(x < 0 || x > screenWidth - 40){
        direction = direction * -1;
        y = y + 20;
    }
}

int getX(){
    return x;
}

int getY(){
    return y;
}

Rectangle getBox() {
    return new Rectangle(x, y, 40, 20);
}

void draw(Graphics g){
  g.drawImage(picture, x, y, 60, 40, null);
}

int getHealth(){
    return health;
}

void loseHealth(){
    health = health - 10;
}

void setHealth(int newHealth){
    health = newHealth;
}

void makeBoss(boolean boss){
    isBoss = boss;
}

boolean isBoss(){
    return isBoss;
}
}
