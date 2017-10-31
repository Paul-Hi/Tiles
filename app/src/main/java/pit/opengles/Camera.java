package pit.opengles;

import android.opengl.Matrix;

/**
 * Created by paulh on 11.10.2017.
 */

public class Camera {

    private Vector3f position = new Vector3f(0.0f, 0.0f, -10.0f);

    private Vector3f forward = new Vector3f(0.0f, 0.0f, 1.0f);

    private Vector3f up = new Vector3f(0.0f, 1.0f, 0.0f);

    private float[] _mViewMatrix = new float[16];
    private float[] _mProjectionMatrix = new float[16];

    public Camera()
    {
        Matrix.setLookAtM(_mViewMatrix, 0, position.x, position.y, position.z, forward.x, forward.y, forward.z, up.x, up.y, up.z);
    }

    public void onSurfaceChanged(int width, int height)
    {
        final float ratio = (float) width / height;
        final float near = 0.1f;
        final float far = 100.0f;
        //final float foV = 70.0f;

        //Matrix.perspectiveM(_mProjectionMatrix, 0, foV, ratio, near, far);
        Matrix.orthoM(_mProjectionMatrix, 0, -ratio, ratio, -1, 1, near, far);
    }

    public float[] getViewMatrix() { return _mViewMatrix;}
    public float[] getProjectionMatrix() { return _mProjectionMatrix;}

}
