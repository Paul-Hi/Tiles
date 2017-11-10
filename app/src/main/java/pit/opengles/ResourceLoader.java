package pit.opengles;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;

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

    public static int loadTexture(final Context context, final int RID)
    {
        final int[] textureHandle = new int[1];

        GLES20.glGenTextures(1, textureHandle, 0);

        if (textureHandle[0] != 0)
        {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inScaled = false;

            final Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), RID, options);

            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0]);

            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);

            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);

            bitmap.recycle();
        }

        if (textureHandle[0] == 0)
        {
            throw new RuntimeException("Texture Loading failed");
        }

        return textureHandle[0];
    }
}
