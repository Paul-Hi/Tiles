package pit.opengles;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ConfigurationInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.SeekBar;

import pit.livewallpaper.ColloredWallpaperService;


/**
 * Created by paulh on 11.10.2017.
 */

public class WallpaperMainActivity extends Activity {

    private GLESPlaneAnimatedSurfaceView _mGLSurfaceView;
    private GLESPlaneAnimatedRenderer _mRenderer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.wallpaper_activity_main);

        _mGLSurfaceView = (GLESPlaneAnimatedSurfaceView) findViewById(R.id.surfaceView);
        _mRenderer = new GLESPlaneAnimatedRenderer(this);
        if(isValidGLES())
        {
            _mGLSurfaceView. setEGLContextClientVersion(3);
            _mGLSurfaceView.setRenderer(_mRenderer);
        }
        else
        {
            throw new RuntimeException("Error OpenGL ES 3.0 not found");
        }

        SharedPreferences prefs = getSharedPreferences("Info", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = prefs.edit();

        editor.putString("color", "default");
        editor.putFloat("animSpeed", 1.0f);
        editor.putString("motion", "straight");

        Button setButton = (Button) findViewById(R.id.buttonSetWallpaper);
        setButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.apply();
                try {
                    Intent intent = new Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER);
                    intent.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT, new ComponentName(WallpaperMainActivity.this, ColloredWallpaperService.class));
                    startActivity(intent);
                } catch (Exception e) {
                    Intent intent = new Intent();
                    intent.setAction(WallpaperManager.ACTION_LIVE_WALLPAPER_CHOOSER);
                    startActivity(intent);
                }
            }
        });

        final RadioGroup col = (RadioGroup) findViewById(R.id.colorGroup);
        col.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                View radioButton = findViewById(checkedId);
                int index = col.indexOfChild(radioButton);
                switch (index) {
                    case 0:
                        _mRenderer.switchColors(new Vector3f(0.5f, -0.5f, -0.5f));
                        editor.putString("color", "red");
                        break;
                    case 1:
                        _mRenderer.switchColors(new Vector3f(-0.5f, -0.5f, 0.5f));
                        editor.putString("color", "blue");
                        break;
                    case 2:
                        _mRenderer.switchColors(new Vector3f(-0.5f, 0.5f, -0.5f));
                        editor.putString("color", "green");
                        break;
                    case 3:
                        _mRenderer.switchColors(new Vector3f(0, 0, 0));
                        editor.putString("color", "default");
                        break;
                }
            }
        });

        final RadioGroup motion = (RadioGroup) findViewById(R.id.motionGroup);
        motion.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                View radioButton = findViewById(checkedId);
                int index = motion.indexOfChild(radioButton);
                switch (index) {
                    case 0:
                        _mRenderer.changeMotion("straight");
                        editor.putString("motion", "straight");
                        editor.apply();
                        break;
                    case 1:
                        _mRenderer.changeMotion("wave");
                        editor.putString("motion", "wave");
                        editor.apply();
                        break;
                }
            }
        });

        SeekBar animSpeed = (SeekBar) findViewById(R.id.animationSpeedSlider);
        animSpeed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float animSpeed = progress/10.0f;
                _mRenderer.changeAnimationSpeed(animSpeed);
                editor.putFloat("animSpeed", animSpeed);
                editor.apply();;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        try{
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu, menu);
        }
        catch(Exception e){
            Intent intent = new Intent();
            intent.setClassName(this, "pit.opengles.WallpaperMainActivity");
            startActivity(intent);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {
            case R.id.preferences:
            {
                Intent intent = new Intent();
                intent.setClassName(this, "pit.opengles.WallpaperMainActivity");
                startActivity(intent);
                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean isValidGLES()
    {
        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        ConfigurationInfo info = am.getDeviceConfigurationInfo();
        return info.reqGlEsVersion >= 0x3000;
    }
}
