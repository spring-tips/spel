package bootiful.spel;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.SimpleEvaluationContext;

class VariablesTest {

    static class WritableInventor {

        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    @Test
    void variables() throws Exception {
        var writableInventor = new WritableInventor();
        writableInventor.setName("Nick");
        Assertions.assertEquals(writableInventor.getName(), "Nick");
        var ep = new SpelExpressionParser();
        var context = SimpleEvaluationContext.forReadWriteDataBinding().build();
        context.setVariable("newName", "Mike");
        ep.parseExpression("name = #newName").getValue(context, writableInventor);
        Assertions.assertEquals("Mike", writableInventor.getName());  // "Mike Tesla"
    }


}
