/*
 * Copyright (C) 2014 Google, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package retrofit2.processor;

import static com.google.auto.common.MoreElements.isAnnotationPresent;

import retrofit.http.Retrofit;
import com.google.auto.common.MoreElements;
import com.google.auto.common.SuperficialValidation;
import com.google.auto.service.AutoService;
import com.google.common.collect.ImmutableSet;

import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;

/**
 * Annotation processor that checks that the type that {@link Retrofit.Builder} is applied to is
 * nested inside an {@code @Retrofit} class. The actual code generation for builders is done in
 * {@link RetrofitProcessor}.
 *
 * @author Éamonn McManus
 */
@AutoService(Processor.class)
public class RetrofitBuilderProcessor extends AbstractProcessor {
  @Override
  public Set<String> getSupportedAnnotationTypes() {
    return ImmutableSet.of(
        Retrofit.Builder.class.getCanonicalName(), Retrofit.Validate.class.getCanonicalName());
  }

  @Override
  public SourceVersion getSupportedSourceVersion() {
    return SourceVersion.latest();
  }

  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    Set<? extends Element> builderTypes =
        roundEnv.getElementsAnnotatedWith(Retrofit.Builder.class);
    if (!SuperficialValidation.validateElements(builderTypes)) {
      return false;
    }
    for (Element annotatedType : builderTypes) {
      // Double-check that the annotation is there. Sometimes the compiler gets confused in case of
      // erroneous source code. SuperficialValidation should protect us against this but it doesn't
      // cost anything to check again.
      if (isAnnotationPresent(annotatedType, Retrofit.Builder.class)) {
        validate(
            annotatedType,
            "@Retrofit.Builder can only be applied to a class or interface inside an"
                + " @Retrofit class");
      }
    }

    Set<? extends Element> validateMethods =
        roundEnv.getElementsAnnotatedWith(Retrofit.Validate.class);
    if (!SuperficialValidation.validateElements(validateMethods)) {
      return false;
    }
    for (Element annotatedMethod : validateMethods) {
      if (isAnnotationPresent(annotatedMethod, Retrofit.Validate.class)) {
        validate(
            annotatedMethod,
            "@Retrofit.Validate can only be applied to a method inside an @Retrofit class");
      }
    }
    return false;
  }

  private void validate(Element annotatedType, String errorMessage) {
    Element container = annotatedType.getEnclosingElement();
    if (!MoreElements.isAnnotationPresent(container, Retrofit.class)) {
      processingEnv.getMessager().printMessage(
          Diagnostic.Kind.ERROR, errorMessage, annotatedType);
    }
  }
}
