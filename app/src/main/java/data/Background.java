package data;

public class Background {
    private float vertices[];
    private short indices[];

    public Background(){
        // Se crea el arreglo de coordendas
        vertices = new float[] {
                0.0f, 1920f, 0.0f, // Esquina superior izquierda
                0.0f, 0.0f, 0.0f, // Esquina inferior izquierda
                1080f, 0.0f, 0.0f, // Esquina inferior derecha
                1080f, 1920f, 0.0f, // Esquina superior derecha
        };

        // Se reutilizan algunos puntos y para ellos se indica el orden de renderizado
        indices = new short[] {0, 1, 2, 0, 2, 3};

    }

    public float[] getVertices(){
        return vertices;
    }

    public short[] getIndices(){
        return indices;
    }

}
