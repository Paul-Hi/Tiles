package pit.livewallpaper;

import android.content.Context;
import android.content.SharedPreferences;
import android.opengl.GLSurfaceView;

import pit.opengles.GLESPlaneAnimatedRenderer;
import pit.opengles.Vector3f;

/**
 * Created by paulh on 16.10.2017.
 */

public class ColloredWallpaperService extends OpenGLESWallpaperService
{

    private GLESPlaneAnimatedRenderer _mRenderer;


    @Override
    GLSurfaceView.Renderer getGLESRenderer()
    {
        _mRenderer = new GLESPlaneAnimatedRenderer(this);
        SharedPreferences prefs = getSharedPreferences("Info", Context.MODE_PRIVATE);
        String color = prefs.getString("color", "");
        if(color.matches("red"))
            _mRenderer.switchColors(new Vector3f(0.5f, -0.5f, -0.5f));
        else if(color.matches("blue"))
            _mRenderer.switchColors(new Vector3f(-0.5f, -0.5f, 0.5f));
        else if(color.matches("green"))
            _mRenderer.switchColors(new Vector3f(-0.5f, 0.5f, -0.5f));
        else
            _mRenderer.switchColors(new Vector3f(0, 0, 0));
        Float animSpeed = prefs.getFloat("animSpeed", 0.1f);
        _mRenderer.changeAnimationSpeed(animSpeed);
        String motion = prefs.getString("motion", "straight");
        _mRenderer.changeMotion(motion);
        return  _mRenderer;
    }
}
