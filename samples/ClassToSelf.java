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
class ClassToSelf {
  ClassToSelf x0(ClassToSelf x) {
    return x;
  }

  ClassToSelf x1(@NullnessUnspecified ClassToSelf x) {
    // NOT-ENOUGH-INFORMATION
    return x;
  }

  ClassToSelf x2(@Nullable ClassToSelf x) {
    // MISMATCH
    return x;
  }

  @NullnessUnspecified
  ClassToSelf x3(ClassToSelf x) {
    return x;
  }

  @NullnessUnspecified
  ClassToSelf x4(@NullnessUnspecified ClassToSelf x) {
    // NOT-ENOUGH-INFORMATION
    return x;
  }

  @NullnessUnspecified
  ClassToSelf x5(@Nullable ClassToSelf x) {
    // NOT-ENOUGH-INFORMATION
    return x;
  }

  @Nullable
  ClassToSelf x6(ClassToSelf x) {
    return x;
  }

  @Nullable
  ClassToSelf x7(@NullnessUnspecified ClassToSelf x) {
    return x;
  }

  @Nullable
  ClassToSelf x8(@Nullable ClassToSelf x) {
    return x;
  }
}
