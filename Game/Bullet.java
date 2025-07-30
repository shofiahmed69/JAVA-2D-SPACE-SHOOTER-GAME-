import java.awt.*;

public class Bullet {
int x;
int y;
boolean isEnemyLaser;

Bullet(int x, int y, boolean isEnemy){
    this.x = x;
    this.y = y;
    this.isEnemyLaser = isEnemy;
}

void move(){
    if(isEnemyLaser){
        y = y + 5;
    }
    else {
        y = y - 5;
    }
}

int getY(){
    return y;
}

Rectangle getBox() {
    return new Rectangle(x, y, 5, 10);
}

void draw(Graphics g){
    if(isEnemyLaser){
        g.setColor(Color.RED);
    }
    else {
        g.setColor(Color.YELLOW);
    }
    g.fillRect(x, y, 5, 10);
}
}
