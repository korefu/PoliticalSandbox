package com.wowloltech.politicalsandbox;


import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.v4.view.ScaleGestureDetectorCompat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import java.util.List;

public class GameView extends View {
    public Path provincePath;
    public float size = 100;
    List<Province> neighbours;
    Paint p;
    Paint pBlack;
    Paint pText;
    StringBuilder name;
    private float deltaX = 0;
    private float deltaY = 0;
    private Province selectedProvince;
    private GameActivity activity;
    private Game game;
    private int selectedX;
    private int selectedY;
    private boolean isArmyMoving = false;
    private Army movingArmy = null;
    private GestureDetector gestureDetector;
    private ScaleGestureDetector mScaleDetector;
    private float mScaleFactor = 1.f;

    public GameView(GameActivity activity, Game game) {
        super(activity);
        this.activity = activity;
        name = new StringBuilder();
        this.game = game;
        p = new Paint();
        pBlack = new Paint();
        pText = new Paint();
        pText.setColor(Color.BLACK);
        pText.setTextAlign(Paint.Align.CENTER);
        pText.setTextSize(25f);
        pBlack.setColor(Color.BLACK);
        pBlack.setStrokeWidth(2);
        pBlack.setStyle(Paint.Style.STROKE);
        provincePath = new Path();
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        createProvincePath(provincePath);

        gestureDetector = new GestureDetector(activity, new MyGestureListener());
        mScaleDetector = new ScaleGestureDetector(activity, new ScaleListener());
        ScaleGestureDetectorCompat.setQuickScaleEnabled(mScaleDetector, false);
    }

    public Army getMovingArmy() {
        return movingArmy;
    }

    public void setMovingArmy(Army movingArmy) {
        this.movingArmy = movingArmy;
    }

    public void setIsArmyMoving(boolean armyMoving) {
        this.isArmyMoving = armyMoving;
    }

    public boolean isArmyMoving() {
        return isArmyMoving;
    }

    public Province getSelectedProvince() {
        return selectedProvince;
    }

    public void setSelectedProvince(Province selectedProvince) {
        this.selectedProvince = selectedProvince;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mScaleDetector.onTouchEvent(event);
        gestureDetector.onTouchEvent(event);
        return true;
    }

    private void createProvincePath(Path provincePath) {
        provincePath.moveTo((float) 0, (float) (size * 0.5));
        provincePath.lineTo((float) (size * Math.sqrt(3) * 0.5), (float) (0));
        provincePath.lineTo((float) (size * Math.sqrt(3)), (float) (size * 0.5));
        provincePath.lineTo((float) (size * Math.sqrt(3)), (float) (size * 1.5));
        provincePath.lineTo((float) (size * Math.sqrt(3) * 0.5), size * 2);
        provincePath.lineTo((float) 0, (float) (size * 1.5));
        provincePath.close();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        canvas.scale(mScaleFactor, mScaleFactor);
        canvas.drawColor(Color.WHITE);
        for (int y = 0; y < Map.getHeight(); y++)
            for (int x = 0; x < Map.getWidth(); x++)
                drawPathByProvince(canvas, Map.getProvinces()[y][x]);
        canvas.restore();
    }

