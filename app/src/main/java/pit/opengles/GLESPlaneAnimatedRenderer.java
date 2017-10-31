package pit.opengles;

import android.content.Context;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.os.SystemClock;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Random;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by paulh on 13.10.2017.
 */

public class GLESPlaneAnimatedRenderer implements GLSurfaceView.Renderer {

    private Context _mContext;
    private Shader _mShader;
    private FloatBuffer _mVertexBuffer;
    private FloatBuffer _mModelBuffer;
    private FloatBuffer _mColorBuffer;
    private Vector3f _mColorCorrection = new Vector3f(0, 0, 0);
    private float _mAnimationSpeed = 1.0f;
    private Camera _mCamera;
    private Vector3f _mLightPosition;
    private Transform _mLightTransform;
    private final int sizeOfFloat = 4;
    private boolean straight = true, wave = false;

    private Plane plane;
    private int c = 0;
    private Random rand;

    public GLESPlaneAnimatedRenderer(Context context)
    {
        _mContext = context;
    }

    @Override
    public void onSurfaceCreated(GL10 notUsed, EGLConfig config)
    {
        _mShader = new Shader(_mContext);
        _mLightPosition = new Vector3f(0, 0, 1);
        _mLightTransform = new Transform(_mLightPosition.x, _mLightPosition.y, _mLightPosition.z, 1, 1, 1, 0.1f,0.1f ,0.1f ,0);
        _mCamera = new Camera();
        rand = new Random(9);
        plane = new Plane();

        GLES30.glEnable(GLES30.GL_DEPTH_TEST);
        GLES30.glEnable(GLES30.GL_CULL_FACE);
        GLES30.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

        createVisuals();
    }

    public void createVisuals()
    {
        c = 0;
        //vertices
        float[] vertices = plane.vertices;
        _mVertexBuffer = floatToBuffer(vertices);
        //modelMatrix
        float scale = 0.13f;
        float spacing = 0.34f;
        Transform[] transforms = new Transform[70 * 62];
        for(float x = 35; x > -35; x--)
        {
            for(float y = 31; y > -31; y--)
            {
                transforms[c++] = new Transform(x * spacing, y * spacing, 1, 1, 1, 1, 1 * scale, 1 * scale, 1 * scale, 0);
            }
        }
        float[] mMs = new float[0];
        for(int i = 0; i < c; i++)
        {
            mMs = concat(mMs, transforms[i].getModelMatrix());
        }
        _mModelBuffer = floatToBuffer(mMs);

        float[] colors = new float[c * 3];


        for(int i = 0; i < colors.length; i++)
        {
            colors[i] = rand.nextFloat();

        }
        _mColorBuffer = floatToBuffer(colors);

        _mVertexBuffer.position(0);
        GLES30.glEnableVertexAttribArray(0);
        GLES30.glVertexAttribPointer(0, 3, GLES30.GL_FLOAT, false, 0, _mVertexBuffer);

        int position = 0;
        _mModelBuffer.position(position);
        GLES30.glEnableVertexAttribArray(1);
        GLES30.glVertexAttribPointer(1, 4, GLES30.GL_FLOAT, false, sizeOfFloat * 16, _mModelBuffer);
        position += 4;
        _mModelBuffer.position(position);
        GLES30.glEnableVertexAttribArray(2);
        GLES30.glVertexAttribPointer(2, 4, GLES30.GL_FLOAT, false, sizeOfFloat * 16, _mModelBuffer);
        position += 4;
        _mModelBuffer.position(position);
        GLES30.glEnableVertexAttribArray(3);
        GLES30.glVertexAttribPointer(3, 4, GLES30.GL_FLOAT, false, sizeOfFloat * 16, _mModelBuffer);
        position += 4;
        _mModelBuffer.position(position);
        GLES30.glEnableVertexAttribArray(4);
        GLES30.glVertexAttribPointer(4, 4, GLES30.GL_FLOAT, false, sizeOfFloat * 16, _mModelBuffer);

        _mColorBuffer.position(0);
        GLES30.glEnableVertexAttribArray(5);
        GLES30.glVertexAttribPointer(5, 3, GLES30.GL_FLOAT, false, 0, _mColorBuffer);

        GLES30.glVertexAttribDivisor(1, 1);
        GLES30.glVertexAttribDivisor(2, 1);
        GLES30.glVertexAttribDivisor(3, 1);
        GLES30.glVertexAttribDivisor(4, 1);
        GLES30.glVertexAttribDivisor(5, 1);
    }

