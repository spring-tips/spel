package bootiful.spel;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.expression.spel.SpelEvaluationException;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.SimpleEvaluationContext;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.Date;
import java.util.GregorianCalendar;

class SpelApplicationTests {

    private record Inventor(String name, Date birthday, String nationality) {
    }

    private final Inventor tesla = new Inventor("Nikola Tesla",
            new GregorianCalendar(1856, 7, 9).getTime(), "Serbian");

    @Test
    void helloWorld() throws Exception {
        var expressionParser = new SpelExpressionParser();
        var expression = expressionParser.parseExpression(
                " 'hello, world'.concat('!').bytes ");

        var message = expression.getValue();

        if (message instanceof byte[] bytes)
            Assertions.assertEquals(new String(bytes), "hello, world!");
    }

    @Test
    void rootObjects() throws Exception {
        var parser = new SpelExpressionParser();
        var expression = parser.parseExpression("name");
        var name = (String) expression.getValue(tesla);
        Assertions.assertEquals(name, "Nikola Tesla");
        var nameIsNikola = parser.parseExpression("name == 'Nikola Tesla'")
                .getValue(tesla, Boolean.class);
        Assertions.assertTrue(nameIsNikola, "the name is Nikola Tesla");
    }

    public static int add(int a, int b) {
        return a + b;
    }

    @Test
    void simpleEvaluationContextWithoutInstanceMethods() {

        var context = SimpleEvaluationContext
                .forReadOnlyDataBinding()
                .withRootObject(tesla);

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


}
