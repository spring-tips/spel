package bootiful.spel;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.expression.spel.standard.SpelExpressionParser;

class NavigationsTest {

    @Test
    void navigations() throws Exception {
        var expressionParser = new SpelExpressionParser();
        var inventor = new Spel.Inventor(null, null, null, null);
        var name = expressionParser
                .parseExpression("name ?: 'Bob' ")
                .getValue(inventor, String.class);
        Assertions.assertEquals(name, "Bob");
        var valueOrNull = expressionParser
                .parseExpression("inventionsArray?.length")
                .getValue(inventor, Integer.class);
        Assertions.assertNull(valueOrNull);
    }

}
