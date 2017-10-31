package pit.opengles;

import android.content.Context;
import android.opengl.GLES20;

/**
 * Created by paulh on 11.10.2017.
 */

public class Shader
{
    private Context mContext;
    private int mProgram;

	public Shader(Context context)
    {
        mContext = context;
        String vertexShader = ResourceLoader.readShader(mContext, R.raw.mainvertexshader);
        String fragmentShader = ResourceLoader.readShader(mContext, R.raw.mainfragmenthader);
        mProgram = init(vertexShader, fragmentShader);
    }

    public int init(String vsSrc, String fsSrc)
    {
        int vs = Create(vsSrc, GLES20.GL_VERTEX_SHADER);
        int fs = Create(fsSrc, GLES20.GL_FRAGMENT_SHADER);

        int program = GLES20.glCreateProgram();


        if (program != 0)

            GLES20.glAttachShader(program, vs);
            GLES20.glAttachShader(program, fs);

            GLES20.glBindAttribLocation(program, 0, "position");
            GLES20.glBindAttribLocation(program, 1, "texCoords");

            GLES20.glLinkProgram(program);

            final int[] linkStatus = new int[1];
            GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linkStatus, 0);

            if (linkStatus[0] == 0)
            {
                GLES20.glDeleteProgram(program);
                program = 0;
            }
        if (program == 0)
        {
            throw new RuntimeException("Error creating program");
        }
        return program;
    }

    private int Create(String shaderSource, int shaderType)
    {
        int id = GLES20.glCreateShader(shaderType);

        if(id != 0) {
            GLES20.glShaderSource(id, shaderSource);

            GLES20.glCompileShader(id);

            final int[] compileStatus = new int[1];
            GLES20.glGetShaderiv(id, GLES20.GL_COMPILE_STATUS, compileStatus, 0);

            if (compileStatus[0] == 0)
            {
                GLES20.glDeleteShader(id);
                id = 0;
            }
        }
        if (id == 0) {
            throw new RuntimeException("Shader: Creation failed");
        }
        return id;
    }

    public int getMainProgram(){ return mProgram;}
}
