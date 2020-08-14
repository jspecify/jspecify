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
class UnionTypeArgumentWithUseSite {
  interface Super<T extends @Nullable Object> {
    void t(T t);

    void tUnionNull(@Nullable T t);
  }

  interface Sub extends Super<Object> {
    @Override
    void t(Object t);

    @Override
    void tUnionNull(@Nullable Object t);
  }

  interface SubUnionNull extends Super<@Nullable Object> {
    @Override
    void t(@Nullable Object t);

    @Override
    void tUnionNull(@Nullable Object t);
  }

  interface SubWeaker extends Super<Object> {
    @Override
    // MISMATCH
    void tUnionNull(Object t);
  }

  interface SubWeakerUnionNull extends Super<@Nullable Object> {
    @Override
    // MISMATCH
    void t(Object t);

    @Override
    // MISMATCH
    void tUnionNull(Object t);
  }
}
