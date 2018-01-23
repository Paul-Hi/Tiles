package pit.opengles;

import android.content.Context;
import android.content.res.Configuration;
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
    private float _mAnimationSpeed = 0.5f;
    private Camera _mCamera;
    private Vector3f _mLightPosition = new Vector3f(0, 0, -1);;
    private Vector3f _mPlanePosition = new Vector3f(0, 0, 0);
    private Transform _mLightTransform = new Transform(_mLightPosition.x, _mLightPosition.y, _mLightPosition.z, 1, 1, 1, 0.1f,0.1f ,0.1f ,0);
    private Transform _mPlaneTransform = new Transform(_mPlanePosition.x, _mPlanePosition.y, _mPlanePosition.z, 1, 1, 1,1.25f, 1.25f,1 ,0);
    private final int sizeOfFloat = 4;
    private int _mAutumn = 0;
    private int _mPink = 0;
    private int _mWinterWonderland = 0;
    private int _mColorful = 0;
    private int _mRed = 0;
    private int _mGreen = 0;
    private int _mBlue = 0;
    private int _mTexture = 0;
    private int _mMask = 0;
    private int _mBump = 0;
    private boolean red = false, blue = false, green = false, colorful  = true, pink = false, autumn = false, winterwonderland = false;
    private boolean straight = true, eight = false, random = false;
    private Vector2f _mOffset = new Vector2f(0, 0);

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

    private void createVisuals()
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

        _mMask = ResourceLoader.loadTexture(_mContext, R.drawable.mask);
        _mRed = ResourceLoader.loadTexture(_mContext, R.drawable.red);
        _mBlue = ResourceLoader.loadTexture(_mContext, R.drawable.blue);
        _mGreen = ResourceLoader.loadTexture(_mContext, R.drawable.green);
        _mColorful = ResourceLoader.loadTexture(_mContext, R.drawable.colorful);
        _mPink = ResourceLoader.loadTexture(_mContext, R.drawable.pink);
        _mAutumn = ResourceLoader.loadTexture(_mContext, R.drawable.autumn);
        _mWinterWonderland = ResourceLoader.loadTexture(_mContext, R.drawable.winterwonderland);
        _mBump = ResourceLoader.loadTexture(_mContext, R.drawable.bump);

        if(red)
            _mTexture = _mRed;
        else if(green)
            _mTexture = _mGreen;
        else if(blue)
            _mTexture = _mBlue;
        else if(colorful)
            _mTexture = _mColorful;
        else if(pink)
            _mTexture = _mPink;
        else if(autumn)
            _mTexture = _mAutumn;
        else if(winterwonderland)
            _mTexture = _mWinterWonderland;
    }

    @Override
    public void onSurfaceChanged(GL10 notUsed, int width, int height)
    {
        GLES20.glViewport(0, 0, width, height);
        _mCamera.onSurfaceChanged(width, height);
    }

    @Override
    public void onDrawFrame(GL10 notUsed)
    {
        long time = SystemClock.uptimeMillis();
        float currentTime = time/1000.0f;
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);

        if(straight) moveStraight(currentTime);
        else if(eight) moveEight(currentTime);
        else if(random) moveRandom();

        DrawModel();
    }

    private void moveStraight(float t)
    {
        _mLightPosition.y = (float)Math.sin(t * _mAnimationSpeed) * 0.9f;
        _mLightPosition.x = 0.0f;
        _mLightTransform.setPosition(_mLightPosition.x, _mLightPosition.y, _mLightPosition.z);
    }

    private void moveEight(float t)
    {
        _mLightPosition.y = (float)Math.sin(t * _mAnimationSpeed) * 0.9f;
        _mLightPosition.x  = (float)Math.sin(t * _mAnimationSpeed * 2) * 0.4f;
        _mLightTransform.setPosition(_mLightPosition.x, _mLightPosition.y, _mLightPosition.z);
    }

    private float x  = (float)Math.random() - 0.6f;
    private float y = (float)Math.random() *1.8f - 0.9f;

    private void moveRandom()
    {
        if(_mLightPosition.y < y) _mLightPosition.y += 0.01f * _mAnimationSpeed;
        if(_mLightPosition.y > y) _mLightPosition.y -= 0.01f * _mAnimationSpeed;
        if(_mLightPosition.x < x) _mLightPosition.x += 0.01f * _mAnimationSpeed;
        if(_mLightPosition.x > x) _mLightPosition.x -= 0.01f * _mAnimationSpeed;
        if(Math.abs(_mLightPosition.y - y) < 0.01f * _mAnimationSpeed && Math.abs(_mLightPosition.x - x) < 0.01f* _mAnimationSpeed)
        {
            x  = (float)Math.random() - 0.5f;
            y = (float)Math.random() *2 - 1;
        }
        _mLightTransform.setPosition(_mLightPosition.x, _mLightPosition.y, _mLightPosition.z);
    }

    private void DrawModel()
    {
        GLES20.glUseProgram(_mShader.getMainProgram());

        float[] MVPMatrix = new float[16];

        android.opengl.Matrix.multiplyMM(MVPMatrix, 0, _mCamera.getProjectionMatrix(), 0, _mCamera.getViewMatrix(), 0);

        GLES20.glUniformMatrix4fv(GLES20.glGetUniformLocation(_mShader.getMainProgram(), "MVMatrix"), 1, false, MVPMatrix, 0);

        GLES20.glUniformMatrix4fv(GLES20.glGetUniformLocation(_mShader.getMainProgram(), "modelMatrix"), 1, false, _mPlaneTransform.getModelMatrix(), 0);

        GLES20.glUniform3fv(GLES20.glGetUniformLocation(_mShader.getMainProgram(), "lightPosition"), 1, _mLightPosition.get(), 0);

        GLES20.glUniform2fv(GLES20.glGetUniformLocation(_mShader.getMainProgram(), "offset"), 1, _mOffset.get(), 0);


        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, _mMask);
        GLES20.glUniform1i(GLES20.glGetUniformLocation(_mShader.getMainProgram(), "mask"), 0);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, _mTexture);
        GLES20.glUniform1i(GLES20.glGetUniformLocation(_mShader.getMainProgram(), "texture"), 1);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE2);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, _mBump);
        GLES20.glUniform1i(GLES20.glGetUniformLocation(_mShader.getMainProgram(), "bumptexture"), 2);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6);
    }

    public void parallaxMove(float x, float y, boolean reversed, boolean touch)
    {
        if (_mContext.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            if (!touch)
            {
                float temp;
                if (reversed) {
                    temp = y;
                    y = -x;
                } else {
                    temp = -y;
                    y = x;
                }
                x = temp;
            }
            else
            {
                float temp;
                if (reversed) {
                    temp = y;
                    y = -x;
                } else {
                    temp = y;
                    y = x;
                }
                x = temp;
            }
        }

        _mOffset.x += (x * (0.001f));
        _mOffset.y -= (y * (0.001f));
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
            case "RED":
                _mTexture = _mRed;
                red = true;
                blue = green = colorful = winterwonderland = pink = autumn = false;
                break;
            case "BLUE":
                _mTexture = _mBlue;
                blue = true;
                red = green = colorful = winterwonderland = pink = autumn = false;
                break;
            case "GREEN":
                _mTexture = _mGreen;
                green = true;
                red = blue= colorful = winterwonderland = pink = autumn = false;
                break;
            case "COLORFUL":
                _mTexture = _mColorful;
                colorful = true;
                red = green = blue = winterwonderland = pink = autumn = false;
                break;
            case "PINK":
                _mTexture = _mPink;
                pink = true;
                red = green = blue = colorful = winterwonderland = autumn = false;
                break;
            case "AUTUMN":
                _mTexture = _mAutumn;
                autumn = true;
                red = green = blue = colorful = winterwonderland = pink = false;
                break;
            case "WINTER WONDERLAND":
                _mTexture = _mWinterWonderland;
                winterwonderland = true;
                red = green = blue = colorful = pink = autumn = false;
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
            eight = false;
            random = false;
        }
        else if(motion.matches("8"))
        {
            straight = false;
            eight = true;
            random = false;
        }
        else if(motion.matches("random"))
        {
            straight = false;
            eight = false;
            random = true;
        }
    }
}