package com.example.pequespiders;

import static android.opengl.GLES20.*;
import static android.opengl.GLUtils.*;
import static android.opengl.Matrix.*;

import java.nio.*;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import Shader.Shader;
import Sprite.Sprite;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLSurfaceView.Renderer;
import android.util.Log;
import android.view.MotionEvent;
import data.Background;
import texture.BackgroundUV;
import texture.SpiderUV;
import util.Reader;

public class PequeSpidersRenderer implements Renderer {

    // Matrices
    private final float[] mProjection = new float[16];
    private final float[] mView = new float[16];
    private final float[] mProjectionAndView = new float[16];

    // Variables Geométricas
    public FloatBuffer vertexBuffer;
    public ShortBuffer drawListBuffer;
    public FloatBuffer uvBuffer;
    public float vertices[];

    // Shaders Variables
    private String mbackground = "mBackground";
    private String mPosition = "vPosition";
    private String atexCoord = "a_texCoord";
    private String sTexture = "s_texture";

    // Resolución de pantalla
    float	mScreenWidth = 1280;
    float	mScreenHeight = 768;

    // Otro
    Context mContext;
    long mLastTime;

    // Instancias
    public Background background = new Background();
    public BackgroundUV backgroundUV = new BackgroundUV();
    public SpiderUV spiderUV = new SpiderUV();
    public Reader readerRaw = new Reader();
    public Sprite sprite = new Sprite();

    //Escalación
    public static float   ssu = 1.0f;
    public static float   ssx = 1.0f;
    public static float   ssy = 1.0f;
    public static float   swp = 320.0f;
    public static float   shp = 480.0f;

    public PequeSpidersRenderer(Context c) {
        mContext = c;
        mLastTime = System.currentTimeMillis() + 100;
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
        UpdateSprite();

        // Renderizar
        Render(mProjectionAndView);

        // Guarda el tiempo actual para ver cuanto tiempo tomó
        mLastTime = now;

    }

