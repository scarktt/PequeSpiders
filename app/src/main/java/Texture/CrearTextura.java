package Texture;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import static android.opengl.GLES20.*;
import static android.opengl.GLUtils.texImage2D;

public class CrearTextura {

    public void CrearTextureAtlas(Context mContext){
        // Se crea un arreglo para almacenar cada id de textura
        int[] texture = new int[1];
        // Genera un id para cada textura, el primer argumento es el numero de ids, el segundo el array de textura
        // Y el ultimo algumento inidica el ínidice inicial del arreglo.
        glGenTextures(1, texture, 0);

        int[] id = new int[1];

        // Retorna la imagen (textura desde los archivos del proyecto)
        id[0] = mContext.getResources().getIdentifier("drawable/textureatlas", null, mContext.getPackageName());

        // Se crea temporalmente un bitmap
        Bitmap bmp0 = BitmapFactory.decodeResource(mContext.getResources(), id[0]);

        // Activamos una textura
        glActiveTexture(GL_TEXTURE0);
        // Se indica que textura que se va utilizar (texture[0]) y que tipo de textura es (GL_TEXTURE_2D)
        glBindTexture(GL_TEXTURE_2D, texture[0]);

        // Asigna los "filtering"
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        texImage2D(GL_TEXTURE_2D, 0, bmp0, 0);

        bmp0.recycle();
    }
}
