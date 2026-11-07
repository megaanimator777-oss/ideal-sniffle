package com.musicviz;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.View;

public class CarView extends View {
    private final Paint p = new Paint(3);
    private boolean white = false;
    private float level = 0.25f, phase = 0;
    public CarView(Context c) { super(c); setLayerType(View.LAYER_TYPE_SOFTWARE, null); }
    public void setWhite(boolean w) { white=w; invalidate(); }
    public void setAudioLevel(float v) { level=Math.max(0.05f, Math.min(1f, v)); invalidate(); }

    @Override protected void onDraw(Canvas c) {
        super.onDraw(c);
        float w=getWidth(), h=getHeight();
        c.drawColor(Color.rgb(8,8,10));
        // glow / floor
        p.setColor(Color.argb(65, 0, 190, 255)); p.setStyle(Paint.Style.FILL);
        c.drawOval(w*.12f,h*.78f,w*.88f,h*.94f,p);
        float carW=w*.78f, carH=h*.55f, left=(w-carW)/2, top=h*.25f;
        int body=white?Color.rgb(225,228,232):Color.rgb(20,22,27);
        p.setColor(body); p.setStyle(Paint.Style.FILL);
        Path shell=new Path();
        shell.moveTo(left+carW*.13f,top+carH*.18f);
        shell.quadTo(left+carW*.5f,top-carH*.04f,left+carW*.87f,top+carH*.18f);
        shell.lineTo(left+carW*.98f,top+carH*.78f);
        shell.quadTo(left+carW*.5f,top+carH*.95f,left+carW*.02f,top+carH*.78f);
        shell.close(); c.drawPath(shell,p);
        // rear window
        p.setColor(Color.rgb(18,25,32));
        c.drawRoundRect(left+carW*.19f,top+carH*.13f,left+carW*.81f,top+carH*.43f,18,18,p);
        // open trunk lid, raised
        p.setColor(body); p.setStyle(Paint.Style.STROKE); p.setStrokeWidth(13);
        c.drawLine(left+carW*.17f,top+carH*.53f,left+carW*.31f,top+carH*.03f,p);
        c.drawLine(left+carW*.83f,top+carH*.53f,left+carW*.69f,top+carH*.03f,p);
        p.setStyle(Paint.Style.FILL);
        // trunk dark cavity
        p.setColor(Color.rgb(3,4,6));
        c.drawRoundRect(left+carW*.14f,top+carH*.48f,left+carW*.86f,top+carH*.84f,12,12,p);
        // enclosure
        p.setColor(Color.rgb(30,32,37));
        c.drawRoundRect(left+carW*.22f,top+carH*.51f,left+carW*.78f,top+carH*.82f,10,10,p);
        // huge subwoofer cone
        float cx=w/2, cy=top+carH*.665f;
        float r=carW*.245f;
        p.setColor(Color.rgb(7,8,10)); c.drawCircle(cx,cy,r*1.12f,p);
        p.setColor(Color.rgb(95,99,105)); p.setStyle(Paint.Style.STROKE); p.setStrokeWidth(5); c.drawCircle(cx,cy,r*1.05f,p); p.setStyle(Paint.Style.FILL);
        float pulse=1f + level*.10f*(float)Math.sin(phase);
        c.save(); c.scale(pulse,pulse,cx,cy);
        p.setShader(new android.graphics.RadialGradient(cx-r*.12f,cy-r*.16f,r,
            new int[]{Color.rgb(100,105,112),Color.rgb(27,29,33),Color.BLACK},null,android.graphics.Shader.TileMode.CLAMP));
        c.drawCircle(cx,cy,r,p); p.setShader(null);
        p.setColor(Color.rgb(10,11,13)); c.drawCircle(cx,cy,r*.34f,p);
        p.setColor(Color.rgb(70,74,80)); c.drawCircle(cx,cy,r*.23f,p);
        p.setColor(Color.rgb(5,6,8)); c.drawCircle(cx,cy,r*.12f,p);
        c.restore();
        // taillights and bumper
        p.setColor(Color.rgb(190,20,25));
        c.drawRoundRect(left+carW*.06f,top+carH*.60f,left+carW*.19f,top+carH*.72f,6,6,p);
        c.drawRoundRect(left+carW*.81f,top+carH*.60f,left+carW*.94f,top+carH*.72f,6,6,p);
        p.setColor(Color.rgb(70,73,78)); c.drawRoundRect(left+carW*.03f,top+carH*.83f,left+carW*.97f,top+carH*.94f,12,12,p);
        p.setColor(Color.WHITE); p.setTextSize(14); p.setTextAlign(Paint.Align.CENTER);
        c.drawText("ВАЗ 2110  •  OPEN TRUNK",w/2,h*.96f,p);
        phase += 0.35f + level*.8f;
        postInvalidateDelayed(45);
    }
}
