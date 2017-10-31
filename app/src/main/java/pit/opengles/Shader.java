package pit.opengles;

import android.content.Context;
import android.opengl.GLES30;

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
        int vs = Create(vsSrc, GLES30.GL_VERTEX_SHADER);
        int fs = Create(fsSrc, GLES30.GL_FRAGMENT_SHADER);

        int program = GLES30.glCreateProgram();
        if (program != 0)

            GLES30.glAttachShader(program, vs);
            GLES30.glAttachShader(program, fs);


            GLES30.glLinkProgram(program);

            final int[] linkStatus = new int[1];
            GLES30.glGetProgramiv(program, GLES30.GL_LINK_STATUS, linkStatus, 0);

            if (linkStatus[0] == 0)
            {
                GLES30.glDeleteProgram(program);
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
        int id = GLES30.glCreateShader(shaderType);

        if(id != 0) {
            GLES30.glShaderSource(id, shaderSource);

            GLES30.glCompileShader(id);

            final int[] compileStatus = new int[1];
            GLES30.glGetShaderiv(id, GLES30.GL_COMPILE_STATUS, compileStatus, 0);

            if (compileStatus[0] == 0)
            {
                GLES30.glDeleteShader(id);
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
