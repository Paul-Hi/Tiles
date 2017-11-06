attribute vec3 position;
attribute vec2 texCoords;

uniform highp mat4 MVMatrix;
uniform highp mat4 modelMatrix;

varying vec3 FragPos;
varying vec2 TexCoords;

void main()
{
	FragPos =  vec3(modelMatrix * vec4(position, 1.0));
	TexCoords = texCoords;
	gl_Position = MVMatrix * modelMatrix * vec4(position, 1.0);
}