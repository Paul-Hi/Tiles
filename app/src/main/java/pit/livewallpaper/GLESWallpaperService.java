package pit.livewallpaper;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.service.wallpaper.WallpaperService;
import android.view.SurfaceHolder;

/**
 * Created by paulh on 13.10.2017.
 */

public abstract class GLESWallpaperService extends WallpaperService
{
    public class GLESEngine extends Engine
    {
        class WallpaperGLESSurfaceView extends GLSurfaceView
        {
            public WallpaperGLESSurfaceView(Context context) {
                super(context);
            }

            @Override
            public SurfaceHolder getHolder()
            {
                return getSurfaceHolder();
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

