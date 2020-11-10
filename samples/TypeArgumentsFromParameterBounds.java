/*
 * Copyright 2020 The jspecify Authors.
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

import org.jspecify.annotations.*;

@DefaultNonNull
public class TypeArgumentsFromParameterBounds<
    T extends Object, E extends @Nullable Object, F extends @NullnessUnspecified Object> {}

@DefaultNonNull
class A {
  public void bar(TypeArgumentsFromParameterBounds<Test, Test, Test> a) {}
}

class B {
  public void bar(TypeArgumentsFromParameterBounds<Test, Test, Test> a) {}
}

class Test {}

@DefaultNonNull
class Use {
  public static void main(
      TypeArgumentsFromParameterBounds<Test, Test, Test> aNotNullNotNullNotNull,
      TypeArgumentsFromParameterBounds<Test, Test, @Nullable Test> aNotNullNotNullNull,
      TypeArgumentsFromParameterBounds<Test, @Nullable Test, Test> aNotNullNullNotNull,
      TypeArgumentsFromParameterBounds<Test, @Nullable Test, @Nullable Test> aNotNullNullNull,
      A a,
      B b) {
    a.bar(aNotNullNotNullNotNull);
    // jspecify_nullness_mismatch
    a.bar(aNotNullNotNullNull);
    // jspecify_nullness_mismatch
    a.bar(aNotNullNullNotNull);
    // jspecify_nullness_mismatch
    a.bar(aNotNullNullNull);

    // jspecify_nullness_mismatch
    b.bar(aNotNullNotNullNotNull);
    // jspecify_nullness_mismatch
    b.bar(aNotNullNotNullNull);
    b.bar(aNotNullNullNotNull);
    b.bar(aNotNullNullNull);
  }
}
