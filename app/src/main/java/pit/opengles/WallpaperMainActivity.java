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
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.text.Html;
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
import android.widget.Toast;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import pit.livewallpaper.ColloredWallpaperService;


/**
 * Created by paulh on 11.10.2017.
 */

public class WallpaperMainActivity extends Activity {

    private int version;
    private GLESPlaneAnimatedSurfaceView _mGLSurfaceView;
    private GLESPlaneAnimatedRenderer _mRenderer;
    public SharedPreferences prefs;
    public SharedPreferences.Editor editor;
    private boolean changedColor;
    private boolean changedMotion;
    private boolean changedSpeed;
    private boolean changedParallax;
    private boolean wasParallax;
    private String movement;
    private String color;
    private String[] colors_intern;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        String[] colors = getResources().getStringArray(R.array.colors);
        colors_intern = getResources().getStringArray(R.array.colors_intern);
        setContentView(R.layout.wallpaper_activity_main);
        changedColor = false;
        changedMotion = false;
        changedSpeed = false;
        changedParallax = false;
        wasParallax = false;
        movement = "";
        color = "";


        prefs = getSharedPreferences("Info", Context.MODE_PRIVATE);
        editor = prefs.edit();

        MobileAds.initialize(this, getString(R.string.ad_mob_id));

        AdView _mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice("00718D2A5312B00A9EF221D92DD35747") /* Pits Handy OnePlus3 - CHECK */
                .addTestDevice("397A8E3873AFA1DDC3F3897C51B44C8B") /* Pits Tablet Huawei - CHECK */
                .build();
        _mAdView.loadAd(adRequest);

