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

import org.jspecify.annotations.DefaultNonNull;
import org.jspecify.annotations.Nullable;

@DefaultNonNull
public class NullnessUnspecifiedTypeParameter<T> {
  public void foo(T t) {}

  public void bar(Test s, T t) {}
}

class Test {}

class Use {
  void main(
      NullnessUnspecifiedTypeParameter<Object> a1,
      NullnessUnspecifiedTypeParameter<@Nullable Object> a2,
      Test x) {
    // jspecify_nullness_mismatch
    a1.foo(null);
    a1.foo(1);

    // jspecify_nullness_mismatch
    a2.foo(null);
    a2.foo(1);

    // jspecify_nullness_mismatch
    a1.bar(null, null);
    // jspecify_nullness_mismatch
    a1.bar(x, null);
    a1.bar(x, 1);

    // jspecify_nullness_mismatch
    a2.bar(null, null);
    // jspecify_nullness_mismatch
    a2.bar(x, null);
    a2.bar(x, 1);
  }
}
