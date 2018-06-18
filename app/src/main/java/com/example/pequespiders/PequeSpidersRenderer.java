package com.example.pequespiders;

import static android.opengl.GLES20.*;
import static android.opengl.GLUtils.*;
import static android.opengl.Matrix.*;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLSurfaceView.Renderer;
import android.view.MotionEvent;

import java.nio.*;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import Shader.Shader;
import Sprite.Sprite;
import data.Coord;
import data.UV;
import util.Reader;

public class PequeSpidersRenderer implements Renderer {

    /****************************************************************************************************************/
    /****************************************- VARIABLES E INSTANCIAS -**********************************************/
    /****************************************************************************************************************/
    // Matrices
    private final float[] mProjection = new float[16];
    private final float[] mView = new float[16];
    private final float[] mProjectionAndView = new float[16];

    // Variables geometricas
    public static float vertices[];
    public static short indices[];
    public static float uvs[];
    public FloatBuffer vertexBuffer;
    public ShortBuffer drawListBuffer;
    public FloatBuffer uvBuffer;

    // Variables para los Shaders
    private String mbackground = "mBackground";
    private String vPosition = "vPosition";
    private String atexCoord = "a_texCoord";
    private String sTexture = "s_texture";

    // Resolucion
    float mScreenWidth = 1920;
    float mScreenHeight = 1080;
    public static float ssu = 1.0f;
    public static float ssx = 1.0f;
    public static float ssy = 1.0f;
    public static float swp = 320.0f;
    public static float shp = 480.0f;

    // Misc
    Context mContext;
    long mLastTime;
    int mProgram;

    // Instancias
    public Reader readerRaw = new Reader();
    public Coord coord = new Coord();
    public UV uv = new UV();
    public Sprite sprite = new Sprite();

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
        Render(mProjectionAndView);

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

        // Crea los triangulos
        SetupTriangle();
        // Crea la información para la textura
        SetupImage();

        // Se asigna color negro
        glClearColor(0.0f, 0.0f, 0.0f, 1);

        glEnable(GL_BLEND);
        glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);

        /* Se crean los shader, para ello se indica que tipo de shader se va a utilizar
         * [GL_VERTEX_SHADER] o [GL_FRAGMENT_SHADER]
         * Se llama una función externa [readerRaw.readTextFileFromRawResource] a la que se indica el contexto
         * y se le indica el archivo que se leerá; esta clase retorna un string con el código del archivo leído
         * NOTA: Recordar que el shader es un pequeño programa que corre en la GPU
         * */
        int vertexShader = Shader.loadShader(GL_VERTEX_SHADER, readerRaw.readTextFileFromRawResource(mContext, R.raw.vertex_shader_texture));
        int fragmentShader = Shader.loadShader(GL_FRAGMENT_SHADER, readerRaw.readTextFileFromRawResource(mContext, R.raw.fragment_shader_texture));

        Shader.program_Image = glCreateProgram();             // Crea un programa vacío [Cuando se habla de programa se refiere a shaders, esta es la etapa final que contendrá al fragment shader y al vertex shader]
        glAttachShader(Shader.program_Image, vertexShader);   // Agrega el vertex shader al programa
        glAttachShader(Shader.program_Image, fragmentShader); // Agrega el fragment shader al programa
        glLinkProgram(Shader.program_Image);                  // Hace al programa ejecutable

        // Se indica que se utilice el programa anteriormente creado
        glUseProgram(Shader.program_Image);
    }

    /****************************************************************************************************************/
    /********************************************- RENDER -**********************************************************/
    /****************************************************************************************************************/

    private void Render(float[] m) {

        // Limpia la pantalla y el buffer de profundidad (Depth Buffer)
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

        // Obtiene la ubicación
        int mPositionHandle = glGetAttribLocation(Shader.program_Image, vPosition);
        glVertexAttribPointer(mPositionHandle, 3, GL_FLOAT, false, 0, vertexBuffer);
        glEnableVertexAttribArray(mPositionHandle);

        int mTexCoordLoc = glGetAttribLocation(Shader.program_Image, atexCoord);
        glVertexAttribPointer(mTexCoordLoc, 2, GL_FLOAT, false, 0, uvBuffer);
        glEnableVertexAttribArray(mTexCoordLoc);

        int mtrxhandle = glGetUniformLocation(Shader.program_Image, mbackground);
        glUniformMatrix4fv(mtrxhandle, 1, false, m, 0);

        int mSamplerLoc = glGetUniformLocation(Shader.program_Image, sTexture);

        glUniform1i(mSamplerLoc, 0);

        // Se manda a dibujar
        glDrawElements(GL_TRIANGLES, coord.getIndices().length, GL_UNSIGNED_SHORT, drawListBuffer);

        // Deshabilita los vextex array
        glDisableVertexAttribArray(mPositionHandle);
        glDisableVertexAttribArray(mTexCoordLoc);
    }

    /****************************************************************************************************************/
    /********************************************- TEXTURAS -********************************************************/
    /****************************************************************************************************************/

    public void SetupImage() {
        // The texture buffer
        ByteBuffer bbBackground = ByteBuffer.allocateDirect(uv.getUv().length * 4);
        bbBackground.order(ByteOrder.nativeOrder());
        uvBuffer = bbBackground.asFloatBuffer();
        uvBuffer.put(uv.getUv());
        uvBuffer.position(0);

        // Generate Textures, if more needed, alter these numbers.
        int[] texturenames = new int[1];
        glGenTextures(1, texturenames, 0);

        int[] id = new int[1];

        // Retrieve our image from resources.
        id[0] = mContext.getResources().getIdentifier("drawable/textureatlas", null, mContext.getPackageName());

        // Temporary create a bitmap
        Bitmap bmp = BitmapFactory.decodeResource(mContext.getResources(), id[0]);

        // Bind texture to texturename
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, texturenames[0]);

        // Set filtering
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        // Load the bitmap into the bound texture.
        texImage2D(GL_TEXTURE_2D, 0, bmp, 0);

        // We are done using the bitmap so we should recycle it.
        bmp.recycle();
    }

    public void SetupTriangle() {
        // The vertex buffer.
        ByteBuffer bbBackground = ByteBuffer.allocateDirect(coord.getVertices().length * 4);
        bbBackground.order(ByteOrder.nativeOrder());
        vertexBuffer = bbBackground.asFloatBuffer();
        vertexBuffer.put(coord.getVertices());
        vertexBuffer.position(0);

        // initialize byte buffer for the draw list
        ByteBuffer dlbBackground = ByteBuffer.allocateDirect(coord.getIndices().length * 2);
        dlbBackground.order(ByteOrder.nativeOrder());
        drawListBuffer = dlbBackground.asShortBuffer();
        drawListBuffer.put(coord.getIndices());
        drawListBuffer.position(0);
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