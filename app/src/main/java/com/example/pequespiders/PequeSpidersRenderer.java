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
import Shader.CompilarShader;

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

    // Instancias
    public CompilarShader compilarShader = new CompilarShader();
    public Shader shader = new Shader();
    public Coord coord = new Coord();
    public UV uv = new UV();
    public Sprite sprite = new Sprite();

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
        CrearBuffers();
        CrearTextura();

        // Se asigna color negro
        glClearColor(0.0f, 0.0f, 0.0f, 1);

        glEnable(GL_BLEND);
        glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);

        compilarShader.Compilar(mContext);
    }

    /****************************************************************************************************************/
    /********************************************- RENDER -**********************************************************/
    /****************************************************************************************************************/

    private void Render(float[] m) {
        // Limpia la pantalla y el buffer de profundidad (Depth Buffer)
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

        // Para pasar el vertex data al shader se necesita ubicación de la variable (de tipo vertex attribute)
        // del vertex shader "VPOSITION" y esta ubicación se obtiene y se almacena en "mPositionHandle"
        int mPositionHandle = glGetAttribLocation(Shader.program_Image, Shader.V_POSITION);
        // Se le indica a OpenGL que a continuación se describirá (con glVertexAttribPointer) qué tipo de
        // información se usará como input al shader en la ubicación encontrada anteriormente
        glEnableVertexAttribArray(mPositionHandle);
        // Describe como se presentan los datos que sirven como input para el shader (este se asocia a una
        // variable del shader)
        glVertexAttribPointer(mPositionHandle, 3, GL_FLOAT, false, 0, vertexBuffer);

        // Para pasar las coordenadas de textura al shader se necesita ubicación de la variable del
        // fragment shader "A_TEXCOORD" y esta ubicación se obtiene y se almacena en "mTexCoordLoc"
        int mTexCoordLoc = glGetAttribLocation(Shader.program_Image, Shader.INPUT_TEXCOORD);
        glVertexAttribPointer(mTexCoordLoc, 2, GL_FLOAT, false, 0, uvBuffer);
        glEnableVertexAttribArray(mTexCoordLoc);

        // Obtiene la ubicación de la variable del shader al que se transferirán los datos
        int mtrxhandle = glGetUniformLocation(Shader.program_Image, Shader.U_MATRIX);

        // Se le indica que aplique la proyeción y transformación
        glUniformMatrix4fv(mtrxhandle, 1, false, m, 0);

        // Se obtiene la ubicación de la variable "S_TEXTURE" de tipo uniform (variable que almacena la información
        // de la textura)
        int ubicacion1 = glGetUniformLocation(Shader.program_Image, Shader.U_TEXTURE);
        int ubicacion2 = glGetUniformLocation(Shader.program_Image, Shader.U_TEXTURE2);

        // Para pasar la información de la textura al shader se debe de especificar el número de unidad
        // de textura que usamos (no el objeto de textura, por ejemplo: de GL_TEXTURE0 se toma el 0 y de GL_TEXTURE1 el 1)
        // y además asociar la textura que queramos a esa unidad.
        glUniform1i(ubicacion1, 0);
        glUniform1i(ubicacion2, 1);

        // Se manda a dibujar
        glDrawElements(GL_TRIANGLES, coord.getIndices().length, GL_UNSIGNED_SHORT, drawListBuffer);

        // Deshabilita los vextex array
        glDisableVertexAttribArray(mPositionHandle);
        glDisableVertexAttribArray(mTexCoordLoc);
    }

    /****************************************************************************************************************/
    /********************************************- TEXTURAS -********************************************************/
    /****************************************************************************************************************/

    public void CrearTextura() {
        int[] texture = new int[2];
        // Genera un id de textura en un arreglo que aumenta en dependencia del numero de texturas
        glGenTextures(1, texture, 0);

        int[] id = new int[2];

        // Retorna la imagen (textura desde los archivos del proyecto)
        id[0] = mContext.getResources().getIdentifier("drawable/textureatlas", null, mContext.getPackageName());
        id[1] = mContext.getResources().getIdentifier("drawable/texturatlas", null, mContext.getPackageName());

        // Se crea temporalmente un bitmap
        Bitmap bmp0 = BitmapFactory.decodeResource(mContext.getResources(), id[0]);
        Bitmap bmp1 = BitmapFactory.decodeResource(mContext.getResources(), id[1]);

        // Activamos una textura
        glActiveTexture(GL_TEXTURE0);
        // Se indica que textura que se va utilizar (texture[0]) y que tipo de textura es (GL_TEXTURE_2D)
        glBindTexture(GL_TEXTURE_2D, texture[0]);
        glActiveTexture(GL_TEXTURE1);
        glBindTexture(GL_TEXTURE_2D, texture[1]);

        // Asigna los "filtering"
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        texImage2D(GL_TEXTURE_2D, 0, bmp0, 0);
        texImage2D(GL_TEXTURE_2D, 0, bmp1, 0);

        bmp0.recycle();
        bmp1.recycle();
    }

    public void CrearBuffers() {
        // El vertex buffer
        ByteBuffer bbBackground = ByteBuffer.allocateDirect(coord.getVertices().length * 4);
        bbBackground.order(ByteOrder.nativeOrder());
        vertexBuffer = bbBackground.asFloatBuffer();
        vertexBuffer.put(coord.getVertices());
        vertexBuffer.position(0);

        // Inicializado un byte buffer para la lista de dibujo
        ByteBuffer dlbBackground = ByteBuffer.allocateDirect(coord.getIndices().length * 2);
        dlbBackground.order(ByteOrder.nativeOrder());
        drawListBuffer = dlbBackground.asShortBuffer();
        drawListBuffer.put(coord.getIndices());
        drawListBuffer.position(0);

        // El texture buffer
        ByteBuffer uvBackground = ByteBuffer.allocateDirect(uv.getUv().length * 4);
        uvBackground.order(ByteOrder.nativeOrder());
        uvBuffer = uvBackground.asFloatBuffer();
        uvBuffer.put(uv.getUv());
        uvBuffer.position(0);
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