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
import org.jspecify.annotations.Nullable;
import org.jspecify.annotations.NullnessUnspecified;

class ProtoDeclarationAnnotations {
  @DefaultNonNull
  class User {
    void params(
        SomeProto proto,
        Object object,
        @NullnessUnspecified Object unspec,
        @Nullable Object unionNull) {
      proto.nonNull(object);
      // jspecify_nullness_not_enough_information
      proto.nonNull(unspec);
      // jspecify_nullness_mismatch
      proto.nonNull(unionNull);

      proto.unionNull(object);
      proto.unionNull(unspec);
      proto.unionNull(unionNull);
    }

    Object nonNull(SomeProto proto) {
      return proto.nonNull();
    }

    Object unionNull(SomeProto proto) {
      // jspecify_nullness_mismatch
      return proto.unionNull();
    }
  }

  @ProtoNonnullApi
  interface SomeProto {
    Object nonNull();

    @ProtoMethodMayReturnNull
    Object unionNull();

    void nonNull(Object o);

    void unionNull(@ProtoMethodAcceptsNullParameter Object o);
  }

  @interface ProtoMethodMayReturnNull {}

  @interface ProtoNonnullApi {}

  @interface ProtoMethodAcceptsNullParameter {}
}
