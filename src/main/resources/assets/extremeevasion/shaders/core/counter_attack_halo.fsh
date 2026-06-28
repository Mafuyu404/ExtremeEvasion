#version 150

uniform sampler2D Sampler0;
uniform vec4 ColorModulator;

in vec2 texCoord0;
in vec4 vertexColor;

out vec4 fragColor;

void main() {
    vec4 texel = texture(Sampler0, texCoord0);
    if (texel.a < 0.1) {
        discard;
    }

    vec3 color = vec3(2.8, 1.85, 0.18) * ColorModulator.rgb;
    float alpha = texel.a * 0.34 * vertexColor.a * ColorModulator.a;
    fragColor = vec4(color, alpha);
}
