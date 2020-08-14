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
class TypeVariableToObjectUnionNull<
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
  Object x0(Never1T x) {
    return x;
  }

  @Nullable
  Object x1(ChildOfNever1T x) {
    return x;
  }

  @Nullable
  Object x3(NullChildOfNever1T x) {
    return x;
  }

  @Nullable
  Object x4(Never2T x) {
    return x;
  }

  @Nullable
  Object x5(ChildOfNever2T x) {
    return x;
  }

  @Nullable
  Object x7(NullChildOfNever2T x) {
    return x;
  }

  @Nullable
  Object x12(ParametricT x) {
    return x;
  }

  @Nullable
  Object x13(ChildOfParametricT x) {
    return x;
  }

  @Nullable
  Object x15(NullChildOfParametricT x) {
    return x;
  }
}
