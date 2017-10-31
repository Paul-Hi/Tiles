package pit.opengles;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

/**
 * Created by paulh on 14.10.2017.
 */

public class GLESPlaneAnimatedSurfaceView extends GLSurfaceView {

    private GLESPlaneAnimatedRenderer _mRenderer;

    public GLESPlaneAnimatedSurfaceView(Context context)
    {
        super(context);
    }

    public GLESPlaneAnimatedSurfaceView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public void setRenderer(GLESPlaneAnimatedRenderer renderer)
    {
        _mRenderer = renderer;
        super.setRenderer(renderer);
    }

    public GLESPlaneAnimatedRenderer getRenderer()
    {
        return _mRenderer;
    }
}
