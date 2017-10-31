package pit.opengles;

import android.opengl.Matrix;

/**
 * Created by paulh on 11.10.2017.
 */

public class Transform {

    private Vector3f _mTranslation = new Vector3f();
    private Vector3f _mRotation = new Vector3f();
    private Vector3f _mScale = new Vector3f();
    private float mAngle;

    private float[] mModelMatrix = new float[16];


    public Transform( float translationX, float translationY, float translationZ,
                      float rotationX, float rotationY, float rotationZ,
                      float scaleX, float scaleY, float scaleZ,
                      float angle)
    {
        _mTranslation.x = translationX;
        _mTranslation.y = translationY;
        _mTranslation.z = translationZ;

        _mRotation.x = rotationX;
        _mRotation.y = rotationY;
        _mRotation.z = rotationZ;

        _mScale.x = scaleX;
        _mScale.y = scaleY;
        _mScale.z = scaleZ;

        mAngle = angle;
        update();
    }

    private void update()
    {
        Matrix.setIdentityM(mModelMatrix, 0);
        Matrix.translateM(mModelMatrix, 0, _mTranslation.x, _mTranslation.y, _mTranslation.z);
        Matrix.rotateM(mModelMatrix, 0, mAngle, _mRotation.x, _mRotation.y, _mRotation.z);
        Matrix.scaleM(mModelMatrix, 0, _mScale.x, _mScale.y, _mScale.z);
    }

    public void setPosition(float translationX, float translationY, float translationZ)
    {
        _mTranslation.x = translationX;
        _mTranslation.y = translationY;
        _mTranslation.z = translationZ;
    }

    public void setRotation(float rotationX, float rotationY, float rotationZ, float angle)
    {
        _mRotation.x = rotationX;
        _mRotation.y = rotationY;
        _mRotation.z = rotationZ;
        mAngle = angle;
    }

    public void setScale(float scaleX, float scaleY, float scaleZ)
    {
        _mScale.x = scaleX;
        _mScale.y = scaleY;
        _mScale.z = scaleZ;
    }

    public float[] getModelMatrix()
    {
        update();
        return mModelMatrix;
    }
}
