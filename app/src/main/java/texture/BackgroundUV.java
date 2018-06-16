package texture;

public class BackgroundUV {
    private float uv[];

    public BackgroundUV(){
        // Se crean las coordenadas UV
        uv = new float[] {
                0.0f, 0.0f,
                0.0f, 1.0f,
                1.0f, 1.0f,
                1.0f, 0.0f
        };
    }

    public float[] getUv(){
        return uv;
    }
}
