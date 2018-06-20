package data;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class Buffers {

    private FloatBuffer vertexData;
    private ShortBuffer drawListBuffer;
    private FloatBuffer uvData;

    private Coord coord = new Coord();
    private UV uv = new UV();

    public Buffers(){
        // El vertex buffer
        ByteBuffer bb = ByteBuffer.allocateDirect(coord.getVertices().length * 4);
        bb.order(ByteOrder.nativeOrder());
        vertexData = bb.asFloatBuffer();
        vertexData.put(coord.getVertices());
        vertexData.position(0);

        // Inicializado un byte buffer para la lista de dibujo
        ByteBuffer dlb = ByteBuffer.allocateDirect(coord.getIndices().length * 2);
        dlb.order(ByteOrder.nativeOrder());
        drawListBuffer = dlb.asShortBuffer();
        drawListBuffer.put(coord.getIndices());
        drawListBuffer.position(0);

        // El texture buffer
        ByteBuffer uvb = ByteBuffer.allocateDirect(uv.getUv().length * 4);
        uvb.order(ByteOrder.nativeOrder());
        uvData = uvb.asFloatBuffer();
        uvData.put(uv.getUv());
        uvData.position(0);
    }

    public FloatBuffer getVertexData(){
        return vertexData;
    }

    public ShortBuffer getDrawListBuffer(){
        return drawListBuffer;
    }

    public FloatBuffer getUvData(){
        return uvData;
    }
}
