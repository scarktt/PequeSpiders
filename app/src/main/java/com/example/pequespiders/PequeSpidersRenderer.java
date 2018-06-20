package com.example.pequespiders;

import static android.opengl.GLES20.*;
import static android.opengl.Matrix.*;

import Texture.CrearTextura;
import android.content.Context;
import android.opengl.GLSurfaceView.Renderer;
import android.view.MotionEvent;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import Texture.Sprite;
import data.BindData;
import Shader.CompilarShader;

public class PequeSpidersRenderer implements Renderer {

    /****************************************************************************************************************/
    /****************************************- VARIABLES E INSTANCIAS -**********************************************/
    /****************************************************************************************************************/
    // Matrices
    private final float[] mProjection = new float[16];
    private final float[] mView = new float[16];
    private final float[] mProjectionAndView = new float[16];

    // Instancias
    public CompilarShader compilarShader = new CompilarShader();
    public Sprite sprite = new Sprite();
    public CrearTextura crearTextura = new CrearTextura();
    public BindData bindData = new BindData();

    // Resolucion
    float mScreenWidth = 1920;
    float mScreenHeight = 1080;
    public static float ssu = 1.0f;

    // Misc
    Context mContext;
    long mLastTime;

    /****************************************************************************************************************/
    /************************************- onMETODOS  Y CONSTRUCTOR -************************************************/
    /****************************************************************************************************************/

    public PequeSpidersRenderer(Context c) {
        mContext = c;
        mLastTime = System.currentTimeMillis() + 100;
        //sprite = new Sprite();
    }

    public void onPause() {
        /* Do stuff to pause the renderer */
    }

    public void onResume() {
        /* Do stuff to resume the renderer */
        mLastTime = System.currentTimeMillis();
    }

    @Override
    public void onDrawFrame(GL10 unused) {

        // Obtiene el tiempo actual
        long now = System.currentTimeMillis();

        // Se verifica que sea valido
        if (mLastTime > now) return;

        // Obtiene la última fracción de tiempo que el frame tomó
        long elapsed = now - mLastTime;

        // Actualiza
        //UpdateSprite();

        // Renderizar
        bindData.bindData(mProjectionAndView);

        // Guarda el tiempo actual para ver cuanto tiempo tomó
        mLastTime = now;

    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {

        // Se necesita conocer el ancho y largo actual.
        mScreenWidth = width;
        mScreenHeight = height;

        // Vuelve a cargar el Viewport, indicándole que sea full screen.
        glViewport(0, 0, (int) mScreenWidth, (int) mScreenHeight);

        // Limpia las matrices.
        for (int i = 0; i < 16; i++) {
            mProjection[i] = 0.0f;
            mView[i] = 0.0f;
            mProjectionAndView[i] = 0.0f;
        }

        // Configura el ancho y alto de la pantalla para la traslacioón del sprite (normal)
        orthoM(mProjection, 0, 0f, mScreenWidth, 0.0f, mScreenHeight, 0, 50);

        // Asigna la posición de la cámara (View matrix)
        setLookAtM(mView, 0, 0f, 0f, 1f, 0f, 0f, 0f, 0f, 1.0f, 0.0f);

        // Calcula la proyección y transformación
        multiplyMM(mProjectionAndView, 0, mProjection, 0, mView, 0);

    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {

        crearTextura.CrearTextureAtlas(mContext);

        // Se asigna color negro
        glClearColor(0.0f, 0.0f, 0.0f, 1);

        glEnable(GL_BLEND);
        glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);

        compilarShader.Compilar(mContext);
    }

    /****************************************************************************************************************/
    /*************************************************- TOUCH -******************************************************/
    /****************************************************************************************************************/

    public void processTouchEvent(MotionEvent event) {
        // Get the half of screen value
        int screenhalf = (int) (mScreenWidth / 2);
        int screenheightpart = (int) (mScreenHeight / 3);
        if (event.getX() < screenhalf) {
            // Left screen touch
            if (event.getY() < screenheightpart)
                sprite.scale(-0.01f);
            else if (event.getY() < (screenheightpart * 2))
                sprite.translate(-10f * ssu, -10f * ssu);
            else
                sprite.rotate(0.01f);
        } else {
            // Right screen touch
            if (event.getY() < screenheightpart)
                sprite.scale(0.01f);
            else if (event.getY() < (screenheightpart * 2))
                sprite.translate(10f * ssu, 10f * ssu);
            else
                sprite.rotate(-0.01f);
        }
    }
}