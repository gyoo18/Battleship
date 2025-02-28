package animations;

import java.util.ArrayList;

public interface Animable {
    public void mix(Object[] a, Object[] b, float m);
    public Object[] animClé();
    public boolean validerClé(Object[] c);;
    public void terminerAnimation(Object[] cléB);
}
