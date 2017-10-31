package pit.opengles;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ConfigurationInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;


import pit.livewallpaper.ColloredWallpaperService;


/**
 * Created by paulh on 11.10.2017.
 */

public class WallpaperMainActivity extends Activity {

    private GLESPlaneAnimatedSurfaceView _mGLSurfaceView;
    private GLESPlaneAnimatedRenderer _mRenderer;
    private AdView _mAdView;
    public SharedPreferences prefs;
    public SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wallpaper_activity_main);

        MobileAds.initialize(this, getString(R.string.ad_mob_id));

        _mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice("F8757B0F4DDA3CEB3AFA68C63062296A") /* Pits Handy OnePlus3 - CHECK */
                .addTestDevice("397A8E3873AFA1DDC3F3897C51B44C8B") /* Pits Tablet Huawei* - CHECK */
                .build();
        _mAdView.loadAd(adRequest);

        prefs = getSharedPreferences("Info", Context.MODE_PRIVATE);
        editor = prefs.edit();

        _mGLSurfaceView = (GLESPlaneAnimatedSurfaceView) findViewById(R.id.surfaceView);
        _mRenderer = new GLESPlaneAnimatedRenderer(this);
        if(isValidGLES())
        {
            _mGLSurfaceView. setEGLContextClientVersion(2);
            _mGLSurfaceView.setRenderer(_mRenderer);
        }
        else
        {
            throw new RuntimeException("Error OpenGL ES 2.0 not found");
        }

        editor.putString("color", prefs.getString("color", "colorful"));
        _mRenderer.switchColors(prefs.getString("color", "colorful"));
        editor.putFloat("animSpeed", prefs.getFloat("animSpeed", 1.0f));
        _mRenderer.changeAnimationSpeed(prefs.getFloat("animSpeed", 1.0f));
        editor.putString("motion", prefs.getString("motion", "straight"));
        _mRenderer.changeMotion(prefs.getString("motion", "straight"));

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


        RadioButton radioButton;

        final RadioGroup col = (RadioGroup) findViewById(R.id.colorGroup);
        String currentColor = prefs.getString("color", "colorful");
        switch (currentColor) {
            case "red":
                radioButton = (RadioButton) findViewById(R.id.red);
                radioButton.toggle();
                break;
            case "blue":
                radioButton = (RadioButton) findViewById(R.id.blue);
                radioButton.toggle();
                break;
            case "green":
                radioButton = (RadioButton) findViewById(R.id.green);
                radioButton.toggle();
                break;
            case "colorful":
                radioButton = (RadioButton) findViewById(R.id.colorful);
                radioButton.toggle();
                break;
        }
        col.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                View radioButton = findViewById(checkedId);
                int index = group.indexOfChild(radioButton);
                switch (index) {
                    case 0:
                        _mRenderer.switchColors("red");
                        editor.putString("color", "red");
                        break;
                    case 1:
                        _mRenderer.switchColors("blue");
                        editor.putString("color", "blue");
                        break;
                    case 2:
                        _mRenderer.switchColors("green");
                        editor.putString("color", "green");
                        break;
                    case 3:
                        _mRenderer.switchColors("colorful");
                        editor.putString("color", "colorful");
                        break;
                }
            }
        });

        final RadioGroup motion = (RadioGroup) findViewById(R.id.motionGroup);
        String currentMotion = prefs.getString("motion", "straight");
        switch (currentMotion) {
            case "straight":
                radioButton = (RadioButton) findViewById(R.id.straight);
                radioButton.toggle();
                break;
            case "wave":
                radioButton = (RadioButton) findViewById(R.id.wave);
                radioButton.toggle();
                break;
        }
        motion.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                View radioButton = findViewById(checkedId);
                int index = group.indexOfChild(radioButton);
                switch (index) {
                    case 0:
                        _mRenderer.changeMotion("straight");
                        editor.putString("motion", "straight");
                        break;
                    case 1:
                        _mRenderer.changeMotion("wave");
                        editor.putString("motion", "wave");
                        break;
                }
            }
        });

        SeekBar animSpeed = (SeekBar) findViewById(R.id.animationSpeedSlider);
        Float currentSpeed = prefs.getFloat("animSpeed", 1.0f);
        animSpeed.setProgress((int)(currentSpeed * 10));
        animSpeed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float animSpeed = progress/10.0f;
                _mRenderer.changeAnimationSpeed(animSpeed);
                editor.putFloat("animSpeed", animSpeed);
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
            intent.setClassName(this, "pit.opengles.ImpressumActivity");
            startActivity(intent);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {
            case R.id.impressum:
            {
                Intent intent = new Intent();
                intent.setClassName(this, "pit.opengles.ImpressumActivity");
                startActivity(intent);
                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean isValidGLES()
    {
        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        assert am != null;
        ConfigurationInfo info = am.getDeviceConfigurationInfo();
        return info.reqGlEsVersion >= 0x2000;
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Set Wallpaper?")
                .setMessage("Do you want to set the current Configuration?")
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1)
                    {
                        WallpaperMainActivity.super.onBackPressed();
                    }
                })
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1)
                    {
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
                        WallpaperMainActivity.super.onBackPressed();
                    }
                }).create().show();
    }
}
