package data;

import Shader.Shader;

import static android.opengl.GLES20.*;
import static android.opengl.GLES20.glDisableVertexAttribArray;

public class BindData {
    public Buffers buffer = new Buffers();
    public Coord coord = new Coord();

    public void bindData(float[] m) {
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
        glVertexAttribPointer(mPositionHandle, 3, GL_FLOAT, false, 0, buffer.getVertexData());

        // Para pasar las coordenadas de textura al shader se necesita ubicación de la variable del
        // fragment shader "A_TEXCOORD" y esta ubicación se obtiene y se almacena en "mTexCoordLoc"
        int mTexCoordLoc = glGetAttribLocation(Shader.program_Image, Shader.INPUT_TEXCOORD);
        glVertexAttribPointer(mTexCoordLoc, 2, GL_FLOAT, false, 0, buffer.getUvData());
        glEnableVertexAttribArray(mTexCoordLoc);

        // Obtiene la ubicación de la variable del shader al que se transferirán los datos
        int mtrxhandle = glGetUniformLocation(Shader.program_Image, Shader.U_MATRIX);

        // Se le indica que aplique la proyeción y transformación
        glUniformMatrix4fv(mtrxhandle, 1, false, m, 0);

        // Se obtiene la ubicación de la variable "U_TEXTURE" de tipo uniform (variable que almacena la información
        // de la textura)
        int ubicacion1 = glGetUniformLocation(Shader.program_Image, Shader.U_TEXTURE);

        // Para pasar la información de la textura al shader se debe de especificar el número de unidad
        // de textura que usamos (no el objeto de textura, por ejemplo: de GL_TEXTURE0 se toma el 0 y de GL_TEXTURE1 el 1)
        // y además asociar la textura que queramos a esa unidad.
        glUniform1i(ubicacion1, 0);

        // Se manda a dibujar
        glDrawElements(GL_TRIANGLES, coord.getIndices().length, GL_UNSIGNED_SHORT, buffer.getDrawListBuffer());

        // Deshabilita los vextex array
        glDisableVertexAttribArray(mPositionHandle);
        glDisableVertexAttribArray(mTexCoordLoc);
    }
}
