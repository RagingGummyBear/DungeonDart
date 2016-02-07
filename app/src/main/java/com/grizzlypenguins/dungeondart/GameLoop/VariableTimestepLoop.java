package com.grizzlypenguins.dungeondart.GameLoop;

import android.graphics.Canvas;
import android.view.View;

import com.grizzlypenguins.dungeondart.Activities.GamePanel;

/**
 * Created by Darko on 06.02.2016.
 */
public class VariableTimestepLoop extends Thread{

    public boolean gameRunning = false ;
    GamePanel view = null;

    public VariableTimestepLoop(GamePanel view) {
        this.view = view;
    }

    public void gameLoop()
    {
        long lastLoopTime = System.nanoTime();
        final int TARGET_FPS = 60;
        final long OPTIMAL_TIME = 1000000000 / TARGET_FPS;
        long lastFpsTime = 0;
        int fps = 0;

        // keep looping round til the game ends
        while (gameRunning)
        {
            // work out how long its been since the last update, this
            // will be used to calculate how far the entities should
            // move this loop
            long now = System.nanoTime();
            long updateLength = now - lastLoopTime;
            lastLoopTime = now;
            double delta = updateLength / ((double)OPTIMAL_TIME);

            // update the frame counter
            lastFpsTime += updateLength;
            fps++;

            // update our FPS counter if a second has passed since
            // we last recorded
            if (lastFpsTime >= 1000000000)
            {
                System.out.println("(FPS: "+fps+")");
                lastFpsTime = 0;
                fps = 0;
            }

            // update the game logic
            view.tick(delta);
            //doGameUpdates(delta);

            // draw everyting
            render();


            // we want each frame to take 10 milliseconds, to do this
            // we've recorded when we started the frame. We add 10 milliseconds
            // to this and then factor in the current time to give
            // us our final value to wait for
            // remember this is in ms, whereas our lastLoopTime etc. vars are in ns.
            try {
                Thread.sleep( (lastLoopTime-System.nanoTime() + OPTIMAL_TIME)/1000000 );
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    synchronized void  render()
    {
      //  finished = false;
        Canvas c = null;
        try {
            c = view.getHolder().lockCanvas();  //gets the canvas from the GamePanel ( since GamePanel is SurfaceView )
            //  c = view.getHolder().lockCanvas();
            synchronized ( view.getHolder()) {
                if( c != null ){
                    view.draw(c);  // draw == render
                }
            }
        } finally {
            if (c != null) {
                view.getHolder().unlockCanvasAndPost(c);
            }
        }
       // finished = true;
    }

    @Override
    public void run() {
        gameLoop();
    }
}
