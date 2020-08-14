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

@NullAware
class SuperObjectUnionNull {
  void foo(Lib<? super @Nullable Object> lib, Object t, @Nullable Object tUnionNull) {
    lib.useT(t);

    lib.useT(tUnionNull);

    //

    lib.useTUnionNull(t);

    lib.useTUnionNull(tUnionNull);
  }

  interface Lib<T extends @Nullable Object> {
    void useT(T t);

    void useTUnionNull(@Nullable T t);
  }
}
