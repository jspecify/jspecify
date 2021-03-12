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

import java.util.stream.Stream;
import org.jspecify.nullness.NullMarked;
import org.jspecify.nullness.Nullable;

@NullMarked
class StreamFilterIsInstanceAndMapCast {
  Stream<Object> justFilter(Stream<@Nullable Object> s) {
    return s.filter(Object.class::isInstance);
  }

  Stream<Lib> filterAndMap(Stream<@Nullable Object> s) {
    return s.filter(Lib.class::isInstance).map(Lib.class::cast);
  }

  void filterAndMapAndLambda(Stream<@Nullable Object> s) {
    s.filter(Lib.class::isInstance).map(Lib.class::cast).forEach(l -> l.dereference());
  }

  void filterAndMapAndMethodReference(Stream<@Nullable Object> s) {
    s.filter(Lib.class::isInstance).map(Lib.class::cast).forEach(Lib::dereference);
  }

  // This passes (at the time of this writing, at least), but I usually don't use -source 11.
  // void filterAndMapAndStoreInVar(Stream<@Nullable Object> s) {
  //  var libs = s.filter(Lib.class::isInstance).map(Lib.class::cast);
  //  libs.forEach(Lib::dereference);
  // }

  interface Lib {
    void dereference();
  }
}
