package org.bkd.saas;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.methods;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;
import java.util.Arrays;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

class ArchitectureTests {

  private static final String BASE_PACKAGE = "org.bkd.saas";

  @Test
  void all_exceptions_should_have_response_status_annotation() {
    JavaClasses classes = mainClasses();

    ArchRule rule =
        classes()
            .that()
            .areAssignableTo(Exception.class)
            .should()
            .beAnnotatedWith(ResponseStatus.class);

    rule.check(classes);
  }

  @Test
  void all_services_should_have_transactional_annotation() {
    JavaClasses classes = mainClasses();

    ArchRule rule =
        classes()
            .that()
            .areAnnotatedWith(Service.class)
            .should()
            .beAnnotatedWith(Transactional.class);

    rule.check(classes);
  }

  @Test
  void all_controller_methods_should_return_response_entity() {
    JavaClasses classes = mainClasses();

    ArchRule rule =
        methods()
            .that()
            .areDeclaredInClassesThat()
            .areAnnotatedWith(RestController.class)
            .should()
            .haveRawReturnType(ResponseEntity.class);

    rule.check(classes);
  }

  @Test
  void all_methods_should_be_public_or_private() {
    JavaClasses classes = mainClasses();

    ArchRule rule = methods().should().bePublic().orShould().bePrivate();

    rule.check(classes);
  }

  @Test
  void classes_with_layer_suffix_should_reside_in_matching_package() {
    JavaClasses classes = mainClasses();

    checkSuffixResidesIn(classes, "Controller", "..rest");
    checkSuffixResidesIn(classes, "Routes", "..rest");
    checkSuffixResidesIn(classes, "Request", "..rest.request");
    checkSuffixResidesIn(classes, "Service", "..service");
    checkSuffixResidesIn(classes, "Entity", "..db");
    checkSuffixResidesIn(classes, "Repository", "..db");
    checkSuffixResidesIn(classes, "Dto", "..dto");
    checkSuffixResidesIn(classes, "Enum", "..dto");
    checkSuffixResidesIn(classes, "Mapper", "..mapper");
    checkSuffixResidesIn(classes, "Exception", "..exception");
  }

  @Test
  void classes_in_layer_package_should_have_matching_suffix() {
    JavaClasses classes = mainClasses();

    checkPackageHasSuffix(classes, "..rest", "Controller", "Routes");
    checkPackageHasSuffix(classes, "..rest.request", "Request");
    checkPackageHasSuffix(classes, "..service", "Service");
    checkPackageHasSuffix(classes, "..db", "Entity", "Repository");
    checkPackageHasSuffix(classes, "..dto", "Dto", "Enum");
    checkPackageHasSuffix(classes, "..mapper", "Mapper", "MapperImpl");
    checkPackageHasSuffix(classes, "..exception", "Exception");
  }

  @Test
  void requests_should_not_be_used_outside_rest_layer() {
    noClasses()
        .that()
        .resideInAnyPackage("..service..", "..db..", "..mapper..", "..dto..")
        .should()
        .dependOnClassesThat()
        .resideInAPackage("..rest.request..")
        .check(mainClasses());
  }

  @Test
  void all_classes_should_be_public() {
    JavaClasses classes = mainClasses();
    ArchRule rule = classes().should().bePublic();
    rule.check(classes);
  }

  private static JavaClasses mainClasses() {
    return new ClassFileImporter()
        .withImportOption(new ImportOption.DoNotIncludeTests())
        .importPackages(BASE_PACKAGE);
  }

  private static void checkSuffixResidesIn(JavaClasses classes, String suffix, String pkg) {
    classes()
        .that()
        .haveSimpleNameEndingWith(suffix)
        .and()
        .resideInAPackage(BASE_PACKAGE + "..")
        .and()
        .areNotNestedClasses()
        .should()
        .resideInAPackage(pkg)
        .because("classes suffixed with '" + suffix + "' belong in " + pkg)
        .check(classes);
  }

  private static void checkPackageHasSuffix(JavaClasses classes, String pkg, String... suffixes) {
    classes()
        .that()
        .resideInAPackage(pkg)
        .and()
        .areNotNestedClasses()
        .and()
        .areNotInterfaces()
        .should(haveSimpleNameEndingWithAny(suffixes))
        .because("classes in " + pkg + " must end with " + String.join("/", suffixes))
        .check(classes);
  }

  private static ArchCondition<JavaClass> haveSimpleNameEndingWithAny(String... suffixes) {
    return new ArchCondition<>("have a simple name ending with " + String.join(" or ", suffixes)) {
      @Override
      public void check(JavaClass item, ConditionEvents events) {
        boolean matches = Arrays.stream(suffixes).anyMatch(item.getSimpleName()::endsWith);
        if (!matches) {
          events.add(
              SimpleConditionEvent.violated(
                  item, item.getName() + " does not end with " + String.join(" or ", suffixes)));
        }
      }
    };
  }
}
