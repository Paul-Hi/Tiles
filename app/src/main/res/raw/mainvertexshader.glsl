#version 300 es

layout(location=0) in vec3 position;
layout(location=1) in mat4 modelMatrix;
layout(location=5) in vec3 color;

uniform highp mat4 MVMatrix;
uniform highp vec3 lightPosition;

out vec3 FragPos;
out vec3 Color;
out float light;

void main()
{
	FragPos = vec3(modelMatrix * vec4(position, 1.0));
	float dist = length(FragPos - lightPosition);
	light = 1.0 / (dist * (dist/2.0));
    Color = color;
	gl_Position = MVMatrix * modelMatrix * vec4(position, 1.0);
}