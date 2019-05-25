package com.wowloltech.politicalsandbox.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import com.wowloltech.politicalsandbox.Game;
import com.wowloltech.politicalsandbox.models.Army;
import com.wowloltech.politicalsandbox.models.Map;
import com.wowloltech.politicalsandbox.models.Player;
import com.wowloltech.politicalsandbox.models.Province;

import java.util.LinkedList;
import java.util.List;

/**
 * TODO: document your custom view class.
 */
public class EditorView extends View {
    Game game;
    public Path provincePath;
    public float size = 100;
    List<Province> neighbours;
    Paint p;
    Paint pBlack;
    Paint pText;
    StringBuilder name;
    private float sqrt3 = (float) Math.sqrt(3);
    private Province selectedProvince;
    private float mPosX;
    private float mPosY;
    private EditorActivity activity;
    int selectedX = 0;
    int selectedY = 0;

    private float cX, cY;

    // Scaling objects
    private ScaleGestureDetector mScaleDetector;
    private GestureDetector gestureDetector;
    private float mScaleFactor = 0.75f;
    // The focus point for the scaling
    private float scalePointX;
    private float scalePointY;

    public EditorView(EditorActivity activity) {
        super(activity);
        this.activity = activity;
        game = activity.getGame();
        name = new StringBuilder();
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
        createProvincePath(provincePath);
        if (activity.getSharedPreferences("settings", Context.MODE_PRIVATE).getBoolean("ha", true))
            setLayerType(LAYER_TYPE_HARDWARE, null);
        else
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        mScaleDetector = new ScaleGestureDetector(activity, new ScaleListener());
        gestureDetector = new GestureDetector(activity, new MyGestureListener());
        game.setEditorView(this);
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

    private void createProvincePath(Path provincePath) {
        provincePath.moveTo(0f, size * 0.5f);
        provincePath.lineTo(size * sqrt3 * 0.5f, 0f);
        provincePath.lineTo(size * sqrt3, size * 0.5f);
        provincePath.lineTo(size * sqrt3, size * 1.5f);
        provincePath.lineTo(size * sqrt3 * 0.5f, size * 2f);
        provincePath.lineTo(0f, size * 1.5f);
        provincePath.close();
    }

    private void drawPathByProvince(Canvas canvas, Province province) {
        if (province.getType() != Province.Type.VOID) {
            name.append(province.getIncome()).append(' ');
            try {
                for (Player p : game.getPlayers())
                    for (Army a : p.getArmies())
                        if (a.getLocation().getId() == province.getId())
                            name.append(a.getStrength()).append(' ');
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (!province.getSelected())
                p.setColor(province.getOwner().getColor());
            else
                p.setColor(province.getOwner().getColor() - 0x66000000);
        } else p.setColor(Color.WHITE);
        if (province.getY() % 2 == 1) {
            provincePath.offset(size * sqrt3 * province.getX() + size * sqrt3 * 0.5f, 1.5f * size * province.getY());
            canvas.drawPath(provincePath, p);
            canvas.drawPath(provincePath, pBlack);
            canvas.drawText(name.toString(), size * sqrt3 * province.getX() + size * sqrt3, pText.getTextSize() / 2 + size + size * 1.5f * province.getY(), pText);
        } else {
            provincePath.offset(size * sqrt3 * province.getX(), size * 1.5f * province.getY());
            canvas.drawPath(provincePath, p);
            canvas.drawPath(provincePath, pBlack);
            canvas.drawText(name.toString(), size * sqrt3 * province.getX() + size * sqrt3 * 0.5f, size + pText.getTextSize() / 2 + size * 1.5f * province.getY(), pText);
        }

        provincePath.offset(-size * sqrt3 * province.getX(), -size * 1.5f * province.getY());
        if (province.getY() % 2 == 1) {
            provincePath.offset(-size * sqrt3 * 0.5f, 0);
        }
        name.delete(0, name.length());
    }

    public Province findProvinceByTouch() {
        float tempY = (int) cY % (int) (size * 3);
        if ((tempY <= size * 1.5 && tempY >= size * 0.5) || (tempY >= size * 2))
            selectedY = (int) Math.floor((cY) / (size * 1.5));
        else {
            int tempX = (int) cX % (int) (size * sqrt3);
            final float halfWidth = size * sqrt3 / 2;
            if (tempY < size * 0.5) {
                if (tempX > halfWidth)
                    selectedY = (int) Math.floor((cY - (size * 0.5 * (tempX - halfWidth) / halfWidth)) / (size * 1.5));
                else
                    selectedY = (int) Math.floor((cY - (size * 0.5 * (halfWidth - tempX) / halfWidth)) / (size * 1.5));
            } else if (tempY > size * 1.5 && tempY < size * 2) {
                if (tempX > halfWidth)
                    selectedY = (int) Math.floor((cY - (size * 0.5 * (size * sqrt3 - tempX) / halfWidth)) / (size * 1.5));
                else
                    selectedY = (int) Math.floor((cY - (size * 0.5 * tempX / halfWidth)) / (size * 1.5));
            }
        }
        if (selectedY % 2 == 0)
            selectedX = (int) Math.floor(cX / (int) (sqrt3 * size));
        else
            selectedX = (int) Math.floor((cX - size * sqrt3 / 2) / (sqrt3 * size));
        try {
            return Map.getProvinces()[selectedY][selectedX];
        } catch (Exception ignored) {
        }
        return null;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        // Let the ScaleGestureDetector inspect all events.
        mScaleDetector.onTouchEvent(ev);
        gestureDetector.onTouchEvent(ev);

        final int action = ev.getAction();
        if (action == MotionEvent.ACTION_DOWN) {
            cX = (ev.getX() - scalePointX) / mScaleFactor - mPosX + scalePointX; // canvas X
            cY = (ev.getY() - scalePointY) / mScaleFactor - mPosY + scalePointY; // canvas Y
        }
        return true;
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            mScaleFactor *= detector.getScaleFactor();
            scalePointX = detector.getFocusX();
            scalePointY = detector.getFocusY();

            // Don't let the object get too small or too large.
            mScaleFactor = Math.max(0.2f, Math.min(mScaleFactor, 5.0f));
            invalidate();
            return true;
        }
    }

    private class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            cX = (e2.getX() - scalePointX) / mScaleFactor - mPosX + scalePointX; // canvas X
            cY = (e2.getY() - scalePointY) / mScaleFactor - mPosY + scalePointY; // canvas Y

            // Only move if the ScaleGestureDetector isn't processing a gesture.
            if (!mScaleDetector.isInProgress()) {
                mPosX -= distanceX / mScaleFactor;
                mPosY -= distanceY / mScaleFactor;
                invalidate();
            }
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            super.onLongPress(e);
            Province province = findProvinceByTouch();

            invalidate();
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            //    Toast.makeText(activity, "onTouch " + e.getX() + " " + e.getY(), Toast.LENGTH_SHORT).show();
            Province province = findProvinceByTouch();
            if (province == null) return false;
            if (province.getType() == Province.Type.VOID) {
                province.setArmies(new LinkedList<Army>());
                province.setType(Province.Type.PLAIN);
                province.setGame(game);
                Player owner = game.findPlayerByID(Integer.valueOf(activity.idTextView.getText().toString()));
                province.setOwner(owner);
                province.setIncome(2);
                province.setRecruits(60);

            } else if (activity.selectedId != province.getOwner().getId()) {
                if (activity.countryPicking) {
                    activity.selectedId = province.getOwner().getId();
                    activity.changePlayer();
                    activity.countryPicking = false;
                } else {
                    for (Army a : province.getArmies())
                        game.removeArmy(a);
                    province.setOwner(game.findPlayerByID(activity.selectedId));
                }
            } else {
                province.setType(Province.Type.VOID);
            }


            invalidate();
            return true;
        }
    }

}
