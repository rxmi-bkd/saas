package org.bkd.saas;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.methods;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchRule;
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
    JavaClasses classes = new ClassFileImporter().importPackages(BASE_PACKAGE);

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
    JavaClasses classes = new ClassFileImporter().importPackages(BASE_PACKAGE);

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
    JavaClasses classes = new ClassFileImporter().importPackages(BASE_PACKAGE);

    ArchRule rule =
        methods()
            .that()
            .areDeclaredInClassesThat()
            .areAnnotatedWith(RestController.class)
            .and()
            .arePublic()
            .should()
            .haveRawReturnType(ResponseEntity.class);

    rule.check(classes);
  }

  @Test
  void all_methods_should_be_public_or_private_or_protected() {
    JavaClasses classes =
        new ClassFileImporter()
            .withImportOption(new ImportOption.DoNotIncludeTests())
            .importPackages(BASE_PACKAGE);

    ArchRule rule = methods().should().bePublic().orShould().beProtected().orShould().bePrivate();

    rule.check(classes);
  }

  @Test
  void all_classes_should_be_public() {
    JavaClasses classes =
        new ClassFileImporter()
            .withImportOption(new ImportOption.DoNotIncludeTests())
            .importPackages(BASE_PACKAGE);

    ArchRule rule = classes().should().bePublic();

    rule.check(classes);
  }
}
