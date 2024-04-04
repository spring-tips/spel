# Spring Expression Language

## History of Spring Expression Language
* JSP EL
* JSF EL
* JBoss EL
* OGNL
* Andy starts work on it. I'll never forget when he ripped out the Antlr code. [Who _does_ that](https://github.com/spring-projects/spring-framework/commit/886739f1d83bd82e3e68074c45b8b6514f36bcdc)?

## A Tour of the Language

* `LiteralExpressionsTest` - literal expressions
* `ArraysAndCollectionsTest` - create literal arrays, collections, and maps
* `MethodsTest` -  invoke custom methods 
* `OperatorsTest` - can't ask questions if you can't compare things... 
* `TypesTest` - work with the `.class` literal of a given type from SpEL
* `VariablesTest` - learn how to set and reference variables
* `EvaluationContextsTest` how to limit what may be evaluated in production
* `RootObjectsTest` - learn how to control the root object on which property accesses dereference
* `SelectionsTest` - learn how to filter collections or maps with selections
* `NavigationsTest` - the language makes it easy and safe to navigate property stacks
* `SpringTest` - see how the SpEL works with and within a Spring application.
* `CompilationTest` - did you know you can _compile_ the Spring Expression Language statement?? Spring Integration knows this. And they use it well. 
* `SpelApplication` - this example was inspired by this amazing commit from [Marcus da Coregio](https://github.com/marcusdacoregio/spring-security/commit/872267cd3a0487d0062a8025aa7e045054745c97) who shows how to, given a SpEL expression - in this case extracted from annotations that Spring cares about - register the corresponding bean for reflection using the Spring AOT component model.