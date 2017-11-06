package pit.livewallpaper;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.GLSurfaceView;
import android.service.wallpaper.WallpaperService;
import android.view.SurfaceHolder;

import pit.opengles.GLESPlaneAnimatedRenderer;

/**
 * Created by paulh on 13.10.2017.
 */

public abstract class GLESWallpaperService extends WallpaperService
{
    public class GLESEngine extends Engine
    {
        class WallpaperGLESSurfaceView extends GLSurfaceView implements SensorEventListener
        {

            private SensorManager mSensorManager;
            private Sensor accelerometer;
            private GLESPlaneAnimatedRenderer _mRenderer;

            private float deltaX = 0;
            private float deltaY = 0;
            private float currentX = 0;
            private float currentY = 0;
            private float currentZ = 0;
            private float lastX = 0;
            private float lastY = 0;
            private float roll = 0;
            private float pitch = 0;


            public WallpaperGLESSurfaceView(Context context)
            {
                super(context);

                mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
                accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
                mSensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_FASTEST);
            }

            @Override
            public void setRenderer(Renderer renderer)
            {
                super.setRenderer(renderer);
                _mRenderer = (GLESPlaneAnimatedRenderer) renderer;
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

                if (currentY > 0.99f) deltaX = 0;
                if(currentX > 0.99f) deltaY = 0;

                if (deltaX > 180) deltaX = 0;
                if (deltaX < -180) deltaX = 0;
                if (deltaY > 180) deltaY = 0;
                if (deltaY < -180) deltaY = 0;
                lastX = roll;
                lastY = pitch;


                if ((Math.abs(deltaX) > 0.001f) || (Math.abs(deltaY) > 0.001f))
                {
                    if(_rendererHasBeenSet) _mRenderer.parallaxMove(deltaX, deltaY);
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {}


            @Override
            public SurfaceHolder getHolder()
            {
                return getSurfaceHolder();
            }

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

            public void onDestroy()
            {
                super.onDetachedFromWindow();
            }
        }

        private WallpaperGLESSurfaceView _mSurfaceView;
        private  boolean _rendererHasBeenSet;

        @Override
        public void onCreate(SurfaceHolder sH)
        {
            super.onCreate(sH);
            _mSurfaceView = new WallpaperGLESSurfaceView(GLESWallpaperService.this);
        }

        @Override
        public void onVisibilityChanged(boolean visible)
        {
            super.onVisibilityChanged(visible);

            if(_rendererHasBeenSet)
            {
                if(visible)
                    _mSurfaceView.onResume();
                else
                    _mSurfaceView.onPause();
            }
        }

        @Override
        public void onDestroy()
        {
            super.onDestroy();
            _mSurfaceView.onDestroy();
        }

        protected  void setRenderer(GLSurfaceView.Renderer renderer)
        {
            _mSurfaceView.setRenderer(renderer);
            _rendererHasBeenSet = true;
        }

        protected void setEGLContextClientVersion(int version)
        {
            _mSurfaceView.setEGLContextClientVersion(version);
        }

        protected void setPreserveEGLContextOnPause(boolean preserve)
        {
            _mSurfaceView.setPreserveEGLContextOnPause(preserve);
        }
    }
}

