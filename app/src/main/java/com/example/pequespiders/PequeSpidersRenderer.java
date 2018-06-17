package com.example.pequespiders;

import static android.opengl.GLES20.*;
import static android.opengl.GLUtils.*;
import static android.opengl.Matrix.*;

import java.nio.*;
import java.util.Random;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import Shader.Shader;
import Sprite.Sprite;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLSurfaceView.Renderer;
import android.view.MotionEvent;
import data.Background;
import texture.BackgroundUV;
import texture.SpiderUV;
import util.Reader;

public class PequeSpidersRenderer implements Renderer {

    /****************************************************************************************************************/
    /****************************************- VARIABLES E INSTANCIAS -**********************************************/
    /****************************************************************************************************************/
    // MAtrices
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
    float mScreenWidth = 1280;
    float mScreenHeight = 768;
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
    public Background background = new Background();
    public BackgroundUV backgroundUV = new BackgroundUV();
    public SpiderUV spiderUV = new SpiderUV();
    public Reader readerRaw = new Reader();
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

        // Sistema de escalamiento
        SetupScaling();
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        // Sistema de escalamiento
        SetupScaling();
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
        glDrawElements(GL_TRIANGLES, indices.length, GL_UNSIGNED_SHORT, drawListBuffer);

        // Deshabilita los vextex array
        glDisableVertexAttribArray(mPositionHandle);
        glDisableVertexAttribArray(mTexCoordLoc);

    }

    /****************************************************************************************************************/
    /********************************************- TEXTURAS -********************************************************/
    /****************************************************************************************************************/

    public void UpdateSprite() {
        // Get new transformed vertices
        //vertices = sprite.getTransformedVertices();

        // The vertex buffer.
        ByteBuffer bb = ByteBuffer.allocateDirect(vertices.length * 4);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(vertices);
        vertexBuffer.position(0);
    }


    public void SetupImage() {
        // We will use a randomizer for randomizing the textures from texture atlas.
        // This is strictly optional as it only effects the output of our app,
        // Not the actual knowledge.
        Random rnd = new Random();

        // 30 imageobjects times 4 vertices times (u and v)
        uvs = new float[30 * 4 * 2];

        // We will make 30 randomly textures objects
        for (int i = 0; i < 30; i++) {
            int random_u_offset = rnd.nextInt(2);
            int random_v_offset = rnd.nextInt(2);

            // Adding the UV's using the offsets
            uvs[(i * 8) + 0] = random_u_offset * 0.5f;
            uvs[(i * 8) + 1] = random_v_offset * 0.5f;
            uvs[(i * 8) + 2] = random_u_offset * 0.5f;
            uvs[(i * 8) + 3] = (random_v_offset + 1) * 0.5f;
            uvs[(i * 8) + 4] = (random_u_offset + 1) * 0.5f;
            uvs[(i * 8) + 5] = (random_v_offset + 1) * 0.5f;
            uvs[(i * 8) + 6] = (random_u_offset + 1) * 0.5f;
            uvs[(i * 8) + 7] = random_v_offset * 0.5f;
        }

        // The texture buffer
        ByteBuffer bb = ByteBuffer.allocateDirect(uvs.length * 4);
        bb.order(ByteOrder.nativeOrder());
        uvBuffer = bb.asFloatBuffer();
        uvBuffer.put(uvs);
        uvBuffer.position(0);

        // Generate Textures, if more needed, alter these numbers.
        int[] texturenames = new int[1];
        glGenTextures(1, texturenames, 0);

        // Retrieve our image from resources.
        int id = mContext.getResources().getIdentifier("drawable/texturatlas", null, mContext.getPackageName());

        // Temporary create a bitmap
        Bitmap bmp = BitmapFactory.decodeResource(mContext.getResources(), id);

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
        // We will need a randomizer
        Random rnd = new Random();

        // Our collection of vertices
        vertices = new float[30 * 4 * 3];

        // Create the vertex data
        for (int i = 0; i < 30; i++) {
            int offset_x = rnd.nextInt((int) swp);
            int offset_y = rnd.nextInt((int) shp);

            // Create the 2D parts of our 3D vertices, others are default 0.0f
            vertices[(i * 12) + 0] = offset_x;
            vertices[(i * 12) + 1] = offset_y + (30.0f * ssu);
            vertices[(i * 12) + 2] = 0f;
            vertices[(i * 12) + 3] = offset_x;
            vertices[(i * 12) + 4] = offset_y;
            vertices[(i * 12) + 5] = 0f;
            vertices[(i * 12) + 6] = offset_x + (30.0f * ssu);
            vertices[(i * 12) + 7] = offset_y;
            vertices[(i * 12) + 8] = 0f;
            vertices[(i * 12) + 9] = offset_x + (30.0f * ssu);
            vertices[(i * 12) + 10] = offset_y + (30.0f * ssu);
            vertices[(i * 12) + 11] = 0f;
        }

        // The indices for all textured quads
        indices = new short[30 * 6];
        int last = 0;
        for (int i = 0; i < 30; i++) {
            // We need to set the new indices for the new quad
            indices[(i * 6) + 0] = (short) (last + 0);
            indices[(i * 6) + 1] = (short) (last + 1);
            indices[(i * 6) + 2] = (short) (last + 2);
            indices[(i * 6) + 3] = (short) (last + 0);
            indices[(i * 6) + 4] = (short) (last + 2);
            indices[(i * 6) + 5] = (short) (last + 3);

            // Our indices are connected to the vertices so we need to keep them
            // in the correct order.
            // normal quad = 0,1,2,0,2,3 so the next one will be 4,5,6,4,6,7
            last = last + 4;
        }

        // The vertex buffer.
        ByteBuffer bb = ByteBuffer.allocateDirect(vertices.length * 4);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(vertices);
        vertexBuffer.position(0);

        // initialize byte buffer for the draw list
        ByteBuffer dlb = ByteBuffer.allocateDirect(indices.length * 2);
        dlb.order(ByteOrder.nativeOrder());
        drawListBuffer = dlb.asShortBuffer();
        drawListBuffer.put(indices);
        drawListBuffer.position(0);
    }

    /****************************************************************************************************************/
    /***************************************- ESCALACIÓN Y TOUCH -**************************************************/
    /****************************************************************************************************************/

    public void SetupScaling() {
        // The screen resolutions
        swp = (int) (mContext.getResources().getDisplayMetrics().widthPixels);
        shp = (int) (mContext.getResources().getDisplayMetrics().heightPixels);

        // Orientation is assumed portrait
        ssx = swp / 320.0f;
        ssy = shp / 480.0f;

        // Get our uniform scaler
        if (ssx > ssy)
            ssu = ssy;
        else
            ssu = ssx;
    }

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