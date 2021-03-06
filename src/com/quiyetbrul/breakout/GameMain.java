package com.quiyetbrul.breakout;

import java.awt.*;
import java.awt.image.BufferStrategy;

public class GameMain extends Canvas implements Runnable {

    // screen size
    public static final int WINDOW_WIDTH = 640, WINDOW_HEIGHT = WINDOW_WIDTH / 12 * 9; // 16x9 ratio
    private static final long serialVersionUID = 4730308219818161523L;
    public static GAME_STATE gameStart = GAME_STATE.Menu;
    private boolean running = false;
    private Thread thread;
    private Handler handler;
    private GameMenu menu;
    private HUD hud;

    public GameMain() {
        init();

        this.addMouseListener(menu);
        this.addKeyListener(new KeyInput(handler));

        new Window(WINDOW_WIDTH, WINDOW_HEIGHT, "BREAKOUT GAME BY QUIYET BRUL", this);
    }

    public static void main(String[] args) {
        new GameMain();
    }

    // Clamps the elapsed time from the game loop
    /*
     * Without this, objects can possibly be led tunneling through other objects or
     * getting out of bounds
     */
    public static int clamp(int var, int min, int max) {

        // Clamping means literally in this instance.
        //// it's used to restrict a value to a given range
        if (var >= max) {
            return var = max;
        } else if (var <= min) {
            return var = min;
        } else {
            return var;
        }
    }

    private void init() {
        handler = new Handler();
        hud = new HUD();
        menu = new GameMenu(this, handler, hud);
    }

    // initialize multiple thread
    public synchronized void start() {
        thread = new Thread(this);
        thread.start();
        running = true;
    }

    // halts threads, ends the app
    public synchronized void stop() {
        try {
            thread.join();
            running = false;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /*
    // fps for the game that loops over the code

    // research part: renders game as fast as it can
    // resolves large amount of collisions per frame, dragging the performance.
    // calls tick() at a steady frequency to make the game stable

    // improvement: avoid using System.currentTimeMillis() it is susceptible to
    // changing the system clock

    // research other or create game loops that offer variable time step but doesn't
    // wreck game physics computations

    */
    public void run() {
        this.requestFocus();
        long lastTime = System.nanoTime();
        double amountOfTicks = 60.0;
        double ns = 1000000000 / amountOfTicks;
        double delta = 0;
        long timer = System.currentTimeMillis();
        int frames = 0;
        while (running) {
            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;
            while (delta >= 1) {
                tick();
                delta--;
            }
            if (running) {
                render();
            }
            frames++;
            if (System.currentTimeMillis() - timer > 1000) {
                timer += 1000;
                System.out.println("FPS: " + frames);

                frames = 0;
            }
        }
        stop();
    }

    // updates the game at the rate specified in function run()
    private void tick() {
        handler.tick();
        if (gameStart == GAME_STATE.Game) {
            hud.tick();
            menu.tick();

            // determines player lives + game over
            if (HUD.LIVES <= 0) {
                HUD.LIVES = 5;
                gameStart = GAME_STATE.GameOver;
            } else if (gameStart == GAME_STATE.Menu || gameStart == GAME_STATE.GameOver || gameStart == GAME_STATE.GameVictory) {
                menu.tick();
            }
        }
    }

    // renders background, game state
    private void render() {
        final int numBuffers = 3;
        BufferStrategy bs = this.getBufferStrategy(); // starts value at null
        if (bs == null) {
            this.createBufferStrategy(numBuffers); // 3: buffer creations
            return;
        }
        Graphics g = bs.getDrawGraphics();

        g.setColor(Color.black); // stops flashing background
        g.fillRect(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);

        handler.render(g);

        if (gameStart == GAME_STATE.Game) {
            hud.render(g);
        } else if (gameStart == GAME_STATE.Menu || gameStart == GAME_STATE.Help || gameStart == GAME_STATE.GameOver || gameStart == GAME_STATE.GameVictory) {
            menu.render(g);
        }

        g.dispose();
        bs.show();
    }

    public enum GAME_STATE {
        Menu, Help, Game, GameOver, GameVictory
    }
}
