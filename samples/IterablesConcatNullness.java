/*
 * Copyright 2026 The JSpecify Authors.
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

@NullMarked
abstract class IterablesConcatNullness {

  interface Iterator<E extends @Nullable Object> {
    boolean hasNext();

    E next();
  }

  interface Iterable<T extends @Nullable Object> {
    Iterator<T> iterator();
  }

  abstract <T extends @Nullable Object> Iterable<T> concat(
      Iterable<? extends T> a, Iterable<? extends T> b);

  abstract <T extends @Nullable Object> T get(Iterable<T> iterable, int position);

  abstract <T extends @Nullable Object> T[] toArray(Iterable<? extends T> iterable, Class<T> type);

  void testConcatNullable(
      Iterable<@Nullable Integer> integers, Iterable<@Nullable String> strings) {
    Iterable<@Nullable Object> result = concat(integers, strings);
    Iterator<@Nullable Object> iter = result.iterator();
    Object element = iter.next();
    // jspecify_nullness_mismatch
    element.toString();
  }

  void testConcatNonNull(Iterable<Integer> integers, Iterable<String> strings) {
    Iterable<Object> result = concat(integers, strings);
    Iterator<Object> iter = result.iterator();
    Object element = iter.next();
    element.toString();
  }

  void testConcatMixed(Iterable<@Nullable Integer> nullable, Iterable<Integer> nonNull) {
    Iterable<@Nullable Integer> result = concat(nullable, nonNull);
    Iterator<@Nullable Integer> iter = result.iterator();
    Integer element = iter.next();
    // jspecify_nullness_mismatch
    element.intValue();
  }

  void testGetNullable(Iterable<@Nullable String> strings) {
    String s = get(strings, 0);
    // jspecify_nullness_mismatch
    s.length();
  }

  void testGetNonNull(Iterable<String> strings) {
    String s = get(strings, 0);
    s.length();
  }

  void testToArrayNullable(Iterable<@Nullable String> strings) {
    @Nullable String[] arr = toArray(strings, String.class);
    String element = arr[0];
    // jspecify_nullness_mismatch
    element.length();
  }

  void testToArrayNonNull(Iterable<String> strings) {
    String[] arr = toArray(strings, String.class);
    String element = arr[0];
    element.length();
  }
}
