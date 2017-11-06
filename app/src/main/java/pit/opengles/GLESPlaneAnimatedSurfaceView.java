package pit.opengles;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.SurfaceHolder;

/**
 * Created by paulh on 14.10.2017.
 */

public class GLESPlaneAnimatedSurfaceView extends GLSurfaceView implements SensorEventListener {

    private GLESPlaneAnimatedRenderer _mRenderer;
    private SensorManager mSensorManager;
    private Sensor accelerometer;
    private boolean rendererHasBeenSet = false;

    private float deltaX = 0;
    private float deltaY = 0;
    private float currentX = 0;
    private float currentY = 0;
    private float currentZ = 0;
    private float lastX = 0;
    private float lastY = 0;
    private float roll = 0;
    private float pitch = 0;


    public GLESPlaneAnimatedSurfaceView(Context context)
    {
        super(context);
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        mSensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_FASTEST);
    }

    public GLESPlaneAnimatedSurfaceView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        mSensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_FASTEST);
    }

    public void setRenderer(GLESPlaneAnimatedRenderer renderer)
    {
        super.setRenderer(renderer);
        _mRenderer = renderer;
        rendererHasBeenSet = true;
    }


    @Override
    public void onSensorChanged(SensorEvent event){
        final float alpha = 0.8f;

        currentX = alpha * currentX + (1 - alpha) * event.values[0];
        currentY = alpha * currentY + (1 - alpha) * event.values[1];
        currentZ = alpha * currentZ + (1 - alpha) * event.values[2];

        double gSum = Math.sqrt(currentX*currentX + currentY*currentY + currentZ*currentZ);
        if (gSum != 0)
        {
            currentX /= gSum;
            currentY /= gSum;
            currentZ /= gSum;
        }
        if (currentZ != 0)
            roll = (float)(Math.atan2(currentX, currentZ) * 180/Math.PI);
        pitch = (float)(Math.sqrt(currentX*currentX + currentZ*currentZ));
        if (pitch != 0)
            pitch = (float)(Math.atan2(currentY, pitch) * 180/Math.PI);
        deltaX = roll - lastX;
        deltaY = pitch - lastY;

        if (currentY > 0.99) deltaX = 0;

        if (deltaX > 180) deltaX = 0;
        if (deltaX < -180) deltaX = 0;
        if (deltaY > 180) deltaY = 0;
        if (deltaY < -180) deltaY = 0;
        lastX = roll;
        lastY = pitch;

        if ((deltaX != 0) || (deltaY != 0))
        {
            if(rendererHasBeenSet) _mRenderer.parallaxMove(deltaX, deltaY);
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}


    @Override
    public void onPause()
    {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        mSensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_FASTEST);

    }

    public GLESPlaneAnimatedRenderer getRenderer() { return _mRenderer; }
}
