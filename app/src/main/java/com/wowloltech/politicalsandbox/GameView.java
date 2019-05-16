package com.wowloltech.politicalsandbox;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.Toast;

import java.util.List;

public class GameView extends View {
    public Path provincePath;
    public float size = 100;
    List<Province> neighbours;
    Paint p;
    Paint pBlack;
    Paint pText;
    StringBuilder name;
    private Province selectedProvince;
    private GameActivity activity;
    private Game game;
    private int selectedX;
    private int selectedY;
    private boolean isArmyMoving = false;
    private boolean longPress = false;
    private Army movingArmy = null;


    private float mLastTouchX;
    private float mLastTouchY;
    private float mPosX;
    private float mPosY;

    private float cX, cY; // circle coords


    // Scaling objects
    private ScaleGestureDetector mScaleDetector;
    private GestureDetector gestureDetector;
    private float mScaleFactor = 1.f;
    // The focus point for the scaling
    private float scalePointX;
    private float scalePointY;


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
        mScaleDetector = new ScaleGestureDetector(activity, new ScaleListener());
        gestureDetector = new GestureDetector(activity, new MyGestureListener());
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
        canvas.scale(mScaleFactor, mScaleFactor, scalePointX, scalePointY);
        canvas.translate(mPosX, mPosY);
        canvas.drawColor(Color.WHITE);
        for (int y = 0; y < Map.getHeight(); y++)
            for (int x = 0; x < Map.getWidth(); x++)
                drawPathByProvince(canvas, Map.getProvinces()[y][x]);
        canvas.restore();

    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        // Let the ScaleGestureDetector inspect all events.
        mScaleDetector.onTouchEvent(ev);
        gestureDetector.onTouchEvent(ev);

        final int action = ev.getAction();