    private void Render(float[] m) {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        // Obtiene las coordenadas exactas del vertex shader que están en vPosition
        int PosicionDeterminada = glGetAttribLocation(Shader.program_Image, mPosition);

        // Habilita un attribute array generico
        glEnableVertexAttribArray(PosicionDeterminada);

        // Prepara las coordenadas
        glVertexAttribPointer(PosicionDeterminada, 3, GL_FLOAT, false, 0, vertexBuffer);

        // Obtiene las coordendas exactas de textura
        int TexCoordLoc = glGetAttribLocation(Shader.program_Image, atexCoord );

        // Habilita un attribute array generico
        glEnableVertexAttribArray (TexCoordLoc);

        // Prepara las coordenadas de textura
        glVertexAttribPointer (TexCoordLoc, 2, GL_FLOAT, false, 0, uvBuffer);

        int mtrxhandle = glGetUniformLocation(Shader.program_Image, mbackground);

        // Aplica las proyecciones y transformaciones
        glUniformMatrix4fv(mtrxhandle, 1, false, m, 0);

        int mSamplerLoc = glGetUniformLocation (Shader.program_Image, sTexture );

        // Set the sampler texture unit to 0, where we have saved the texture.
        glUniform1i ( mSamplerLoc, 0);

        // Dibuja el background
        glDrawElements(GL_TRIANGLES, background.getIndices().length, GL_UNSIGNED_SHORT, drawListBuffer);

        // Deshabilita el vertex array
        glDisableVertexAttribArray(PosicionDeterminada);
        glDisableVertexAttribArray(TexCoordLoc);

    }


    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {

        // Se necesita conocer el ancho y largo actual.
        mScreenWidth = width;
        mScreenHeight = height;

        // Vuelve a cargar el Viewport, indicándole que sea full screen.
        glViewport(0, 0, (int)mScreenWidth, (int)mScreenHeight);

        // Limpia las matrices.
        for(int i=0;i<16;i++)
        {
            mProjection[i] = 0.0f;
            mView[i] = 0.0f;
            mProjectionAndView[i] = 0.0f;
        }

        // Configura el ancho y alto de la pantalla para la traslacioón del sprite (normal)
        orthoM(mProjection, 0, 0f, mScreenWidth, 0.0f, mScreenHeight, 0, 50);

        // Asigna la posición de la cámara (View matrix)
        setLookAtM(mView, 0, 0f, 0f, 1f, 0f, 0f, 0f, 0f, 1.0f, 0.0f);

        // Calculate the projection and view transformation
        // Calcula la proyección y transformación
        multiplyMM(mProjectionAndView, 0, mProjection, 0, mView, 0);

        // Setup our scaling system
        //SetupScaling();
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        // Setup our scaling system
        //SetupScaling();

        inicializacionCoords(background.getVertices(), background.getIndices());
        inicializacionTexture(backgroundUV.getUv(), spiderUV.getUv());

        // Se asigna color negro
        glClearColor(0.0f, 0.0f, 0.0f, 1);

        /* Se crean los shader, para ello se indica que tipo de shader se va a utilizar
         * [GL_VERTEX_SHADER] o [GL_FRAGMENT_SHADER]
         * Se llama una función externa [readerRaw.readTextFileFromRawResource] a la que se indica el contexto
         * y se le indica el archivo que se leerá; esta clase retorna un string con el código del archivo leído
         * NOTA: Recordar que el shader es un pequeño programa que corre en la GPU */
        int vertexShader = Shader.loadShader(GL_VERTEX_SHADER, readerRaw.readTextFileFromRawResource(mContext, R.raw.vertex_shader));
        int fragmentShader = Shader.loadShader(GL_FRAGMENT_SHADER, readerRaw.readTextFileFromRawResource(mContext, R.raw.fragment_shader));

        Shader.program_SolidColor = glCreateProgram();             // Crea un programa vacío [Cuando se habla de programa se refiere a shaders, esta es la etapa final que contendrá al fragment shader y al vertex shader]
        glAttachShader(Shader.program_SolidColor, vertexShader);   // Agrega el vertex shader al programa
        glAttachShader(Shader.program_SolidColor, fragmentShader); // Agrega el fragment shader al programa
        glLinkProgram(Shader.program_SolidColor);                  // Hace al programa ejecutable

        // Se crean los shader para la textura
        vertexShader = Shader.loadShader(GL_VERTEX_SHADER, readerRaw.readTextFileFromRawResource(mContext, R.raw.vertex_shader_texture));
        fragmentShader = Shader.loadShader(GL_FRAGMENT_SHADER, readerRaw.readTextFileFromRawResource(mContext, R.raw.fragment_shader_texture));

        Shader.program_Image = glCreateProgram();             // Crea un programa vacío
        glAttachShader(Shader.program_Image, vertexShader);   // Agrega el vertex shader al programa
        glAttachShader(Shader.program_Image, fragmentShader); // Agrega el fragment shader al programa
        glLinkProgram(Shader.program_Image);                  // Hace al programa ejecutable


        // Se indica que se utilice el programa anteriormente creado
        glUseProgram(Shader.program_Image);
    }

    public void inicializacionTexture(float[] uvB, float[] uvS) {
        // Texture buffer Background
        ByteBuffer bb = ByteBuffer.allocateDirect(uvB.length * 4);
        bb.order(ByteOrder.nativeOrder());
        uvBuffer = bb.asFloatBuffer();
        uvBuffer.put(uvB);
        uvBuffer.position(0);

        // Se generan las texturas (Si se necesitan más texturas, aumentar el número)
        int[] textureID = new int[2];

        // Se indica el nombre del arreglo de texturas y en índice (0 para la primer textura)
        glGenTextures(1, textureID, 0);

        int id = 0;

        try{
            // Obtiene la imagen desde los archivos
            id = mContext.getResources().getIdentifier("drawable/background", null, mContext.getPackageName());

        }catch (Error e){
            Log.e("Imagen source", "Error al abrir la imagen.");
        }

        // se crea un bitmap
        Bitmap bmp = BitmapFactory.decodeResource(mContext.getResources(), id);

        // Se vincula la textura con textureID
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, textureID[0]);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        // Se carga la textura indicando que es de tipo 2D
        texImage2D(GL_TEXTURE_2D, 0, bmp, 0);

