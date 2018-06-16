uniform mat4 mBackground;
attribute vec4 vPosition;

void main() {
    gl_Position = mBackground * vPosition;
}
