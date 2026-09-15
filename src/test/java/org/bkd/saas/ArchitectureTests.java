package org.bkd.saas;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.Test;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ResponseStatus;

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
}