        // Se indica que se recicle el bitmap (se debe de indicar porque no utilizará
        // y tampoco será recogido automáticamwnte)
        bmp.recycle();

        /************************** SPIDER TEXTURE *********************************/
        // Texture buffer Spider
        ByteBuffer bb1 = ByteBuffer.allocateDirect(uvS.length * 4);
        bb1.order(ByteOrder.nativeOrder());
        uvBuffer = bb1.asFloatBuffer();
        uvBuffer.put(uvS);
        uvBuffer.position(0);

        glGenTextures(1, textureID, 1);

        int id_Spider = 0;

        try{
            // Obtiene la imagen desde los archivos
            id_Spider = mContext.getResources().getIdentifier("drawable/spider2", null, mContext.getPackageName());

        }catch (Error e){
            Log.e("Imagen source", "Error al abrir la imagen.");
        }

        // se crea un bitmap
        Bitmap bmp1 = BitmapFactory.decodeResource(mContext.getResources(), id_Spider);

        // Se vincula la textura con textureID
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, textureID[1]);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        // Se carga la textura indicando que es de tipo 2D
        texImage2D(GL_TEXTURE_2D, 0, bmp1, 0);

        // Se indica que se recicle el bitmap (se debe de indicar porque no utilizará
        // y tampoco será recogido automáticamwnte)
        bmp.recycle();

    }

    public void inicializacionCoords(float[] vertices, short[] indices) {
        // Vertex buffer
        ByteBuffer bb = ByteBuffer.allocateDirect(vertices.length * 4);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(vertices);
        vertexBuffer.position(0);

        // se inicializa un bytebuffer
        ByteBuffer dlb = ByteBuffer.allocateDirect(indices.length * 2);
        dlb.order(ByteOrder.nativeOrder());
        drawListBuffer = dlb.asShortBuffer();
        drawListBuffer.put(indices);
        drawListBuffer.position(0);
    }

    public void UpdateSprite() {
        // Get new transformed vertices
        vertices = sprite.getTransformedVertices();

        // The vertex buffer.
        ByteBuffer bb = ByteBuffer.allocateDirect(vertices.length * 4);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(vertices);
        vertexBuffer.position(0);
    }

    /*public void SetupScaling() {
        // The screen resolutions
        swp = (int) (mContext.getResources().getDisplayMetrics().widthPixels);
        shp = (int) (mContext.getResources().getDisplayMetrics().heightPixels);

        // Orientation is assumed portrait
        ssx = swp / 320.0f;
        ssy = shp / 480.0f;

        // Get our uniform scaler
        if(ssx > ssy)
            ssu = ssy;
        else
            ssu = ssx;
    }*/

    public void processTouchEvent(MotionEvent event) {
        // Get the half of screen value
        int screenhalf = (int) (mScreenWidth / 2);
        int screenheightpart = (int) (mScreenHeight / 3);
        if(event.getX()<screenhalf)
        {
            // Left screen touch
            if(event.getY() < screenheightpart)
                sprite.scale(-0.01f);
            else if(event.getY() < (screenheightpart*2))
                sprite.translate(-10f, -10f);
            else
                sprite.rotate(0.01f);
        }
        else
        {
            // Right screen touch
            if(event.getY() < screenheightpart)
                sprite.scale(0.01f);
            else if(event.getY() < (screenheightpart*2))
                sprite.translate(10f, 10f);
            else
                sprite.rotate(-0.01f);
        }
    }
}
