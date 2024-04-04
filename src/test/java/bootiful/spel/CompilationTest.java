package bootiful.spel;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.ReflectionUtils;
import org.springframework.expression.spel.SpelCompilerMode;
import org.springframework.expression.spel.SpelParserConfiguration;
import org.springframework.expression.spel.standard.SpelExpression;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import java.util.ArrayList;
import java.util.Map;

import static bootiful.spel.Spel.TESLA;

class CompilationTest {

    @Test
    void compiled() throws Exception {
        Assertions.assertTrue(compilationMode(SpelCompilerMode.OFF, "name"));
        Assertions.assertFalse(compilationMode(SpelCompilerMode.IMMEDIATE, "nationality"));
    }

    private long stopwatch(Runnable runnable) {
        var start = System.nanoTime();
        runnable.run();
        var stop = System.nanoTime();
        return stop - start;
    }

    private boolean compilationMode(SpelCompilerMode compilerMode, String property) throws Exception {
        var config = new SpelParserConfiguration(compilerMode, getClass().getClassLoader());
        var parser = new SpelExpressionParser(config);
        var expr = parser.parseExpression(property);
        var runnable = (Runnable) () -> Assertions.assertNotNull(expr.getValue(TESLA));
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
        Assertions.assertTrue(avg < first, "the subsequent runs should be much faster than the first run");
        System.out.println(Map.of("first", first, "subsequent", avg, "compilerMode", compilerMode.toString(),
                "improvement factor", improvement));

        var field = SpelExpression.class.getDeclaredField("compiledAst");
        ReflectionUtils.makeAccessible(field);
        var compiledAst = field.get(expr);
        return null == compiledAst;

    }

}