        switch (action & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN: {

                final float x = (ev.getX() - scalePointX) / mScaleFactor;
                final float y = (ev.getY() - scalePointY) / mScaleFactor;
                cX = x - mPosX + scalePointX; // canvas X
                cY = y - mPosY + scalePointY; // canvas Y

                // Remember where we started
                mLastTouchX = x;
                mLastTouchY = y;
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                final float x = (ev.getX() - scalePointX) / mScaleFactor;
                final float y = (ev.getY() - scalePointY) / mScaleFactor;
                cX = x - mPosX + scalePointX; // canvas X
                cY = y - mPosY + scalePointY; // canvas Y


                // Only move if the ScaleGestureDetector isn't processing a gesture.
                if (!mScaleDetector.isInProgress()) {
                    final float dx = x - mLastTouchX; // change in X
                    final float dy = y - mLastTouchY; // change in Y

                    if (dx < 1000)
                        mPosX += dx;
                    if (dy < 1000)
                        mPosY += dy;

                    invalidate();
                }

                mLastTouchX = x;
                mLastTouchY = y;

                break;

            }
            case MotionEvent.ACTION_UP: {
                mLastTouchX = 0;
                mLastTouchY = 0;
                invalidate();
            }
        }
        return true;
    }

    private void drawPathByProvince(Canvas canvas, Province province) {
        if (province.getType() != Province.Type.VOID) {
            name.append(province.getIncome());
            name.append(' ');
            try {
                for (Player p : game.getPlayers())
                    for (Army a : p.getArmies())
                        if (a.getLocation().getId() == province.getId()) {
                            name.append(a.getStrength());
                            name.append(' ');
                        }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (!province.getSelected())
                p.setColor(province.getOwner().getColor());
            else
                p.setColor(province.getOwner().getColor() - 0x66000000);

            if (province.getY() % 2 == 1) {
                provincePath.offset((float) (size * Math.sqrt(3) * province.getX() + size * Math.sqrt(3) * 0.5), (float) (size * 1.5 * province.getY()));
                canvas.drawPath(provincePath, p);
                if (mScaleFactor > 0.5f) {
                    canvas.drawPath(provincePath, pBlack);
                    canvas.drawText(name.toString(), (float) (size * Math.sqrt(3) * province.getX() + size * Math.sqrt(3)), (float) (pText.getTextSize() / 2 + size + size * 1.5 * province.getY()), pText);
                }
            } else {
                provincePath.offset((float) (size * Math.sqrt(3) * province.getX()), (float) (size * 1.5 * province.getY()));
                canvas.drawPath(provincePath, p);
                if (mScaleFactor > 0.5f) {
                    canvas.drawPath(provincePath, pBlack);
                    canvas.drawText(name.toString(), (float) (size * Math.sqrt(3) * province.getX() + size * Math.sqrt(3) * 0.5), (float) (size + pText.getTextSize() / 2 + size * 1.5 * province.getY()), pText);
                }
            }

            provincePath.offset(-(float) (size * Math.sqrt(3) * province.getX()), -(float) (size * 1.5 * province.getY()));
            if (province.getY() % 2 == 1) {
                provincePath.offset(-(float) (size * Math.sqrt(3) * 0.5), 0);
            }
            name.delete(0, name.length());
        }
    }


    public Province findProvinceByTouch() {
        float tempY = (int) cY % (int) (size * 3);
        if ((tempY <= size * 1.5 && tempY >= size * 0.5) || (tempY >= size * 2))
            selectedY = (int) Math.floor((cY) / (size * 1.5));
        else {
            int tempX = (int) cX % (int) (size * Math.sqrt(3));
            final float halfWidth = (float) (size * Math.sqrt(3) / 2);
            if (tempY < size * 0.5) {
                if (tempX > halfWidth)
                    selectedY = (int) Math.floor((cY - (size * 0.5 * (tempX - halfWidth) / halfWidth)) / (size * 1.5));
                else
                    selectedY = (int) Math.floor((cY - (size * 0.5 * (halfWidth - tempX) / halfWidth)) / (size * 1.5));
            } else if (tempY > size * 1.5 && tempY < size * 2) {
                if (tempX > halfWidth)
                    selectedY = (int) Math.floor((cY - (size * 0.5 * (size * Math.sqrt(3) - tempX) / halfWidth)) / (size * 1.5));
                else
                    selectedY = (int) Math.floor((cY - (size * 0.5 * tempX / halfWidth)) / (size * 1.5));
            }
        }
        if (selectedY % 2 == 0)
            selectedX = (int) Math.floor(cX / (int) (Math.sqrt(3) * size));
        else
            selectedX = (int) Math.floor((cX - size * Math.sqrt(3) / 2) / (Math.sqrt(3) * size));
        try {
            return Map.getProvinces()[selectedY][selectedX];
        } catch (Exception e) {
        }
        return null;
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

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            mScaleFactor *= detector.getScaleFactor();
            scalePointX = detector.getFocusX();
            scalePointY = detector.getFocusY();

            // Don't let the object get too small or too large.
            mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 10.0f));

            invalidate();
            return true;
        }

    }

    private class MyGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public void onLongPress(MotionEvent e) {
            super.onLongPress(e);
            Province province = findProvinceByTouch();
            if (province != null && province.getType() != Province.Type.VOID && province.getOwner() == game.getCurrentPlayer()
                    && !isArmyMoving && province.getArmies().size() > 0 && selectedProvince != null && !activity.AITurn) {
                selectedProvince = province;
                province.getOwner().combineArmy(province);
                activity.movingArmy(province.getArmies().get(0));
                longPress = true;
                invalidate();
            }
            invalidate();
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
        //    Toast.makeText(activity, "onTouch " + e.getX() + " " + e.getY(), Toast.LENGTH_SHORT).show();
            Province province = findProvinceByTouch();
            if (province != null && province.getType() != Province.Type.VOID && !activity.AITurn) {
                selectedProvince = province;
                //      Log.d("myLog", selectedProvince.getArmies().toString());
                if (activity.newGame) {
                    activity.getSharedPreferences("save", Context.MODE_PRIVATE).edit().putInt("player_id", province.getOwner().getId()).apply();
                    activity.currentTurn(province.getOwner());
                    activity.button.setEnabled(true);
                    invalidate();
                    return true;
                }
                if (!isArmyMoving())
                    activity.openContextMenu(activity.map);
                else if (selectedProvince.getSelected()) {
                    boolean isEnemy = movingArmy.getOwner() == selectedProvince.getOwner();
                    movingArmy.getOwner().attackProvince(movingArmy, selectedProvince);
                    if (!longPress || isEnemy)
                        movingArmy.getOwner().moveArmy(movingArmy, selectedProvince);
                    else {
                        longPress = false;
                        movingArmy.setSpeed(movingArmy.getSpeed() + 1);
                    }
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