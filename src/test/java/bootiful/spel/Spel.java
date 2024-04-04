package bootiful.spel;

import java.util.Date;
import java.util.GregorianCalendar;

class Spel {

    public record Inventor(String name, Date birthday, String nationality, String[] inventionsArray) {
    }

    public static Inventor TESLA = new Inventor("Nikola Tesla",
            new GregorianCalendar(1856, 7, 9).getTime(), "Serbian",
            new String[]{"induction motor", "commutator for dynamo"});

}


