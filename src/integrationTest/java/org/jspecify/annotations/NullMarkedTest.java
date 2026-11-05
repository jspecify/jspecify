/*
 * Copyright 2018-2020 The JSpecify Authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jspecify.annotations;

import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toSet;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.condition.JRE.JAVA_8;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledOnJre;
import org.junit.jupiter.api.condition.EnabledOnJre;

@DisplayName("@NullMarked")
class NullMarkedTest {
  static Set<String> loadTargets() {
    return stream(NullMarked.class.getAnnotation(Target.class).value())
        .map(ElementType::toString)
        .collect(toSet());
  }

  @Test
  @EnabledOnJre(JAVA_8)
  void onlyBasicReflectionWorksUnderJava8() {
    Object unused = NullMarked.class.getMethods();
    /*
     * But reading the *annotations* on NullMarked would result in an exception: Those annotations
     * include @Target, which refers to MODULE, which doesn't exist under Java 8.
     *
     * (It happens to fail with ArrayStoreException.)
     */
    assertThrows(ArrayStoreException.class, NullMarked.class::getAnnotations);
  }

  @Test
  @DisabledOnJre(JAVA_8)
  void includesModuleTarget() {
    assertEquals(
        new HashSet<>(asList("TYPE", "METHOD", "CONSTRUCTOR", "PACKAGE", "MODULE")), loadTargets());
  }
}
