#version 150

uniform sampler2D DiffuseSampler;
uniform float IntensityAmount;
uniform vec2 OutSize;

in vec2 texCoord;

out vec4 fragColor;

vec3 sampleGaussian(vec2 uv, vec2 texel, float radius) {
    vec3 sum = texture(DiffuseSampler, uv).rgb * 0.227027;
    sum += texture(DiffuseSampler, uv + vec2(texel.x * radius, 0.0)).rgb * 0.1216216;
    sum += texture(DiffuseSampler, uv - vec2(texel.x * radius, 0.0)).rgb * 0.1216216;
    sum += texture(DiffuseSampler, uv + vec2(0.0, texel.y * radius)).rgb * 0.1216216;
    sum += texture(DiffuseSampler, uv - vec2(0.0, texel.y * radius)).rgb * 0.1216216;
    sum += texture(DiffuseSampler, uv + vec2(texel.x * radius, texel.y * radius)).rgb * 0.071054;
    sum += texture(DiffuseSampler, uv + vec2(-texel.x * radius, texel.y * radius)).rgb * 0.071054;
    sum += texture(DiffuseSampler, uv + vec2(texel.x * radius, -texel.y * radius)).rgb * 0.071054;
    sum += texture(DiffuseSampler, uv - vec2(texel.x * radius, texel.y * radius)).rgb * 0.071054;
    return sum;
}

void main() {
    vec4 color = texture(DiffuseSampler, texCoord);
    float intensity = clamp(IntensityAmount, 0.0, 1.0);

    vec2 centerOffset = texCoord - vec2(0.5);
    centerOffset.x *= OutSize.x / max(OutSize.y, 1.0);
    vec2 ellipseOffset = vec2(centerOffset.x * 0.72, centerOffset.y * 1.28);
    float ellipseDistance = length(ellipseOffset);
    float focusMask = smoothstep(0.24, 0.62, ellipseDistance);
    float outerMask = smoothstep(0.48, 0.70, ellipseDistance);
    vec2 texel = 1.0 / max(OutSize, vec2(1.0));
    float blurRadius = mix(7.0, 14.0, focusMask);
    vec3 blurred = sampleGaussian(texCoord, texel, blurRadius);
    vec3 focusedSource = mix(color.rgb, blurred, clamp(focusMask * intensity * 1.0, 0.0, 1.0));

    vec3 linearColor = pow(focusedSource, vec3(2.2));
    float grayLinear = dot(linearColor, vec3(0.2126, 0.7152, 0.0722));
    vec3 grayColor = pow(vec3(grayLinear), vec3(1.0 / 2.2));
    vec3 desaturated = mix(focusedSource, grayColor, 0.55);
    vec3 blueGray = vec3(0.60, 0.72, 0.94);
    vec3 tinted = desaturated * blueGray + vec3(0.026, 0.04, 0.075);
    float gamma = mix(0.88, 0.68, focusMask * intensity);
    tinted = pow(clamp(tinted, 0.0, 1.0), vec3(gamma));
    tinted = mix(tinted, vec3(1.0), outerMask * intensity * 0.20);
    vec3 finalColor = mix(color.rgb, tinted, intensity);
    fragColor = vec4(finalColor, 1.0);
}
