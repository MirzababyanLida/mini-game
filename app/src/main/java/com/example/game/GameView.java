package com.example.game;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Handler;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;

import java.util.Random;




    public class GameView extends View {

        int dWidth, dHeight;
        Bitmap trash, hand, plastic;
        Handler handler;
        Runnable runnable;
        long UPDATE_MILLIS = 30;
        int handX, handY;
        int plasticX, plasticY;
        Random random;
        boolean plasticAnimation = false;
        int points = 0;
        float TEXT_SIZE = 120;
        Paint textPaint;
        Paint healthPaint;
        int life = 3;
        Context context;
        int handSpeed;
        int trashX, trashY;


        public GameView(Context context) {
            super(context);
            this.context = context;
            Display display = ((Activity) getContext()).getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            dWidth = size.x;
            dHeight = size.y;

            plastic = BitmapFactory.decodeResource(getResources(), R.drawable.plastic);
            trash = BitmapFactory.decodeResource(getResources(), R.drawable.trash);
            hand = BitmapFactory.decodeResource(getResources(), R.drawable.hand);
            handler = new Handler();
            runnable = new Runnable() {
                @Override
                public void run() {
                    invalidate();
                }
            };
            random = new Random();
            handX = dWidth + random.nextInt(300);
            handY = random.nextInt(600);
            plasticX = handX;
            plasticY = handY + hand.getHeight() - 30;
            textPaint = new Paint();
            textPaint.setColor(Color.rgb(255, 0, 0));
            textPaint.setTextSize(TEXT_SIZE);
            textPaint.setTextAlign(Paint.Align.LEFT);
            healthPaint = new Paint();
            healthPaint.setColor(Color.GREEN);
            handSpeed = 21 + random.nextInt(30);
            trashX = dWidth/2 - trash.getWidth()/2;
            trashY = dHeight - trash.getHeight();





        }
        boolean plasticInTrash = false;

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            canvas.drawColor(Color.BLUE);

            if (!plasticAnimation) {
                handX -= handSpeed;
                plasticX -= handSpeed;
            }

            if (handX <= -hand.getWidth()) {
                resetHandAndPlastic();
                if (!plasticInTrash) {
                    life--;
                    if (life == 0) {
                        gameOver();
                        return; // Exit the method to prevent further drawing
                    }
                }
                plasticInTrash = false;
            }

            if (plasticAnimation) {
                plasticY += 40;
                if (plasticY + plastic.getHeight() >= dHeight) {
                    resetHandAndPlastic();
                    if (!plasticInTrash) {
                        life--;
                        if (life == 0) {
                            gameOver();
                            return; // Exit the method to prevent further drawing
                        }
                    }
                    plasticInTrash = false;
                }
            }

            // Draw game elements
            canvas.drawBitmap(trash, trashX, trashY, null);
            canvas.drawBitmap(hand, handX, handY, null);
            canvas.drawBitmap(plastic, plasticX, plasticY, null);
            canvas.drawText("" + points, 20, TEXT_SIZE, textPaint);
            if (life == 2)
                healthPaint.setColor(Color.YELLOW);
            else if (life == 1)
                healthPaint.setColor(Color.RED);
            canvas.drawRect(dWidth - 200, 30, dWidth - 200 * 60 * life, 80, healthPaint);
            if (life != 0)
                handler.postDelayed(runnable, UPDATE_MILLIS);
        }


        private void resetHandAndPlastic() {
            handX = dWidth + random.nextInt(300);
            handY = random.nextInt(600);
            plasticX = handX;
            plasticY = handY + hand.getHeight() - 30;
            plasticAnimation = false;
        }



        private void gameOver() {
            Intent intent = new Intent(context, GameOver.class);
            intent.putExtra("points", points);
            context.startActivity(intent);
            ((Activity) context).finish();
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float touchX = event.getX();
            float touchY = event.getY();
            if (event.getAction() == MotionEvent.ACTION_DOWN && life > 0) {
                if (!plasticAnimation &&
                        (touchX >= handX && touchX <= (handX + hand.getWidth())
                                && touchY >= handY && touchY <= (handY + hand.getHeight()))) {
                    plasticAnimation = true;
                    points++; // Increment points
                    plasticInTrash = true; // Set plasticInTrash to true
                    return true; // Touch event handled
                }
            }
            return super.onTouchEvent(event);
        }


    }



