# 🚀 Java 2D Space Shooter

A high-octane, classic arcade-style space shooter game built from scratch using **Java Swing** and **AWT**. This project emphasizes **Object-Oriented Programming (OOP)** principles to handle game logic, entity management, and rendering.

---

## 🎮 Features

* **Player Controls:** Smooth movement and rapid-fire shooting mechanics.
* **Enemy AI:** Multiple waves of enemies with varied movement patterns.
* **Power-ups:** Collectibles to enhance health, speed, or firepower.
* **Dynamic Graphics:** 2D sprites, background scrolling, and explosion effects.
* **Score System:** Real-time score tracking and high-score persistence.
* **Collision Detection:** Precise hit-box management for bullets, enemies, and players.

---

## 🛠️ Tech Stack & Concepts

* **Language:** Java
* **Framework:** Java Swing & AWT (Abstract Window Toolkit)
* **OOP Principles Used:**
    * **Inheritance:** Base `Entity` class for players and enemies.
    * **Encapsulation:** Private attributes with getters/setters for game state.
    * **Polymorphism:** Method overriding for specific entity behaviors.
    * **Composition:** Handling game levels and sound managers.
* **Game Loop:** Implemented using `javax.swing.Timer` (or a custom Thread) for consistent frame rates.



## 🚀 How to Run

### Prerequisites
* **JDK 11** or higher installed.
* An IDE (IntelliJ IDEA, Eclipse, or NetBeans) or Command Line.

### Steps
1.  **Clone the Repository:**
    ```bash
    git clone [https://github.com/your-username/java-space-shooter.git](https://github.com/your-username/java-space-shooter.git)
    ```
2.  **Compile the Project:**
    ```bash
    javac src/*.java -d bin
    ```
3.  **Run the Game:**
    ```bash
    java -cp bin Main
    ```

---

## 📁 Project Structure

```text
src/
├── entities/       # Player, Enemy, Bullet, and Power-up classes
├── graphics/       # Image loaders and sprite management
├── ui/             # Game panels, menus, and HUD
├── logic/          # Collision detection and Game Engine
└── Main.java       # Game entry point
