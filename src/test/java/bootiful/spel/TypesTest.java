package bootiful.spel;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.expression.spel.standard.SpelExpressionParser;

class TypesTest {

    @Test
    void types() throws Exception {
        var ep = new SpelExpressionParser();
        var result = ep.parseExpression("T(java.math.RoundingMode).CEILING < T (java.math.RoundingMode).FLOOR")
                .getValue(Boolean.class);
        Assertions.assertTrue(result);
    }
}
