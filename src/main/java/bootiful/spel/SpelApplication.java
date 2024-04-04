package bootiful.spel;

import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.TypeReference;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.aot.BeanFactoryInitializationAotContribution;
import org.springframework.beans.factory.aot.BeanFactoryInitializationAotProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.expression.spel.SpelNode;
import org.springframework.expression.spel.ast.BeanReference;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@SpringBootApplication
public class SpelApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpelApplication.class, args);
	}

	@Bean
	static SpelHints spelHints() {
		return new SpelHints();
	}

	private static final String SPEL = " @customerService.uid()  ";

	static class SpelHints implements BeanFactoryInitializationAotProcessor {

		private String resolveBeanName(BeanReference br) {
			try {
				var field = ReflectionUtils
						.findField(BeanReference.class, "beanName");
				field.setAccessible(true);
				return (String) field.get(br);
			} catch (IllegalAccessException ex) {
				throw new IllegalStateException("Could not resolve beanName for BeanReference [%s]".formatted(br), ex);
			}
		}

		private void resolveBeanNames(Set<String> beans, SpelNode spelNode) {
			if (spelNode instanceof BeanReference beanReference) {
				beans.add(resolveBeanName(beanReference));
			}
			var childCount = spelNode.getChildCount();

			if (childCount == 0) return;

			for (var i = 0; i < childCount; i++)
				resolveBeanNames(beans, spelNode.getChild(i));

		}

		@Override
		public BeanFactoryInitializationAotContribution processAheadOfTime(ConfigurableListableBeanFactory beanFactory) {
			var sep = new SpelExpressionParser();
			var rawParse = sep.parseRaw(SPEL).getAST();
			var beanNames = new HashSet<String>();
			resolveBeanNames(beanNames, rawParse);
			return (generationContext, beanFactoryInitializationCode) -> {
				var hints = generationContext.getRuntimeHints();
				for (var bean : beanNames) {
					var bd = beanFactory.getBeanDefinition(bean);
					var clzzName = bd.getBeanClassName();
					if (StringUtils.hasText(clzzName)) {
						hints.reflection().registerType(TypeReference.of(clzzName), MemberCategory.values());
					}
				}
			};
		}
	}



	@Bean
	ApplicationRunner illPutASpelOnYou(BeanFactory beanFactory) throws Exception {
		return args -> {

			var spel = new SpelExpressionParser();
			var sec = new StandardEvaluationContext();
			sec.setBeanResolver(new BeanFactoryResolver(beanFactory));
			var uid = spel.parseExpression(SPEL).getValue(sec, String.class);
			System.out.println("the UID is " + uid);

		};
	}
}

@Service
class CustomerService {

	public String uid() {
		return UUID.randomUUID().toString();
	}

}