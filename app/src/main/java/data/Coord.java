package data;

public class Coord {
    private float vertices[];
    private short indices[];

    public Coord(){
        // Se crea el arreglo de coordendas
        vertices = new float[] {
                0.0f, 1920f, 0.0f, // Esquina superior izquierda
                0.0f, 0.0f, 0.0f, // Esquina inferior izquierda
                1080f, 0.0f, 0.0f, // Esquina inferior derecha
                1080f, 1920f, 0.0f, // Esquina superior derecha

                360f, 1800f, 0.0f, // Esquina superior izquierda
                360f, 1600f, 0.0f, // Esquina inferior izquierda
                700f, 1600f, 0.0f, // Esquina inferior derecha
                700f, 1800f, 0.0f, // Esquina superior derecha
        };

        // Se reutilizan algunos puntos y para ellos se indica el orden de renderizado
        //indices = new short[] {0, 1, 2, 0, 2, 3};

        // The indices for all textured quads
        indices = new short[2*6];
        int last = 0;
        for(int i=0;i<2;i++) {
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

    }

    public float[] getVertices(){
        return vertices;
    }

    public short[] getIndices(){
        return indices;
    }

}
