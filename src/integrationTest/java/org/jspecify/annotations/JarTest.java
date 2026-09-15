/*
 * Copyright 2026 The JSpecify Authors.
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

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.File;
import java.net.URL;
import java.util.jar.JarFile;
import org.junit.jupiter.api.Test;

class JarTest {
  @Test
  void containsLicense() throws Exception {
    URL codeSource = NullMarked.class.getProtectionDomain().getCodeSource().getLocation();
    try (JarFile jar = new JarFile(new File(codeSource.toURI()))) {
      assertNotNull(jar.getEntry("META-INF/LICENSE"));
    }
  }
}
