import de.upb.cs.swt.sectypes.qual.High;
import de.upb.cs.swt.sectypes.qual.Low;

/**
 * Created by benhermann on 19.12.17.
 */
public class SampleProgram {
    public void main(@Low String[] args) {
        @High int secret = 42;
        @Low int pub = 12;

        if (secret == 42) {
            pub = 1;
        } else {
            pub = 2;
            pub = secret;
            secret = pub;
        }

        pub = secret;

    }
}
