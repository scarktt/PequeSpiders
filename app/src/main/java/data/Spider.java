package data;

public class Spider {
    private float vertices[];
    private short indices[];

    public Spider(){
        // Se crea el arreglo de coordendas
        vertices = new float[] {
                /*-1.24f,-1.92f,
                1.22f,-1.96f,
                2f,-1f,
                2.3f,0.2f,
                2.06f, 1.3f, 0.98f,
                2.32f, -0.14f,
                2.3f, -0.13f,
                2.46f, -0.3f,
                2.47f, -0.31f,
                2.3f, -1.3f,
                2.34f, -2.24f,
                1.4f, -2.46f,
                0.3f, -2.2f,
                -0.92f,1.94f,
                -1.54f, 3.08f,
                -0.3f,3.54f,
                -1.18f, 4.24f,
                -1.28f,4.28f,
                -0.92f, 3.84f,
                -0.82f, 3.06f,
                0.3f,2.34f,
                -0.5f,2.38f,
                -0.24f,3.04f,
                0.46f,3.58f,
                -0.38f, 4.28f,
                -0.36f, 4.28f,
                0.02f, 3.64f,
                0.14f, 3f,
                1.16f,2.3f,
                0.46f, 2.28f,
                0.5f, 2.8f,
                1.28f, 3.28f,
                0.66f, 3.72f,
                0.66f, 3.7f,
                1f, 3.3f,
                1.06f, 3f,2f,
                2.18f, 1.08f,
                -2.14f,0.94f,
                -2.52f,1.38f,
                -3.04f,0.76f,
                -3.38f,0.72f,
                -3.46f,1.04f,
                -3f,1f, -2.5f,
                2.06f, -1.8f,
                1.36f,-2.2f,
                0.78f, -2.56f,
                1.16f, -3.3f,
                -0.02f, -4,0f,
                -4.02f,-0.42f,
                -3.26f,-0.46f,
                -2.72f,0.44f,
                -2.28f,0f,
                -2.26f,-0.26f,
                -2.78f,0.4f,
                -3.38f,-0.9f,
                -4f,-1f,
                -3.96f,-1.44f,
                -3.28f,-1.36f,
                -2.76f,-0.46f,
                -2f,-1f*/
                360f, 1800f, 0.0f, // Esquina superior izquierda
                360f, 1600f, 0.0f, // Esquina inferior izquierda
                700f, 1600f, 0.0f, // Esquina inferior derecha
                700f, 1800f, 0.0f, // Esquina superior derecha
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
