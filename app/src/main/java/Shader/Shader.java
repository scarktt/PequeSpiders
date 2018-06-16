package Shader;

import static android.opengl.GLES20.*;
import android.util.Log;

public class Shader {

    public static int program_SolidColor;
    public static int program_Image;

    public static int loadShader(int type, String shaderCode){

        // Se crea el tipo de vertex shader
        // O el tipo de fragment shader
        int shader = glCreateShader(type);

        if (shader != 0){
            // Se agrega el códifo al shader para compilarlo
            glShaderSource(shader, shaderCode);
            glCompileShader(shader);

            // Obtiene el estatus de compilación
            final int[] compileStatus = new int[1];
            glGetShaderiv(shader, GL_COMPILE_STATUS, compileStatus, 0);

            // Si la compilación es igual a 0 significa que falló y se elimina el shader
            if (compileStatus[0] == 0)
            {
                Log.e("Shader", "Error al compilar el shader: " + glGetShaderInfoLog(shader));
                glDeleteShader(shader);
                shader = 0;
            }
        }

        if (shader == 0) {
            throw new RuntimeException("Error al crear el shader.");
        }

        return shader;
    }
}
