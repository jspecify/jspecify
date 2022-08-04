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
package org.jspecify.nullness;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assumptions.assumeFalse;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("@NullMarked")
class NullMarkedTest {
  boolean isJava8() {
    String version = System.getProperty("java.version");
    return version.startsWith("1.8");
  }

  Set<String> loadTargets() {
    return Arrays.stream(NullMarked.class.getAnnotation(Target.class).value())
        .map(ElementType::toString)
        .collect(Collectors.toSet());
  }

  @Nested
  @DisplayName("with Java 8")
  class WithJava8 {
    @BeforeEach
    void assumeJava8() {
      assumeTrue(isJava8());
    }

    @Test
    void basicReflectionDoesNotThrowException() {
      Object unused = NullMarked.class.getMethods();
      /*
       * But reading the *annotations* on NullMarked would result in an exception: Those annotations
       * include @Target, which refers to MODULE, which doesn't exist under Java 8.
       */
    }
  }

  @Nested
  @DisplayName("with Java 9+")
  class WithJava9OrLater {
    @BeforeEach
    void assumeJava9OrLater() {
      assumeFalse(isJava8());
    }

    @Test
    void annotationIncludesModuleAsTarget() {
      Set<String> targets = loadTargets();
      assertEquals(
          new HashSet<String>(Arrays.asList("TYPE", "METHOD", "CONSTRUCTOR", "PACKAGE", "MODULE")),
          targets);
    }
  }
}
