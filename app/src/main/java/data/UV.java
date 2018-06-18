package data;

public class UV {
    private float uv[];

    public UV(){
        // Se crean las coordenadas UV
        /*Siguiendo el siguiente orden:
         *      0.0f, 0.0f, // Esquina inferior izquierda
         *      0.0f, 1.0f, // Esquina superior izquierda
         *      1.0f, 1.0f, // Esquina superior derecha
         *      1.0f, 0.0f, // Esquina inferior derecha
         */
        uv = new float[] {
                //       U     V
                // Coordenadas background
                0.50f, 0.25f,
                0.50f, 0.75f,
                1.0f, 0.75f,
                1.0f, 0.25f,

                // Coordenadas del personaje
                0.0f, 0.25f,
                0.0f, 0.75f,
                0.25f, 0.75f,
                0.25f, 0.25f,

                // Coordenadas spider 1
                0.0f, 0.75f,
                0.0f, 1.0f,
                0.25f, 1.0f,
                0.25f, 0.75f
        };
    }

    public float[] getUv(){
        return uv;
    }
}
