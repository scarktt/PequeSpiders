uniform mat4 u_Matrix;

attribute vec4 v_Position;

attribute vec2 a_texCoord;
varying vec2 v_texCoord;

void main() {
    gl_Position = u_Matrix * v_Position;
    v_texCoord = a_texCoord;
}
