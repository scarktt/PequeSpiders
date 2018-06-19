package Shader;

import android.content.Context;
import com.example.pequespiders.R;
import util.Reader;

import static android.opengl.GLES20.*;

public class CompilarShader {
    public Reader readerRaw = new Reader();

    public void Compilar(Context mContext){
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
}
