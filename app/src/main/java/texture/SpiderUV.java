package texture;

public class SpiderUV {
    private float uvSpider[];

    public SpiderUV(){
        // Se crean las coordenadas UV
        uvSpider = new float[] {
                0.0f, 0.0f,
                0.0f, 1.0f,
                1.0f, 1.0f,
                1.0f, 0.0f
        };
    }

    public float[] getUv(){
        return uvSpider;
    }
}
