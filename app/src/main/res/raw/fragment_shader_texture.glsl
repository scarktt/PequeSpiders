precision mediump float;
varying vec2 v_texCoord;
uniform sampler2D b_texture;
uniform sampler2D s_texture;
vec4 col1;
vec4 col2;

void main() {
    /*col1 = texture2D(b_texture, v_texCoord );
    col2 = texture2D(s_texture, v_texCoord );
    //gl_FragColor = col1 + col2;
    gl_FragColor = col1 * col2;
    gl_FragColor = vec4(col1.rgb * (1.0f - col2.a) + col2.rgb * col2.a, 1.0f);*/
    gl_FragColor = texture2D(b_texture, v_texCoord);
}
