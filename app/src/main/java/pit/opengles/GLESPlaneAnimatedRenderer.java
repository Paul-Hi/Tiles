package pit.opengles;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.SystemClock;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by paulh on 13.10.2017.
 */

public class GLESPlaneAnimatedRenderer implements GLSurfaceView.Renderer {

    private Context _mContext;
    private Shader _mShader;
    private FloatBuffer _mVertexBuffer;
    private FloatBuffer _mTexCoordBuffer;
    private float _mAnimationSpeed = 0.2f;
    private Camera _mCamera;
    private Vector3f _mLightPosition = new Vector3f(0, 0, -1);;
    private Vector3f _mPlanePosition = new Vector3f(0, 0, 0);
    private Transform _mLightTransform = new Transform(_mLightPosition.x, _mLightPosition.y, _mLightPosition.z, 1, 1, 1, 0.1f,0.1f ,0.1f ,0);
    private Transform _mPlaneTransform = new Transform(_mPlanePosition.x, _mPlanePosition.y, _mPlanePosition.z, 1, 1, 1,1.25f, 1.25f,1 ,0);
    private final int sizeOfFloat = 4;
    private int _mPumkin = 0;
    private int _mColorful = 0;
    private int _mRed = 0;
    private int _mGreen = 0;
    private int _mBlue = 0;
    private int _mTexture = 0;
    private int _mMask = 0;
    private boolean red = false, blue = false, green = false, colorful  = true, pumkin = false;
    private boolean straight = true, wave = false;


    private Plane plane;

    public GLESPlaneAnimatedRenderer(Context context)
    {
        _mContext = context;
    }

    @Override
    public void onSurfaceCreated(GL10 notUsed, EGLConfig config)
    {
        _mShader = new Shader(_mContext);
        _mCamera = new Camera();
        plane = new Plane();


        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glEnable(GLES20.GL_CULL_FACE);
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

        createVisuals();
    }

    public void createVisuals()
    {
        //vertices
        float[] vertices = plane.vertices;
        _mVertexBuffer = floatToBuffer(vertices);
        //texCoords
        float[] texCoords = plane.texCoords;
        _mTexCoordBuffer = floatToBuffer(texCoords);

        _mVertexBuffer.position(0);
        GLES20.glEnableVertexAttribArray(0);
        GLES20.glVertexAttribPointer(0, 3, GLES20.GL_FLOAT, false, 0, _mVertexBuffer);

        _mTexCoordBuffer.position(0);
        GLES20.glEnableVertexAttribArray(1);
        GLES20.glVertexAttribPointer(1, 2, GLES20.GL_FLOAT, false, 0, _mTexCoordBuffer);

        _mMask = ResourceLoader.loadTexture(_mContext, R.drawable.newmask);
        _mRed = ResourceLoader.loadTexture(_mContext, R.drawable.newred);
        _mBlue = ResourceLoader.loadTexture(_mContext, R.drawable.newblue);
        _mGreen = ResourceLoader.loadTexture(_mContext, R.drawable.newgreen);
        _mColorful = ResourceLoader.loadTexture(_mContext, R.drawable.newcolorful);
        _mPumkin = ResourceLoader.loadTexture(_mContext, R.drawable.pumkin);

        if(red)
            _mTexture = _mRed;
        else if(green)
            _mTexture = _mGreen;
        else if(blue)
            _mTexture = _mBlue;
        else if(colorful)
            _mTexture = _mColorful;
        else if(pumkin)
            _mTexture =  _mPumkin;
    }

    @Override
    public void onSurfaceChanged(GL10 notUsed, int width, int height)
    {
        GLES20.glViewport(0, 0, width, height);
        _mCamera.onSurfaceChanged(width, height);
    }

    private long time = 0;

    @Override
    public void onDrawFrame(GL10 notUsed)
    {
        time = SystemClock.uptimeMillis();
        float currentTime = time/1000.0f;
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);

        if(straight) moveStraight(currentTime);
        else if(wave) moveWave(currentTime);

        DrawModel();
    }

    private void moveStraight(float t)
    {
        _mLightPosition.y = (float)Math.sin(t * _mAnimationSpeed) * 0.5f;
        _mLightPosition.x = 0.0f;
        _mLightTransform.setPosition(_mLightPosition.x, _mLightPosition.y, _mLightPosition.z);
    }

    private void moveWave(float t)
    {
        _mLightPosition.y = (float)Math.sin(t * _mAnimationSpeed) * 0.5f;
        _mLightPosition.x  = (float)Math.sin(t * _mAnimationSpeed * 2) * 0.25f;
        _mLightTransform.setPosition(_mLightPosition.x, _mLightPosition.y, _mLightPosition.z);
    }
    private void DrawModel()
    {
        GLES20.glUseProgram(_mShader.getMainProgram());

        float[] MVPMatrix = new float[16];

        android.opengl.Matrix.multiplyMM(MVPMatrix, 0, _mCamera.getProjectionMatrix(), 0, _mCamera.getViewMatrix(), 0);

        GLES20.glUniformMatrix4fv(GLES20.glGetUniformLocation(_mShader.getMainProgram(), "MVMatrix"), 1, false, MVPMatrix, 0);

        GLES20.glUniformMatrix4fv(GLES20.glGetUniformLocation(_mShader.getMainProgram(), "modelMatrix"), 1, false, _mPlaneTransform.getModelMatrix(), 0);

        GLES20.glUniform3fv(GLES20.glGetUniformLocation(_mShader.getMainProgram(), "lightPosition"), 1,_mLightPosition.get(), 0);


        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, _mMask);
        GLES20.glUniform1i(GLES20.glGetUniformLocation(_mShader.getMainProgram(), "mask"), 0);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, _mTexture);
        GLES20.glUniform1i(GLES20.glGetUniformLocation(_mShader.getMainProgram(), "texture"), 1);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6);
    }

    public void parallaxMove(float x, float y, boolean reversed)
    {
        if (_mContext.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
        {
            float temp;
            if (reversed)
            {
                temp = y;
                y = -x;
            }
            else
            {
                temp = -y;
                y = x;
            }
            x = temp;
        }
        _mPlanePosition.x += (x * (0.002f));
        _mPlanePosition.y -= (y * (0.002f));
        _mPlaneTransform.setPosition(_mPlanePosition.x, _mPlanePosition.y, _mPlanePosition.z);
    }

    public void resetParallax()
    {
        _mPlanePosition.x = 0;
        _mPlanePosition.y = 0;
        _mPlaneTransform.setPosition(_mPlanePosition.x, _mPlanePosition.y, _mPlanePosition.z);
    }

    private FloatBuffer floatToBuffer(float[] array)
    {
        FloatBuffer fb = ByteBuffer.allocateDirect(array.length * sizeOfFloat).order(ByteOrder.nativeOrder()).asFloatBuffer();
        fb.put(array).position(0);

        return fb;
    }

    public void switchColors(String newColor)
    {
        switch (newColor)
        {
            case "red":
                _mTexture = _mRed;
                red = true;
                blue = green = colorful = pumkin = false;
                break;
            case "blue":
                _mTexture = _mBlue;
                blue = true;
                red = green = colorful = pumkin = false;
                break;
            case "green":
                _mTexture = _mGreen;
                green = true;
                red = blue= colorful = pumkin = false;
                break;
            case "colorful":
                _mTexture = _mColorful;
                colorful = true;
                red = green = blue = pumkin = false;
                break;
            case "pumkin":
                _mTexture = _mPumkin;
                pumkin = true;
                red = green = blue = colorful = false;
                break;
        }
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