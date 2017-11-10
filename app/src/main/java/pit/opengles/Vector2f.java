package pit.opengles;

/**
 * Created by paulh on 12.10.2017.
 */

public class Vector2f extends Vector {

    public float x;
    public float y;

    public Vector2f() { this(0); }

    public Vector2f(float s) { x = y = s; }

    public Vector2f(float _x, float _y)
    {
        x = _x;
        y = _y;
    }

    public Vector2f(float[] set)
    {
        if(set.length != 2)
            throw new IllegalArgumentException("Array must be of size 2");
        this.x = set[0];
        this.y = set[1];
    }

    @Override
    public float[] get()
    {
        float[] ret = {this.x, this.y};
        return ret;
    }

    @Override
    public Vector2f set(float[] set)
    {
        if (set.length != 2)
            throw new IllegalArgumentException("Array must be of size 2");
        this.x = set[0];
        this.y = set[1];

        return this;
    }
}