        try {
            version = prefs.getInt("update", getPackageManager().getPackageInfo(getPackageName(), 0).versionCode);
            editor.putInt("update", version);
            editor.apply();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        try {
            if(version < getPackageManager().getPackageInfo(getPackageName(), 0).versionCode)
            {
                editor.putInt("update", getPackageManager().getPackageInfo(getPackageName(), 0).versionCode);
                editor.apply();
                AlertDialog.Builder builder;
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                {
                    builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
                }
                else
                {
                    builder = new AlertDialog.Builder(this);
                }
                builder.setMessage(Html.fromHtml(getResources().getString(R.string.update)))
                        .setIcon(R.mipmap.tlw_icon)
                        .setNegativeButton(R.string.menuRate, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                try
                                {
                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market:://details?id=pit.opengles")));
                                }
                                catch(Exception e)
                                {
                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=pit.opengles")));
                                }
                            }
                        })
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .show();
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

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

        //SAVED STATE
        editor.putString("color", prefs.getString("color", "COLORFUL"));
        _mRenderer.switchColors(prefs.getString("color", "COLORFUL"));
        color =  prefs.getString("color", "");
        editor.putFloat("animSpeed", prefs.getFloat("animSpeed", 0.2f));
        _mRenderer.changeAnimationSpeed(prefs.getFloat("animSpeed", 0.2f));
        editor.putString("motion", prefs.getString("motion", "straight"));
        _mRenderer.changeMotion(prefs.getString("motion", "straight"));
        movement = prefs.getString("motion", "");
        editor.putBoolean("sensors", prefs.getBoolean("sensors", false));
        _mGLSurfaceView.activateSensors(prefs.getBoolean("sensors", false));
        wasParallax = prefs.getBoolean("sensors", false);


        //COLOR DROPDOWN
        Spinner colorDropDown = (Spinner) findViewById(R.id.colorDropdown);
        ArrayAdapter adapter= new ArrayAdapter<>(this, R.layout.spinner_item, colors);
        final ImageView dropDownImage = (ImageView) findViewById(R.id.dropdownimage);

        colorDropDown.setAdapter(adapter);
        String currentColor = prefs.getString("color", "COLORFUL");
        switch (currentColor) {
            case "RED":
                colorDropDown.setSelection(0);
                dropDownImage.setBackgroundColor(Color.rgb(255, 0, 0));
                break;
            case "BLUE":
                colorDropDown.setSelection(1);
                dropDownImage.setBackgroundColor(Color.rgb(0, 0, 255));
                break;
            case "GREEN":
                colorDropDown.setSelection(2);
                dropDownImage.setBackgroundColor(Color.rgb(0, 255, 0));
                break;
            case "COLORFUL":
                colorDropDown.setSelection(3);
                dropDownImage.setBackgroundColor(Color.rgb(255, 150, 0));
                break;
            case "PINK":
                colorDropDown.setSelection(4);
                dropDownImage.setBackgroundColor(Color.rgb(225, 0, 135));
                break;
            case "AUTUMN":
                colorDropDown.setSelection(5);
                dropDownImage.setBackgroundColor(Color.rgb(175, 125, 0));
                break;
            case "WINTER WONDERLAND":
                colorDropDown.setSelection(6);
                dropDownImage.setBackgroundColor(Color.rgb(200, 200, 255));
                break;
        }

        colorDropDown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(!color.equals(colors_intern[(int) id])) changedColor = true;
                else changedColor = false;
                switch ((int)id) {
                    case 0:
                        _mRenderer.switchColors("RED");
                        editor.putString("color", "RED");
                        dropDownImage.setBackgroundColor(Color.rgb(255, 0, 0));
                        break;
                    case 1:
                        _mRenderer.switchColors("BLUE");
                        editor.putString("color", "BLUE");
                        dropDownImage.setBackgroundColor(Color.rgb(0, 0, 255));
                        break;
                    case 2:
                        _mRenderer.switchColors("GREEN");
                        editor.putString("color", "GREEN");
                        dropDownImage.setBackgroundColor(Color.rgb(0, 255, 0));
                        break;
                    case 3:
                        _mRenderer.switchColors("COLORFUL");
                        editor.putString("color", "COLORFUL");
                        dropDownImage.setBackgroundColor(Color.rgb(255, 150, 0));
                        break;
                    case 4:
                        _mRenderer.switchColors("PINK");
                        editor.putString("color", "PINK");
                        dropDownImage.setBackgroundColor(Color.rgb(225, 0, 135));
                        break;
                    case 5:
                        _mRenderer.switchColors("AUTUMN");
                        editor.putString("color", "AUTUMN");
                        dropDownImage.setBackgroundColor(Color.rgb(175, 125, 0));
                        break;
                    case 6:
                        _mRenderer.switchColors("WINTER WONDERLAND");
                        editor.putString("color", "WINTER WONDERLAND");
                        dropDownImage.setBackgroundColor(Color.rgb(200, 200, 255));
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
                changedColor = false;
                changedMotion = false;
                changedSpeed = false;
                changedParallax = false;
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

        //PARALLAX TOGGLE
        SwitchCompat parallaxToggle = (SwitchCompat) findViewById(R.id.parallaxToggle) ;
        boolean pT = prefs.getBoolean("sensors", false);
        if(pT)
            parallaxToggle.toggle();
        parallaxToggle.setOnCheckedChangeListener( new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked)
                {
                    if(!wasParallax) changedParallax = true;
                    else changedParallax = false;
                    _mGLSurfaceView.activateSensors(true);
                    editor.putBoolean("sensors", true);
                }
                else
                {
                    if(wasParallax) changedParallax = true;
                    else changedParallax = false;
                    _mGLSurfaceView.activateSensors(false);
                    editor.putBoolean("sensors", false);
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
                View radioButton = findViewById(checkedId);
                int index = group.indexOfChild(radioButton);
                switch (index) {
                    case 0:
                        if(!movement.equals("straight")) changedMotion = true;
                        else changedMotion = false;
                        _mRenderer.changeMotion("straight");
                        editor.putString("motion", "straight");
                        break;
                    case 1:
                        if(!movement.equals("8")) changedMotion = true;
                        else changedMotion = false;
                        _mRenderer.changeMotion("8");
                        editor.putString("motion", "8");
                        break;
                    case 2:
                        if(!movement.equals("random")) changedMotion = true;
                        else changedMotion = false;
                        _mRenderer.changeMotion("random");
                        editor.putString("motion", "random");
                        break;
                }
            }
        });

        //ANIMATION SPEED SLIDER
        SeekBar animSpeed = (SeekBar) findViewById(R.id.animationSpeedSlider);
        Float currentSpeed = prefs.getFloat("animSpeed", 0.5f);
        animSpeed.setProgress((int)(currentSpeed * 100));
        animSpeed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                changedSpeed = true;
                float animSpeed = progress/100.0f;
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
                Intent intent = new Intent();
                intent.setClassName(this, "pit.opengles.ImpressumActivity");
                startActivity(intent);
                return true;
            case R.id.rate:
                try
                {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market:://details?id=pit.opengles")));
                }
                catch(Exception e)
                {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=pit.opengles")));
                }
                return true;
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
        if(!(changedColor || changedMotion || changedParallax || changedSpeed))
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