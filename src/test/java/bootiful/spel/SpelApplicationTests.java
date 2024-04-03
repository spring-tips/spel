package bootiful.spel;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.expression.spel.SpelCompilerMode;
import org.springframework.expression.spel.SpelEvaluationException;
import org.springframework.expression.spel.SpelParserConfiguration;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.SimpleEvaluationContext;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.*;

class SpelApplicationTests {

    private record Inventor(String name, Date birthday, String nationality, String[] inventionsArray) {
    }

    private final Inventor tesla = new Inventor("Nikola Tesla",
            new GregorianCalendar(1856, 7, 9).getTime(), "Serbian",
            new String[]{"induction motor", "commutator for dynamo"});

    @Test
    void literalExpressions() throws Exception {
        var expressionParser = new SpelExpressionParser();
        var expression = expressionParser.parseExpression(" 'hello, world'.concat('!').bytes ");
        var message = expression.getValue();
        if (message instanceof byte[] bytes)
            Assertions.assertEquals(new String(bytes), "hello, world!");
    }

    @Test
    void arraysAndCollections() throws Exception {
        var expressionParser = new SpelExpressionParser();

        // access
        var expression = expressionParser.parseExpression("inventionsArray[0].toUpperCase()");
        var result = expression.getValue(this.tesla, String.class);
        Assertions.assertEquals(result, "induction motor".toUpperCase());

        // array literals
        var array = expressionParser.parseExpression("new int[] { 1,2,3,4} ")
                .getValue(int[].class);
        Assertions.assertEquals(array.length, 4);

        // list literals
        var numberedList = expressionParser.parseExpression("{0 ,1,2,3  }")
                .getValue(List.class);
        Assertions.assertNotNull(numberedList, "the numbered list is not null");
        Assertions.assertTrue(!numberedList.isEmpty(), "the numbered list is not null");
        for (var i = 0; i < 4; i++)
            Assertions.assertTrue(numberedList.contains(i));

        // map literals
        var map = expressionParser.parseExpression("{ name: 'Bob'} ")
                .getValue(Map.class);
        Assertions.assertTrue(map.containsKey("name") && map.get("name").equals("Bob"));


    }

    @Test
    void methods() throws Exception {
        var expressionParser = new SpelExpressionParser();
        Assertions
                .assertEquals(expressionParser.parseExpression(" 'abc'.substring(1,3) ").getValue(String.class),
                        "bc");
    }

    @Test
    void operators() throws Exception {
        var expressionParser = new SpelExpressionParser();
        Assertions.assertEquals(expressionParser.parseExpression(" 2 == 2 ")
                .getValue(Boolean.class), true);
        Assertions.assertEquals(expressionParser.parseExpression(" 'black'  <  'block' ")
                .getValue(Boolean.class), true);

    }

    @Test
    void types() throws Exception {
        var ep = new SpelExpressionParser();
        var result = ep.parseExpression("T(java.math.RoundingMode).CEILING < T (java.math.RoundingMode).FLOOR")
                .getValue(Boolean.class);
        Assertions.assertTrue(result);
    }

    @Test
    void variables() throws Exception {
        class WritableInventor {
            private String name;

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }
        }
        var writableInventor = new WritableInventor();
        writableInventor.setName("Nick");
        Assertions.assertEquals(writableInventor.getName(), "Nick");
        var ep = new SpelExpressionParser();
        var context = SimpleEvaluationContext.forReadWriteDataBinding().build();
        context.setVariable("newName", "Mike");
        ep.parseExpression("name = #newName").getValue(context, writableInventor);
        Assertions.assertEquals("Mike", writableInventor.getName());  // "Mike Tesla"
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

    public static int add(int a, int b) {
        return a + b;
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


    private long stopwatch(Runnable runnable) {
        var start = System.nanoTime();
        runnable.run();
        var stop = System.nanoTime();
        return stop - start;
    }

    @Test
    void compilationImmediate() throws Exception {
        doWithCompilationMode(SpelCompilerMode.IMMEDIATE, "name");
    }


    private void doWithCompilationMode(SpelCompilerMode spelCompilerMode, String property) {
        var config = new SpelParserConfiguration(spelCompilerMode, getClass().getClassLoader());
        var parser = new SpelExpressionParser(config);
        var expr = parser.parseExpression(property);
        var runnable = (Runnable) () -> Assertions.assertNotNull(expr.getValue(this.tesla));
        var first = stopwatch(runnable);
        var maxRuns = 1000;
        var collection = new ArrayList<Long>();
        for (var i = 0; i < maxRuns; i++)
            collection.add(this.stopwatch(runnable));
        var avg = collection.stream()
                .mapToLong(value -> value)
                .summaryStatistics()
                .getAverage();
        var improvement = first / avg;
        System.out.println(Map.of("first", first, "subsequent", avg, "compilerMode", spelCompilerMode.toString(),
                "delta", improvement));
    }




}
