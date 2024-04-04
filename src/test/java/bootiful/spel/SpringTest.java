package bootiful.spel;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Set;

import static bootiful.spel.Spel.TESLA;

class SpringTest {


    @Service
    static class InventorRegistry {

        public Collection<Spel.Inventor> getInventors() {
            return Set.of(TESLA);
        }
    }

    @Configuration
    static class SimpleConfig {

        @Bean
        InventorRegistry registry() {
            return new InventorRegistry();
        }
    }


    @Test
    void beans() throws Exception {
        var applicationContext = new AnnotationConfigApplicationContext(SimpleConfig.class);
        var expressionParser = new SpelExpressionParser();
        var sec = new StandardEvaluationContext();
        sec.setBeanResolver(new BeanFactoryResolver(applicationContext));
//        sec.setBeanResolver((context, beanName) -> applicationContext.getBean(beanName));
        var collectionOfInventors = expressionParser.parseExpression("@registry.inventors")
                .getValue(sec, Collection.class);
        System.out.println(collectionOfInventors);
    }


    @Configuration
    static class SpelConfiguration {

        private final Collection<Spel.Inventor> inventions;

        SpelConfiguration(@Value("#{ @registry.inventors }") Collection<Spel.Inventor> inventions) {
            System.out.println("the inventions are [" + inventions + "]");
            this.inventions = inventions;
        }
    }

    @Test
    void spring() {
        var applicationContext = new AnnotationConfigApplicationContext(SimpleConfig.class,
                SpelConfiguration.class);
        applicationContext.start();
        Assertions.assertEquals(applicationContext.getBean(SpelConfiguration.class).inventions,
                applicationContext.getBean(InventorRegistry.class).getInventors());
    }

}
