package com.quiyetbrul.breakout;

import static com.quiyetbrul.breakout.GameMain.*;

import java.awt.*;

public class Ball extends GameObject{

    private Handler handler;
    private Player player;
    private HUD hud;

    private final int BALL_WIDTH = 10;
    private final int BALL_HEIGHT = 10;

    public Ball(int x, int y, ID id, Handler handler){
        super(x, y, id);

        this.handler = handler;

        setVelX(5); setVelY(5);
    }

    public Rectangle getBounds(){
        return new Rectangle((int) x_coordinate, (int) y_coordinate, BALL_WIDTH, BALL_HEIGHT);
    }

    @Override
    public void tick() {
        // updates x,y coordinates in accordance to velocity
        x_coordinate += getVelX();
        y_coordinate += getVelY();

        // bounce on LTR edge for now
        bounceOnWall();


        // each time ball goes beyond bottom edge, ball gets destroyed
        // lives > 0 ? add ball
        if(y_coordinate >= WINDOW_HEIGHT) {
            hud.LIVES -= 1;
            handler.removeObject(this);
            if(hud.LIVES > 0)
                handler.addObject(new Ball(BALL_X, BALL_Y, ID.Ball, handler));
            if(gameStart == GAME_STATE.GameOver)
                handler.removeObject(this);
        }
//        bounceOnPaddle();
    }

    private void bounceOnWall(){
        // remove || y_coordinate >= WINDOW_HEIGHT - 50 for the ball not to bounce on bottom edge
        if(y_coordinate <=0 || y_coordinate >= WINDOW_HEIGHT - 50) velY *= -1;
        if(x_coordinate <=0 || x_coordinate >= WINDOW_WIDTH - 20) velX *= -1;
    }

//    private void bounceOnPaddle(){
//        for(int i = 0; i < handler.object.size(); i++){
//            GameObject player = handler.object.get(i);
//
//            if(player.getX_coordinate())
//
//            if(player.getId() == ID.Ball){
//                if(getBounds().intersects(player.getBounds())){
//                    velX *= -1;
//                    velY *= -1;
//                }
//            }
//        }
//    }

    @Override
    public void render(Graphics g) {
        g.fillOval((int) x_coordinate, (int) y_coordinate, BALL_WIDTH, BALL_HEIGHT);
    }
}
