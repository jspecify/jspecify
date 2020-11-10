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
public class IgnoreAnnotations {
  @Nullable public Derived field = null;

  @Nullable
  public Derived foo(Derived x, @NullnessUnspecified Base y) {
    return null;
  }

  public Derived everythingNotNullable(Derived x) {
    return null;
  }

  public @Nullable Derived everythingNullable(@Nullable Derived x) {
    return null;
  }

  public @NullnessUnspecified Derived everythingUnknown(@NullnessUnspecified Derived x) {
    return null;
  }
}

class Base {
  void foo() {}
}

class Derived extends Base {}

class Use {
  static void main(IgnoreAnnotations a, Derived x) {
    a.foo(x, null).foo();
    // jspecify_nullness_mismatch
    a.foo(null, x).foo();

    a.field.foo();

    // jspecify_nullness_mismatch
    a.everythingNotNullable(null).foo();
    a.everythingNotNullable(x).foo();

    a.everythingNullable(null).foo();

    a.everythingUnknown(null).foo();
  }
}
