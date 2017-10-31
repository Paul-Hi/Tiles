package pit.opengles;

/**
 * Created by paulh on 12.10.2017.
 */

public class Vector3f extends Vector{

    public float x;
    public float y;
    public float z;

    public Vector3f() { this(0); }

    public Vector3f(float s) { x = y = z = s; }

    public Vector3f(float _x, float _y, float _z)
    {
        x = _x;
        y = _y;
        z = _z;
    }

    public Vector3f(float[] set)
    {
        if(set.length != 3)
            throw new IllegalArgumentException("Array must be of size 3");
        this.x = set[0];
        this.y = set[1];
        this.z = set[2];
    }

    @Override
    public float[] get()
    {
        float[] ret = {this.x, this.y, this.z};
        return ret;
    }

    @Override
    public Vector3f set(float[] set)
    {
        if (set.length != 3)
            throw new IllegalArgumentException("Array must be of size 3");
        this.x = set[0];
        this.y = set[1];
        this.z = set[2];

        return this;
    }
}

