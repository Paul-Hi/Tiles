precision mediump float;

uniform vec3 lightPosition;
uniform sampler2D mask;
uniform sampler2D texture;

varying vec3 FragPos;
varying vec2 TexCoords;


void main()
{
	float dist = length(FragPos - lightPosition);
	float light = 0.02 / ((dist * (dist)));
	if(light > 0.99) light * 2.0;
	gl_FragColor = mix((texture2D(texture, TexCoords)), vec4(0.0), (texture2D(mask, TexCoords)).a) * light;
}