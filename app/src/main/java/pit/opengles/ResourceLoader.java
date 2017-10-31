package pit.opengles;

import android.content.Context;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by paulh on 12.10.2017.
 */

public class ResourceLoader {

    public static String readShader(final Context context, final int resourceId)
    {
        final InputStream input = context.getResources().openRawResource(
                resourceId);
        final InputStreamReader reader = new InputStreamReader(
                input);
        final BufferedReader bufferedReader = new BufferedReader(
                reader);

        String next;
        final StringBuilder builder = new StringBuilder();

        try
        {
            while ((next = bufferedReader.readLine()) != null)
            {
                builder.append(next);
                builder.append('\n');
            }
        }
        catch (IOException e)
        {
            return null;
        }

        return builder.toString();
    }
}
