package bootiful.spel;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import static bootiful.spel.Spel.TESLA;

class RootObjectsTest {


    @Test
    void rootObjects() throws Exception {
        var parser = new SpelExpressionParser();
        var expression = parser.parseExpression("name");
        var name = (String) expression.getValue(TESLA);
        Assertions.assertEquals(name, "Nikola Tesla");
        var nameIsNikola = parser.parseExpression("name == 'Nikola Tesla'")
                .getValue(TESLA, Boolean.class);
        Assertions.assertTrue(nameIsNikola, "the name is Nikola Tesla");
    }


}
