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
class TypeVariableUnionNullToSelfUnionNull<
    Never1T,
    ChildOfNever1T extends Never1T,
    NullChildOfNever1T extends @Nullable Never1T,
    //
    Never2T extends Object,
    ChildOfNever2T extends Never2T,
    NullChildOfNever2T extends @Nullable Never2T,
    //
    ParametricT extends @Nullable Object,
    ChildOfParametricT extends ParametricT,
    NullChildOfParametricT extends @Nullable ParametricT,
    //
    UnusedT> {
  @Nullable
  Never1T x0(@Nullable Never1T x) {
    return x;
  }

  @Nullable
  ChildOfNever1T x1(@Nullable ChildOfNever1T x) {
    return x;
  }

  @Nullable
  NullChildOfNever1T x3(@Nullable NullChildOfNever1T x) {
    return x;
  }

  @Nullable
  Never2T x4(@Nullable Never2T x) {
    return x;
  }

  @Nullable
  ChildOfNever2T x5(@Nullable ChildOfNever2T x) {
    return x;
  }

  @Nullable
  NullChildOfNever2T x7(@Nullable NullChildOfNever2T x) {
    return x;
  }

  @Nullable
  ParametricT x12(@Nullable ParametricT x) {
    return x;
  }

  @Nullable
  ChildOfParametricT x13(@Nullable ChildOfParametricT x) {
    return x;
  }

  @Nullable
  NullChildOfParametricT x15(@Nullable NullChildOfParametricT x) {
    return x;
  }
}
