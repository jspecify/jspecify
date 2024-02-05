/*
 * Copyright 2024 The JSpecify Authors.
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
package org.jspecify.taglet;

import com.sun.source.doctree.DocTree;
import com.sun.source.doctree.UnknownBlockTagTree;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.StringJoiner;
import javax.lang.model.element.Element;
import jdk.javadoc.doclet.Taglet;

/**
 * This taglet defines the {@code @jspecify_spec_nullness} block tag to more easily refer to the
 * JSpecify Nullness Specification. Usage:
 *
 * <ul>
 *   <li>{@code @jspecify_spec_nullness} expands to a link to the JSpecify Nullness Specification.
 *   <li>{@code @jspecify_spec_nullness #anchor} expands to a link to the given anchor of the
 *       JSpecify Nullness Specification.
 *   <li>{@code @jspecify_spec_nullness #anchor text} expands to a link to the given anchor of the
 *       JSpecify Nullness Specification with the given text.
 * </ul>
 */
public class JSpecifyNullnessTaglet implements Taglet {
  public Set<Location> getAllowedLocations() {
    return EnumSet.allOf(Location.class);
  }

  public boolean isInlineTag() {
    return false;
  }

  public String getName() {
    return "jspecify_spec_nullness";
  }

  public String toString(List<? extends DocTree> tags, Element element) {
    if (tags.isEmpty()) {
      // Shouldn't happen.
      return "";
    }
    StringJoiner sb = new StringJoiner(", ");
    for (DocTree t : tags) {
      String text = getTagText(t);
      String[] split = text.split(" ", 2);
      sb.add(formatTagText(split));
    }
    return formatTaglet(sb.toString());
  }

  private String getTagText(DocTree t) {
    if (t instanceof UnknownBlockTagTree) {
      UnknownBlockTagTree ubtt = (UnknownBlockTagTree) t;
      // TODO: nicer way to convert sub-trees?
      return ubtt.getContent().toString();
    } else {
      return "";
    }
  }

  private String formatTagText(String[] split) {
    // split has length 0, 1, or 2.
    String anchor, rest;
    switch (split.length) {
      case 0:
        anchor = "";
        rest = "link";
        break;
      case 1:
        anchor = split[0];
        rest = "link";
        break;
      case 2:
        anchor = split[0];
        rest = split[1];
        break;
      default:
        // Issue an error?
        anchor = "";
        rest = "";
    }
    return String.format("<A HREF=\"https://jspecify.dev/docs/spec/%s\">%s</A>", anchor, rest);
  }

  private String formatTaglet(String body) {
    return String.format(
        "<DT><SPAN CLASS=\"simpleTagLabel\">JSpecify Nullness Specification:</SPAN></DT><DD>%s</DD>",
        body);
  }
}
