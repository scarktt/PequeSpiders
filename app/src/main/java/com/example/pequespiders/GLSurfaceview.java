package com.example.pequespiders;

import android.opengl.GLSurfaceView;
import android.content.Context;
import android.view.MotionEvent;

public class GLSurfaceview extends GLSurfaceView {
    private final PequeSpidersRenderer mRenderer;

    public GLSurfaceview(Context context) {
        super(context);

        // Indica que se va a usar opengl es 2
        setEGLContextClientVersion(2);

        // Asigna el Renderer para dibujar en el sufaceview
        mRenderer = new PequeSpidersRenderer(context);
        setRenderer(mRenderer);

        // Renderiza la ventana solo cuando hay un cambio en los "dibujos"
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);

    }

    @Override
    public void onPause() {
        super.onPause();
        mRenderer.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        mRenderer.onResume();
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        mRenderer.processTouchEvent(e);
        return true;
    }
}
