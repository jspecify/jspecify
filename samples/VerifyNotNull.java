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
import static com.google.common.base.Verify.verifyNotNull;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.jspecify.annotations.NullnessUnspecified;

@NullMarked
class VerifyNotNull {
  Object x0(Object o) {
    verifyNotNull(o);
    return o;
  }

  Object x1(@NullnessUnspecified Object o) {
    verifyNotNull(o);
    return o;
  }

  Object x2(@Nullable Object o) {
    verifyNotNull(o);
    return o;
  }
}