    public Province findProvinceByTouch(float x, float y) {
        float trueX = (x + deltaX) / mScaleFactor;
        float trueY = (y + deltaY) / mScaleFactor;
        float tempY = (int) trueY % (int) (size * 3);
        if ((tempY <= size * 1.5 && tempY >= size * 0.5) || (tempY >= size * 2))
            selectedY = (int) Math.floor((trueY) / (size * 1.5));
        else {
            int tempX = (int) trueX % (int) (size * Math.sqrt(3));
            final float halfWidth = (float) (size * Math.sqrt(3) / 2);
            if (tempY < size * 0.5) {
                if (tempX > halfWidth)
                    selectedY = (int) Math.floor((trueY - (size * 0.5 * (tempX - halfWidth) / halfWidth)) / (size * 1.5));
                else
                    selectedY = (int) Math.floor((trueY - (size * 0.5 * (halfWidth - tempX) / halfWidth)) / (size * 1.5));
            } else if (tempY > size * 1.5 && tempY < size * 2) {
                if (tempX > halfWidth)
                    selectedY = (int) Math.floor((trueY - (size * 0.5 * (size * Math.sqrt(3) - tempX) / halfWidth)) / (size * 1.5));
                else
                    selectedY = (int) Math.floor((trueY - (size * 0.5 * tempX / halfWidth)) / (size * 1.5));
            }
        }
        if (selectedY % 2 == 0)
            selectedX = (int) Math.floor(trueX / (int) (Math.sqrt(3) * size));
        else
            selectedX = (int) Math.floor((trueX - size * Math.sqrt(3) / 2) / (Math.sqrt(3) * size));
        try {
            return Map.getProvinces()[selectedY][selectedX];
        } catch (Exception e) {
        }
        return null;
    }

    private void drawPathByProvince(Canvas canvas, Province province) {
        if (province.getType() != Province.Type.VOID) {
            name.append(String.valueOf(province.getIncome()));
            for (Player p : game.getPlayers())
                for (Army a : p.getArmies())
                    if (a.getLocation().getId() == province.getId())
                        name.append(" " + String.valueOf(a.getStrength()));
            if (!province.getSelected())
                p.setColor(Map.getColor(province.getOwner().getId()));
            else
                p.setColor(Map.getColor(province.getOwner().getId()) - 0x66000000);

            if (province.getY() % 2 == 1) {
                provincePath.offset((float) (size * Math.sqrt(3) * province.getX() + size * Math.sqrt(3) * 0.5), (float) (size * 1.5 * province.getY()));
                canvas.drawPath(provincePath, p);
                canvas.drawText(name.toString(), (float) (size * Math.sqrt(3) * province.getX() + size * Math.sqrt(3)), (float) (pText.getTextSize() / 2 + size + size * 1.5 * province.getY()), pText);

            } else {
                provincePath.offset((float) (size * Math.sqrt(3) * province.getX()), (float) (size * 1.5 * province.getY()));
                canvas.drawPath(provincePath, p);
                canvas.drawText(name.toString(), (float) (size * Math.sqrt(3) * province.getX() + size * Math.sqrt(3) * 0.5), (float) (size + pText.getTextSize() / 2 + size * 1.5 * province.getY()), pText);
            }
            if (mScaleFactor > 0.5f)
                canvas.drawPath(provincePath, pBlack);

            provincePath.offset(-(float) (size * Math.sqrt(3) * province.getX()), -(float) (size * 1.5 * province.getY()));
            if (province.getY() % 2 == 1) {
                provincePath.offset(-(float) (size * Math.sqrt(3) * 0.5), 0);
            }
            name.delete(0, name.length());
        }
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            mScaleFactor *= detector.getScaleFactor();
            mScaleFactor = Math.max(0.25f, Math.min(mScaleFactor, 2f));
            invalidate();
            return true;
        }
    }

    private class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            scrollBy((int) distanceX, (int) distanceY);
            deltaX += (int) (distanceX);
            deltaY += (int) (distanceY);
            return true;
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            //Toast.makeText(MainActivity.this,"onTouch " + e.getX() + " " + e.getY(),Toast.LENGTH_SHORT).show();
            Province province = findProvinceByTouch(e.getX(), e.getY());
            if (province != null && province.getType() != Province.Type.VOID) {
                selectedProvince = province;
                Log.d("myLog", selectedProvince.getArmies().toString());
                if (!isArmyMoving())
                    activity.openContextMenu(activity.map);
                else if (selectedProvince.getSelected()) {
                    movingArmy.getOwner().attackProvince(movingArmy, selectedProvince);
                    activity.movingArmy(movingArmy);
                } else {
                    movingArmy.setSpeed(2);
                    activity.movingArmy(movingArmy);
                }
                invalidate();
            }
            invalidate();
            return true;
        }
    }
}
