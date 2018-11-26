package pit.opengles;

/**
 * Created by paulh on 12.10.2017.
 */

public abstract class Vector {

    public abstract float[] get();
    public abstract Vector set(float[] set);

    public float lengthSquared()
    {
        float[] a = get();
        float result = 0;
        for (float f : a) {
            result += (f * f);
        }
        return result;
    }

    public float length()
    {
        return (float)Math.sqrt(lengthSquared());
    }

    public Vector negate()
    {
        return scale(-1);
    }

    public Vector add(Vector other)
    {
        float[] a = get();
        float[] b = other.get();
        if(a.length != b.length)
            throw new IllegalArgumentException("Vectors must be of same dimension");
        for(int i = 0; i < a.length; i++)
            a[i] += b[i];

        return set(a);
    }

    public Vector sub(Vector other)
    {
        return add(other.negate());
    }

    public Vector mult(Vector other)
    {
        float[] a = get();
        float[] b = other.get();
        if(a.length != b.length)
            throw new IllegalArgumentException("Vectors must be of same dimension");
        for(int i = 0; i < a.length; i++)
            a[i] *= b[i];

        return set(a);
    }

    public Vector div(Vector other)
    {
        float[] a = get();
        float[] b = other.get();
        if(a.length != b.length)
            throw new IllegalArgumentException("Vectors must be of same dimension");
        for(int i = 0; i < a.length; i++)
            a[i] /= b[i];

        return set(a);
    }


    public Vector scale(float scalar)
    {
        return mult(scalar);
    }

    public Vector normalize()
    {
        return  div(length());
    }

    public Vector cross(Vector other)
    {
        float[] a = get();
        float[] b = other.get();
        if(a.length != b.length)
            throw new IllegalArgumentException("Vectors must be of same dimension");
        float[] result = new float[a.length];
        result[0] = a[2] * b[1] -  a[1] * b[2];
        result[1] = a[0] * b[2] - a[2] * b[0];
        result[2] = a[1] * b[0] - a[0] * b[1];

        return set(result);
    }



    //Helper
    private Vector mult(float other)
    {
        float[] a = get();
        for(int i = 0; i < a.length; i++)
            a[i] *= other;

        return set(a);
    }

    private Vector div(float other)
    {
        float[] a = get();
        for(int i = 0; i < a.length; i++)
            a[i] /= other;

        return set(a);
    }

}
