package com.example.ollie.View;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class PaintView extends View {

    private static final float EPAISSEUR_TRAIT = 8f;

    private Paint brush = new Paint();
    private Path drawingPath = new Path();

    public PaintView(Context context) {
        super(context);

        brush.setAntiAlias(true);
        brush.setColor(Color.RED);
        brush.setStyle(Paint.Style.STROKE);
        brush.setStrokeJoin(Paint.Join.ROUND);
        brush.setStrokeWidth(EPAISSEUR_TRAIT);

    }


    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawPath(drawingPath, brush);
    }


    public void clearPath() {
        drawingPath = new Path();
    }


    public void moveTo(float x, float y) {
        drawingPath.moveTo(x, y);
    }


    public void lineTo(float x, float y) {
        drawingPath.lineTo(x, y);
        postInvalidate();
    }
}
