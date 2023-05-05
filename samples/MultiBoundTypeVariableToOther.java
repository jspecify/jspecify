/*
 * Copyright 2020 The JSpecify Authors.
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
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.jspecify.annotations.NullnessUnspecified;

@NullMarked
class MultiBoundTypeVariableToOther {
  <T extends Object & Lib> Lib x0(T x) {
    return x;
  }

  <T extends Object & @NullnessUnspecified Lib> Lib x1(T x) {
    // jspecify_but_expect_warning
    return x;
  }

  <T extends Object & @Nullable Lib> Lib x2(T x) {
    // jspecify_but_expect_error
    return x;
  }

  <T extends @NullnessUnspecified Object & Lib> Lib x3(T x) {
    // jspecify_but_expect_warning
    return x;
  }

  <T extends @NullnessUnspecified Object & @NullnessUnspecified Lib> Lib x4(T x) {
    // jspecify_nullness_not_enough_information
    return x;
  }

  <T extends @NullnessUnspecified Object & @Nullable Lib> Lib x5(T x) {
    // jspecify_nullness_not_enough_information
    return x;
  }

  <T extends @Nullable Object & Lib> Lib x6(T x) {
    // jspecify_but_expect_error
    return x;
  }

  <T extends @Nullable Object & @NullnessUnspecified Lib> Lib x7(T x) {
    // jspecify_nullness_not_enough_information jspecify_but_expect_error
    return x;
  }

  <T extends @Nullable Object & @Nullable Lib> Lib x8(T x) {
    // jspecify_nullness_mismatch
    return x;
  }

  interface Lib {}
}
