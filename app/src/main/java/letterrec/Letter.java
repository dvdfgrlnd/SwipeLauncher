package letterrec;

import java.io.Serializable;

/**
 * Created by david on 3/18/18.
 */

public class Letter implements Serializable {
    public String letter;
    public double[] vector;

    public Letter(String l, double[] p) {
        this.letter = l;
        this.vector = p;
    }
}
