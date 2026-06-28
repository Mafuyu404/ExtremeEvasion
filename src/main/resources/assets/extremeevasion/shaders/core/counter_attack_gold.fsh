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

    vec3 gold = vec3(1.25, 0.78, 0.10);
    vec3 textureColor = texel.rgb * 0.78;
    vec3 color = (textureColor + gold) * ColorModulator.rgb;
    float alpha = texel.a * 0.32 * vertexColor.a * ColorModulator.a;
    fragColor = vec4(color, alpha);
}
