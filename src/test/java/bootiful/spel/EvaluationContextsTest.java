package bootiful.spel;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.expression.spel.SpelEvaluationException;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.SimpleEvaluationContext;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import static bootiful.spel.Spel.TESLA;

class EvaluationContextsTest {

    public static int add(int a, int b) {
        return a + b;
    }


    @Test
    void standardEvaluationContext() throws Exception {

        var method = getClass().getMethod("add", int.class, int.class);
        Assertions.assertNotNull(method);

        var context = new StandardEvaluationContext();
        context.setVariable("x", 10);
        context.setVariable("y", 5);
        context.registerFunction("add", method);

        var parser = new SpelExpressionParser();
        Assertions.assertEquals(parser.parseExpression("#x + #y")
                .getValue(context, Integer.class), 15);
        Assertions.assertEquals(parser.parseExpression("#add(#x,#y)")
                .getValue(context, Integer.class), 15);
    }

    @Test
    void simpleEvaluationContextWithoutInstanceMethods() {

        var context = SimpleEvaluationContext
                .forReadOnlyDataBinding()
                .withRootObject(TESLA);

        var parser = new SpelExpressionParser();

        // works
        Assertions.assertEquals(parser.parseExpression("#root.name")
                .getValue(context.build(), String.class), "Nikola Tesla");
        try {
            // fails
            Assertions.assertNotNull(parser.parseExpression("#root.toString() ")
                    .getValue(context.build(), String.class));
            Assertions.fail(); // this shouldn't be possible.
        } //
        catch (SpelEvaluationException e) {

        }
        // now with instance methods enabled.
        Assertions.assertNotNull(parser.parseExpression("#root.toString() ")
                .getValue(context.withInstanceMethods().build(), String.class));
    }


}
