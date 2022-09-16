/*
 * Copyright 2022 The JSpecify Authors.
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

/**
 * Annotations addressing cross-cutting concerns among other annotation types. The single offering
 * so far is {@link Implies @Implies}.
 *
 * <h2 id="unfrozen">Important warning</h2>
 *
 * <p>These annotations are under development, and <b>any</b> aspect of their naming, locations, or
 * design is subject to change until the JSpecify 1.0 release. Moreover, supporting analysis tools
 * will be tracking the changes on varying schedules. Releasing a library using these annotations in
 * its API is <b>strongly discouraged</b> at this time.
 */
package org.jspecify.meta;
