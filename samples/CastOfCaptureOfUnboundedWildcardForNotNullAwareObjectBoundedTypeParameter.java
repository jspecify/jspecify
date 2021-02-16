/*
 * Copyright 2021 The JSpecify Authors.
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

class CastOfCaptureOfUnboundedWildcardForNotNullAwareObjectBoundedTypeParameter {
  @DefaultNonNull
  interface Super {
    void consume(Bar bar);
  }

  @DefaultNonNull
  abstract class Sub implements Super {
    void x(Supplier<?> supplier) {
      if (supplier != null) {
        // jspecify_nullness_not_enough_information
        consume((Bar) supplier.get());
      }
    }
  }

  interface Supplier<T> {
    T get();
  }

  @DefaultNonNull
  interface Bar {}
}
