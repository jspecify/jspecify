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

import org.jspecify.annotations.NullAware;
import org.jspecify.annotations.Nullable;
import org.jspecify.annotations.NullnessUnspecified;

@NullAware
class ComplexParametric {
  interface SuperSuper<T extends @Nullable Object> {
    Lib<T> t();

    Lib<@NullnessUnspecified T> tUnspec();

    Lib<@Nullable T> tUnionNull();

    default void checkT(Lib<T> lib) {}

    default void checkTUnspec(Lib<@NullnessUnspecified T> lib) {}

    default void checkTUnionNull(Lib<@Nullable T> lib) {}
  }

  static void checkNeverNull(Lib<? extends Object> lib) {}

  static <T> void checkUnspecNull(Lib<@NullnessUnspecified T> lib) {}

  interface SuperNeverNever<T extends Object & Foo> extends SuperSuper<T> {
    default void x() {
      checkNeverNull(t());
      // NOT-ENOUGH-INFORMATION
      checkUnspecNull(t());
      checkT(t());
      // NOT-ENOUGH-INFORMATION
      checkTUnspec(t());
      // MISMATCH
      checkTUnionNull(t());

      // NOT-ENOUGH-INFORMATION
      checkNeverNull(tUnspec());
      // NOT-ENOUGH-INFORMATION
      checkUnspecNull(tUnspec());
      // NOT-ENOUGH-INFORMATION
      checkT(tUnspec());
      // NOT-ENOUGH-INFORMATION
      checkTUnspec(tUnspec());
      // NOT-ENOUGH-INFORMATION
      checkTUnionNull(tUnspec());

      // MISMATCH
      checkNeverNull(tUnionNull());
      // NOT-ENOUGH-INFORMATION
      ComplexParametric.<T>checkUnspecNull(tUnionNull());
      // MISMATCH
      checkT(tUnionNull());
      // NOT-ENOUGH-INFORMATION
      checkTUnspec(tUnionNull());
      checkTUnionNull(tUnionNull());
    }
  }

  interface SuperNeverUnspec<T extends Object & @NullnessUnspecified Foo> extends SuperSuper<T> {
    default void x() {
      checkNeverNull(t());
      // NOT-ENOUGH-INFORMATION
      checkUnspecNull(t());
      checkT(t());
      // NOT-ENOUGH-INFORMATION
      checkTUnspec(t());
      // MISMATCH
      checkTUnionNull(t());

      // NOT-ENOUGH-INFORMATION
      checkNeverNull(tUnspec());
      // NOT-ENOUGH-INFORMATION
      checkUnspecNull(tUnspec());
      // NOT-ENOUGH-INFORMATION
      checkT(tUnspec());
      // NOT-ENOUGH-INFORMATION
      checkTUnspec(tUnspec());
      // NOT-ENOUGH-INFORMATION
      checkTUnionNull(tUnspec());

      // MISMATCH
      checkNeverNull(tUnionNull());
      // NOT-ENOUGH-INFORMATION
      ComplexParametric.<T>checkUnspecNull(tUnionNull());
      // MISMATCH
      checkT(tUnionNull());
      // NOT-ENOUGH-INFORMATION
      checkTUnspec(tUnionNull());
      checkTUnionNull(tUnionNull());
    }
  }

  interface SuperNeverUnionNull<T extends Object & @Nullable Foo> extends SuperSuper<T> {
    default void x() {
      checkNeverNull(t());
      // NOT-ENOUGH-INFORMATION
      checkUnspecNull(t());
      checkT(t());
      // NOT-ENOUGH-INFORMATION
      checkTUnspec(t());
      // MISMATCH
      checkTUnionNull(t());

      // NOT-ENOUGH-INFORMATION
      checkNeverNull(tUnspec());
      // NOT-ENOUGH-INFORMATION
      checkUnspecNull(tUnspec());
      // NOT-ENOUGH-INFORMATION
      checkT(tUnspec());
      // NOT-ENOUGH-INFORMATION
      checkTUnspec(tUnspec());
      // NOT-ENOUGH-INFORMATION
      checkTUnionNull(tUnspec());

      // MISMATCH
      checkNeverNull(tUnionNull());
      // NOT-ENOUGH-INFORMATION
      ComplexParametric.<T>checkUnspecNull(tUnionNull());
      // MISMATCH
      checkT(tUnionNull());
      // NOT-ENOUGH-INFORMATION
      checkTUnspec(tUnionNull());
      checkTUnionNull(tUnionNull());
    }
  }

