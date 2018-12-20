precision mediump float;

uniform vec3 lightPosition;
uniform sampler2D mask;
uniform sampler2D texture;
uniform sampler2D bumptexture;
uniform vec2 offset;

varying vec3 FragPos;
varying vec2 TexCoords;


void main()
{
	mat3 tbn = (mat3(vec3(1,0,0), vec3(0,1,0), vec3(0,0,-1)));
	vec3 norm = tbn * normalize(texture2D(bumptexture, TexCoords + offset ).rgb * 2.0 - 1.0);
	vec3 lightPar = vec3(lightPosition.xy + (offset * 0.8), lightPosition.z);
	float dist = length(FragPos - lightPar);
    vec3 lightDir = normalize(lightPar - vec3(FragPos.x, FragPos.y, 0));
    float diff = max(dot(norm, lightDir), 0.0);
	float light = (diff * 0.02) / ((dist * (dist)));
	gl_FragColor = mix((texture2D(texture, TexCoords + offset))* light, texture2D(mask, TexCoords + offset), (texture2D(mask, TexCoords + offset)).a);
}