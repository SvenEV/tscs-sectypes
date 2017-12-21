import de.upb.cs.swt.sectypes.qual.High;
import de.upb.cs.swt.sectypes.qual.Low;

/**
 * Created by benhermann on 19.12.17.
 */
public class SampleProgram {
    public void main(String[] args) {
        @High int secret = Integer.parseInt("42");
        @Low int pub = 12;

        if (secret == 32) {
            pub = 1;
        } else {
            pub = 2;
            pub = secret;
            secret = pub;
        }

        for (secret = Integer.parseInt("1"); secret == 42; pub++) {
            pub = pub * 2;
        }

        pub = secret;

    }
}