    @Override
    public void onSurfaceChanged(GL10 notUsed, int width, int height)
    {
        GLES30.glViewport(0, 0, width, height);
        _mCamera.onSurfaceChanged(width, height);
    }

    private long time = 0;

    @Override
    public void onDrawFrame(GL10 notUsed)
    {
        time = SystemClock.uptimeMillis();
        float currentTime = time/1000.0f;
        GLES30.glClear(GLES30.GL_DEPTH_BUFFER_BIT | GLES30.GL_COLOR_BUFFER_BIT);

        if(straight) moveStraight(currentTime);
        else if(wave) moveWave(currentTime);

        DrawModelInstanced();
    }

    private void moveStraight(float t)
    {
        _mLightPosition.y = (float)Math.sin(t * _mAnimationSpeed) * 4;
        _mLightTransform.setPosition(_mLightPosition.x, _mLightPosition.y, _mLightPosition.z);
    }

    private void moveWave(float t)
    {
        _mLightPosition.y = (float)Math.sin(t * _mAnimationSpeed) * 4;
        _mLightPosition.x  = (float)Math.sin(t * _mAnimationSpeed * 2);
        _mLightTransform.setPosition(_mLightPosition.x, _mLightPosition.y, _mLightPosition.z);
    }
    private void DrawModelInstanced()
    {
        GLES30.glUseProgram(_mShader.getMainProgram());

        float[] MVPMatrix = new float[16];

        android.opengl.Matrix.multiplyMM(MVPMatrix, 0, _mCamera.getProjectionMatrix(), 0, _mCamera.getViewMatrix(), 0);

        GLES30.glUniformMatrix4fv(GLES30.glGetUniformLocation(_mShader.getMainProgram(), "MVMatrix"), 1, false, MVPMatrix, 0);

        GLES30.glUniform3fv(GLES30.glGetUniformLocation(_mShader.getMainProgram(), "lightPosition"), 1,_mLightPosition.get(), 0);
        GLES30.glUniform3fv(GLES30.glGetUniformLocation(_mShader.getMainProgram(), "colorCorrection"), 1,_mColorCorrection.get(), 0);

        GLES30.glDrawArraysInstanced(GLES30.GL_TRIANGLES, 0, 6, 70 * 62);
    }

    private FloatBuffer floatToBuffer(float[] array)
    {
        FloatBuffer fb = ByteBuffer.allocateDirect(array.length * sizeOfFloat).order(ByteOrder.nativeOrder()).asFloatBuffer();
        fb.put(array).position(0);

        return fb;
    }

    static float[] concat(float[]... floatArrays) {
        int length = 0;
        for (float[] array : floatArrays) {
            length += array.length;
        }
        float[] result = new float[length];
        int position = 0;
        for (float[] array : floatArrays) {
            for (float element : array) {
                result[position] = element;
                position++;
            }
        }
        return result;
    }

    public void switchColors(Vector3f newColorCorrection)
    {
        _mColorCorrection = newColorCorrection;
    }

    public void changeAnimationSpeed(float newSpeed)
    {
        _mAnimationSpeed = newSpeed;
    }

    public void changeMotion(String motion)
    {
        if(motion.matches("straight"))
        {
            straight = true;
            wave = false;
        }
        else if(motion.matches("wave"))
        {
            straight = false;
            wave = true;
        }
    }

}