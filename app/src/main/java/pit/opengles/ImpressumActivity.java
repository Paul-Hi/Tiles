package pit.opengles;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * Created by paulh on 30.10.2017.
 */

public class ImpressumActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.impressum_activity);

        Button rate = (Button) findViewById(R.id.rate);
        rate.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                try
                {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market:://details?id=pit.opengles")));
                }
                catch(Exception e)
                {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=pit.opengles")));
                }
            }
        });
    }
}