  interface SuperUnspecNever<T extends @NullnessUnspecified Object & Foo> extends SuperSuper<T> {
    default void x() {
      checkNeverNull(t());
      // NOT-ENOUGH-INFORMATION
      checkUnspecNull(t());
      checkT(t());
      // NOT-ENOUGH-INFORMATION
      checkTUnspec(t());
      // MISMATCH
      checkTUnionNull(t());

      // NOT-ENOUGH-INFORMATION
      checkNeverNull(tUnspec());
      // NOT-ENOUGH-INFORMATION
      checkUnspecNull(tUnspec());
      // NOT-ENOUGH-INFORMATION
      checkT(tUnspec());
      // NOT-ENOUGH-INFORMATION
      checkTUnspec(tUnspec());
      // NOT-ENOUGH-INFORMATION
      checkTUnionNull(tUnspec());

      // MISMATCH
      checkNeverNull(tUnionNull());
      // NOT-ENOUGH-INFORMATION
      ComplexParametric.<T>checkUnspecNull(tUnionNull());
      // MISMATCH
      checkT(tUnionNull());
      // NOT-ENOUGH-INFORMATION
      checkTUnspec(tUnionNull());
      checkTUnionNull(tUnionNull());
    }
  }

  interface SuperUnspecUnspec<T extends @NullnessUnspecified Object & @NullnessUnspecified Foo>
      extends SuperSuper<T> {}

  interface SuperUnspecUnionNull<T extends @NullnessUnspecified Object & @Nullable Foo>
      extends SuperSuper<T> {}

  interface SuperUnionNullNever<T extends @Nullable Object & Foo> extends SuperSuper<T> {
    default void x() {
      checkNeverNull(t());
      // NOT-ENOUGH-INFORMATION
      checkUnspecNull(t());
      checkT(t());
      // NOT-ENOUGH-INFORMATION
      checkTUnspec(t());
      // MISMATCH
      checkTUnionNull(t());

      // NOT-ENOUGH-INFORMATION
      checkNeverNull(tUnspec());
      // NOT-ENOUGH-INFORMATION
      checkUnspecNull(tUnspec());
      // NOT-ENOUGH-INFORMATION
      checkT(tUnspec());
      // NOT-ENOUGH-INFORMATION
      checkTUnspec(tUnspec());
      // NOT-ENOUGH-INFORMATION
      checkTUnionNull(tUnspec());

      // MISMATCH
      checkNeverNull(tUnionNull());
      // NOT-ENOUGH-INFORMATION
      ComplexParametric.<T>checkUnspecNull(tUnionNull());
      // MISMATCH
      checkT(tUnionNull());
      // NOT-ENOUGH-INFORMATION
      checkTUnspec(tUnionNull());
      checkTUnionNull(tUnionNull());
    }
  }

  interface SuperUnionNullUnspec<T extends @Nullable Object & @NullnessUnspecified Foo>
      extends SuperSuper<T> {}

  interface SuperUnionNullUnionNull<T extends @Nullable Object & @Nullable Foo>
      extends SuperSuper<T> {
    default void x() {
      // MISMATCH
      checkNeverNull(t());
      // MISMATCH
      checkUnspecNull(t());
      checkT(t());
      // NOT-ENOUGH-INFORMATION
      checkTUnspec(t());
      // MISMATCH
      checkTUnionNull(t());

      // MISMATCH
      checkNeverNull(tUnspec());
      // NOT-ENOUGH-INFORMATION
      checkUnspecNull(tUnspec());
      // NOT-ENOUGH-INFORMATION
      checkT(tUnspec());
      // NOT-ENOUGH-INFORMATION
      checkTUnspec(tUnspec());
      // NOT-ENOUGH-INFORMATION
      checkTUnionNull(tUnspec());

      // MISMATCH
      checkNeverNull(tUnionNull());
      // NOT-ENOUGH-INFORMATION
      ComplexParametric.<T>checkUnspecNull(tUnionNull());
      // MISMATCH
      checkT(tUnionNull());
      // NOT-ENOUGH-INFORMATION
      checkTUnspec(tUnionNull());
      checkTUnionNull(tUnionNull());
    }
  }

  interface Foo {}

  interface Lib<T extends @Nullable Object> {}
}
