#version 300 es

precision mediump float;

uniform vec3 colorCorrection;

in vec3 FragPos;
in vec3 Color;
in float light;

layout(location=0) out vec4 fragColor;

void main()
{
    if(light < 0.02) discard;
    float bloom = light;
    if(light > 0.98) bloom *= (light*1.1);
	fragColor = vec4(Color + colorCorrection, 1.0) * bloom;
}