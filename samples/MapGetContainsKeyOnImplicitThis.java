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

import java.util.Map;
import org.jspecify.annotations.NullMarked;

@NullMarked
abstract class MapGetContainsKeyOnImplicitThis implements Map<Object, Object> {
  Object noCheckObject(Object key) {
    // jspecify_nullness_mismatch
    return get(key);
  }

  Object checkObject(Object key) {
    if (containsKey(key)) {
      // jspecify_but_expect_error
      return get(key);
    }
    return "";
  }
}