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
class TypeVariableUnspecToSelf<
    Never1T,
    ChildOfNever1T extends Never1T,
    UnspecChildOfNever1T extends @NullnessUnspecified Never1T,
    NullChildOfNever1T extends @Nullable Never1T,
    //
    Never2T extends Object,
    ChildOfNever2T extends Never2T,
    UnspecChildOfNever2T extends @NullnessUnspecified Never2T,
    NullChildOfNever2T extends @Nullable Never2T,
    //
    UnspecT extends @NullnessUnspecified Object,
    ChildOfUnspecT extends UnspecT,
    UnspecChildOfUnspecT extends @NullnessUnspecified UnspecT,
    NullChildOfUnspecT extends @Nullable UnspecT,
    //
    ParametricT extends @Nullable Object,
    ChildOfParametricT extends ParametricT,
    UnspecChildOfParametricT extends @NullnessUnspecified ParametricT,
    NullChildOfParametricT extends @Nullable ParametricT,
    //
    UnusedT> {
  Never1T x0(@NullnessUnspecified Never1T x) {
    // NOT-ENOUGH-INFORMATION
    return x;
  }

  ChildOfNever1T x1(@NullnessUnspecified ChildOfNever1T x) {
    // NOT-ENOUGH-INFORMATION
    return x;
  }

  UnspecChildOfNever1T x2(@NullnessUnspecified UnspecChildOfNever1T x) {
    // NOT-ENOUGH-INFORMATION
    return x;
  }

  NullChildOfNever1T x3(@NullnessUnspecified NullChildOfNever1T x) {
    // NOT-ENOUGH-INFORMATION
    return x;
  }

  Never2T x4(@NullnessUnspecified Never2T x) {
    // NOT-ENOUGH-INFORMATION
    return x;
  }

  ChildOfNever2T x5(@NullnessUnspecified ChildOfNever2T x) {
    // NOT-ENOUGH-INFORMATION
    return x;
  }

  UnspecChildOfNever2T x6(@NullnessUnspecified UnspecChildOfNever2T x) {
    // NOT-ENOUGH-INFORMATION
    return x;
  }

  NullChildOfNever2T x7(@NullnessUnspecified NullChildOfNever2T x) {
    // NOT-ENOUGH-INFORMATION
    return x;
  }

  UnspecT x8(@NullnessUnspecified UnspecT x) {
    // NOT-ENOUGH-INFORMATION
    return x;
  }

  ChildOfUnspecT x9(@NullnessUnspecified ChildOfUnspecT x) {
    // NOT-ENOUGH-INFORMATION
    return x;
  }

  UnspecChildOfUnspecT x10(@NullnessUnspecified UnspecChildOfUnspecT x) {
    // NOT-ENOUGH-INFORMATION
    return x;
  }

  NullChildOfUnspecT x11(@NullnessUnspecified NullChildOfUnspecT x) {
    // NOT-ENOUGH-INFORMATION
    return x;
  }

  ParametricT x12(@NullnessUnspecified ParametricT x) {
    // NOT-ENOUGH-INFORMATION
    return x;
  }

  ChildOfParametricT x13(@NullnessUnspecified ChildOfParametricT x) {
    // NOT-ENOUGH-INFORMATION
    return x;
  }

  UnspecChildOfParametricT x14(@NullnessUnspecified UnspecChildOfParametricT x) {
    // NOT-ENOUGH-INFORMATION
    return x;
  }

  NullChildOfParametricT x15(@NullnessUnspecified NullChildOfParametricT x) {
    // NOT-ENOUGH-INFORMATION
    return x;
  }
}
