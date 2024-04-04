package bootiful.spel;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import java.util.List;

class SelectionsTest {


    @Test
    void selection() throws Exception {
        record Cat(String type) {
        }
        var expressionParser = new SpelExpressionParser();
        var values = List.of(new Cat("Leopard"), new Cat("Tiger"), new Cat("Lion"),
                new Cat("Tiger"));
        var fewerValues = expressionParser.parseExpression("#root.?[ type == 'Tiger' ] ")
                .getValue(values, List.class);
        Assertions.assertEquals(fewerValues.size(), 2);
        System.out.println(fewerValues);
    }


}
