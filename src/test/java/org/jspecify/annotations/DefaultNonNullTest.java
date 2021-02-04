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

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

class DefaultNonNullTest {
  @Test
  void annotationIncludesModuleAsTargetForJava9() {
    Set<String> targets =
        Arrays.stream(DefaultNonNull.class.getAnnotation(Target.class).value())
            .map(ElementType::toString)
            .collect(Collectors.toSet());
    if (Runtime.version().compareToIgnoreOptional(Runtime.Version.parse("8")) > 0) {
      // testing with Java 9+
      assertEquals(new HashSet<String>(Arrays.asList("TYPE", "PACKAGE", "MODULE")), targets);
    } else {
      // testing with Java 8
      assertEquals(new HashSet<String>(Arrays.asList("TYPE", "PACKAGE")), targets);
    }
  }
}
