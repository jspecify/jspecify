/*
 * Copyright 2021 The JSpecify Authors.
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
<<<<<<<< HEAD:samples/IsNullOrEmpty.java
import static com.google.common.base.Strings.isNullOrEmpty;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
class IsNullOrEmpty {
  Object x(@Nullable String s) {
    return isNullOrEmpty(s) ? "" : s;
========
package org.jspecify.conformance.tests;

import org.jspecify.conformance.deps.Dep;

/** This is here only to prove that assertions can refer to classes in deps. */
public class UsesDep {
  public static Dep dep() {
    // test:cannot-convert:null? to Dep*
    return null;
>>>>>>>> 8f1c5091a1cc9cd6f2cc26a0f884394d430ae06f:conformance-tests/src/assertions/java/org/jspecify/conformance/tests/UsesDep.java
  }
}
