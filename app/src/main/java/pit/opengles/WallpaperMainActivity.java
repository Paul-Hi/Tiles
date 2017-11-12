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
import android.content.pm.PackageManager;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;
import android.widget.ToggleButton;

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
    private boolean changed;
    private PackageManager p;
    private ComponentName cN;
    String[] colors = {
            "RED",
            "BLUE",
            "GREEN",
            "COLORFUL",
            "WINTER WONDERLAND",
    };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wallpaper_activity_main);
        changed = false;
        p = getPackageManager();
        cN = new ComponentName(this, WallpaperMainActivity.class);

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
            _mGLSurfaceView.setEGLContextClientVersion(2);
            _mGLSurfaceView.setRenderer(_mRenderer);
        }
        else
        {
            throw new RuntimeException("Error OpenGL ES 2.0 not found");
        }

        editor.putString("color", prefs.getString("color", "COLORFUL"));
        _mRenderer.switchColors(prefs.getString("color", "COLORFUL"));
        editor.putFloat("animSpeed", prefs.getFloat("animSpeed", 0.2f));
        _mRenderer.changeAnimationSpeed(prefs.getFloat("animSpeed", 0.2f));
        editor.putString("motion", prefs.getString("motion", "straight"));
        _mRenderer.changeMotion(prefs.getString("motion", "straight"));
        editor.putBoolean("sensors", prefs.getBoolean("sensors", false));
        _mGLSurfaceView.activateSensors(prefs.getBoolean("sensors", false));


        //COLOR DROPDOWN
        Spinner colorDropDown = (Spinner) findViewById(R.id.colorDropdown);
        ArrayAdapter adapter= new ArrayAdapter(this, R.layout.spinner_item, colors);
        final ImageView dropDownImage = (ImageView) findViewById(R.id.dropdownimage);

        colorDropDown.setAdapter(adapter);
        String currentColor = prefs.getString("color", "COLORFUL");
        switch (currentColor) {
            case "RED":
                colorDropDown.setSelection(0);
                dropDownImage.setImageResource(R.drawable.spinnerred);
                break;
            case "BLUE":
                colorDropDown.setSelection(1);
                dropDownImage.setImageResource(R.drawable.spinnerblue);
                break;
            case "GREEN":
                colorDropDown.setSelection(2);
                dropDownImage.setImageResource(R.drawable.spinnergreen);
                break;
            case "COLORFUL":
                colorDropDown.setSelection(3);
                dropDownImage.setImageResource(R.drawable.spinnerdefault);
                break;
            case "WINTER WONDERLAND":
                colorDropDown.setSelection(4);
                dropDownImage.setImageResource(R.drawable.spinnerdefault);
                break;
        }

        colorDropDown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(prefs.getString("color", "") != colors[(int) id]) changed = true;
                switch (colors[(int) id]) {
                    case "RED":
                        _mRenderer.switchColors("RED");
                        editor.putString("color", "RED");
                        dropDownImage.setImageResource(R.drawable.spinnerred);
                        break;
                    case "BLUE":
                        _mRenderer.switchColors("BLUE");
                        editor.putString("color", "BLUE");
                        dropDownImage.setImageResource(R.drawable.spinnerblue);
                        break;
                    case "GREEN":
                        _mRenderer.switchColors("GREEN");
                        editor.putString("color", "GREEN");
                        dropDownImage.setImageResource(R.drawable.spinnergreen);
                        break;
                    case "COLORFUL":
                        _mRenderer.switchColors("COLORFUL");
                        editor.putString("color", "COLORFUL");
                        dropDownImage.setImageResource(R.drawable.spinnerdefault);
                        break;
                    case "WINTER WONDERLAND":
                        _mRenderer.switchColors("WINTER WONDERLAND");
                        editor.putString("color", "WINTER WONDERLAND");
                        dropDownImage.setImageResource(R.drawable.spinnerdefault);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        //SET WALLPAPER
        Button setButton = (Button) findViewById(R.id.buttonSetWallpaper);
        setButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.apply();
                changed = false;
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

        //PARALLAX BUTTON
        ToggleButton parallaxToggle = (ToggleButton) findViewById(R.id.parallaxToggle) ;
        boolean pT = prefs.getBoolean("sensors", false);
        if(pT)
            parallaxToggle.toggle();
        parallaxToggle.setOnCheckedChangeListener( new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                changed = true;
                if(isChecked)
                {
                    _mGLSurfaceView.activateSensors(true);
                    editor.putBoolean("sensors", true);
                    Toast.makeText(getBaseContext(), "Parallax Effect enabled",
                        Toast.LENGTH_SHORT).show();
                }
                else
                {
                    _mGLSurfaceView.activateSensors(false);
                    editor.putBoolean("sensors", false);
                    Toast.makeText(getBaseContext(), "Parallax Effect disabled",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });



        RadioButton radioButton;

        // MOTION RADIO GROUP
        final RadioGroup motion = (RadioGroup) findViewById(R.id.motionGroup);
        String currentMotion = prefs.getString("motion", "straight");
        switch (currentMotion) {
            case "straight":
                radioButton = (RadioButton) findViewById(R.id.straight);
                radioButton.toggle();
                break;
            case "8":
                radioButton = (RadioButton) findViewById(R.id.eight);
                radioButton.toggle();
                break;
            case "random":
                radioButton = (RadioButton) findViewById(R.id.random);
                radioButton.toggle();
                break;
        }
        motion.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                changed = true;
                View radioButton = findViewById(checkedId);
                int index = group.indexOfChild(radioButton);
                switch (index) {
                    case 0:
                        _mRenderer.changeMotion("straight");
                        editor.putString("motion", "straight");
                        break;
                    case 1:
                        _mRenderer.changeMotion("8");
                        editor.putString("motion", "8");
                        break;
                    case 2:
                        _mRenderer.changeMotion("random");
                        editor.putString("motion", "random");
                        break;
                }
            }
        });

        //ANIMATION SPEED SLIDER
        SeekBar animSpeed = (SeekBar) findViewById(R.id.animationSpeedSlider);
        Float currentSpeed = prefs.getFloat("animSpeed", 0.2f);
        animSpeed.setProgress((int)(currentSpeed * 50));
        animSpeed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                changed = true;
                float animSpeed = progress/50.0f;
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
        if(!changed)
        {
            WallpaperMainActivity.super.onBackPressed();
            return;
        }
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