// Copyright 2020 The JSpecify Authors
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.devtools.javatools.typeannotationrefactoring;

import static com.google.common.base.Verify.verify;
import static com.google.common.base.Verify.verifyNotNull;
import static com.google.common.collect.Iterables.getOnlyElement;
import static com.google.common.collect.Range.closedOpen;
import static com.sun.source.tree.Tree.Kind.IDENTIFIER;
import static com.sun.source.tree.Tree.Kind.MEMBER_SELECT;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.Files.readAllBytes;
import static java.nio.file.Files.write;
import static java.util.Arrays.stream;
import static java.util.Comparator.comparing;
import static javax.tools.Diagnostic.Kind.ERROR;
import static javax.tools.StandardLocation.PLATFORM_CLASS_PATH;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Range;
import com.sun.source.tree.AnnotatedTypeTree;
import com.sun.source.tree.AnnotationTree;
import com.sun.source.tree.ClassTree;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.IdentifierTree;
import com.sun.source.tree.ImportTree;
import com.sun.source.tree.LiteralTree;
import com.sun.source.tree.MemberSelectTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.NewArrayTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.TypeParameterTree;
import com.sun.source.tree.VariableTree;
import com.sun.source.tree.WildcardTree;
import com.sun.source.util.SimpleTreeVisitor;
import com.sun.source.util.TreePath;
import com.sun.source.util.TreePathScanner;
import com.sun.tools.javac.file.JavacFileManager;
import com.sun.tools.javac.parser.JavacParser;
import com.sun.tools.javac.parser.ParserFactory;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.JCTree.JCCompilationUnit;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.Log;
import com.sun.tools.javac.util.Options;
import java.io.IOError;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Stream;
import javax.lang.model.element.Name;
import javax.tools.DiagnosticCollector;
import javax.tools.DiagnosticListener;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;

/**
 * A best effort refactoring to migrate Checker Framework {@code @Nullable} type annotations to
 * similar-ish JSpecify annotations.
 *
 * <p>usage: CheckerFrameworkToJspecifyRefactoring [files]
 */
public final class CheckerFrameworkToJspecifyRefactoring {
  public static void main(String[] args) {
    stream(args).parallel().forEach(file -> process(file));
  }

  private static void process(String file) {
    if (!file.endsWith(".java")) {
      return;
    }
    try {
      Path path = Paths.get(file);
      String input = new String(readAllBytes(path), UTF_8);
      String output = refactor(input);
      if (!input.equals(output)) {
        write(path, output.getBytes(UTF_8));
      }
    } catch (IOException e) {
      throw new UncheckedIOException(file, e);
    }
  }

  private static final ImmutableMap<String, String> SUBSTITUTES =
      ImmutableMap.of("Nullable", "Nullable", "PolyNull", "Nullable");

  private static String refactor(String input) {
    SortedMap<Range<Integer>, String> definiteReplacements = new TreeMap<>(BY_START_THEN_END);
    SortedMap<Range<Integer>, String> possibleReplacements = new TreeMap<>(BY_START_THEN_END);
    Context context = new Context();
    JCCompilationUnit unit = parse(context, input);
    boolean[] sawAnnotatedForNullness = new boolean[1];

    new TreePathScanner<Void, Void>() {
      boolean addedImports;
      boolean inNullHostileClass;
      boolean inJavaUtil;

      @Override
      public Void visitCompilationUnit(CompilationUnitTree node, Void aVoid) {
        // TODO(cpovirk): Calling Tree.toString() is a hack.
        inJavaUtil = node.getPackageName().toString().startsWith("java.util");
        return super.visitCompilationUnit(node, aVoid);
      }

      @Override
      public Void visitClass(ClassTree node, Void aVoid) {
        boolean oldInNullHostileClass = inNullHostileClass;
        try {
          inNullHostileClass |=
              node.getSimpleName().contentEquals("ConcurrentHashMap")
                  || node.getSimpleName().contentEquals("ConcurrentLinkedDeque")
                  || node.getSimpleName().contentEquals("ConcurrentSkipListMap")
                  || node.getSimpleName().contentEquals("ConcurrentSkipListSet")
                  || node.getSimpleName().contentEquals("Dictionary")
                  || node.getSimpleName().contentEquals("Hashtable")
                  || node.getSimpleName().contentEquals("Properties")
                  || node.getSimpleName().contentEquals("UIDefaults")
                  || node.getSimpleName().contentEquals("UIManager");
          return super.visitClass(node, aVoid);
        } finally {
          inNullHostileClass = oldInNullHostileClass;
        }
      }

      @Override
      public Void visitImport(ImportTree node, Void aVoid) {
        if (!addedImports) {
          addedImports = true;
          definiteReplacements.put(atStart(node), "import org.jspecify.annotations.Nullable;\n");
          possibleReplacements.put(
              atStart(node),
              "import org.jspecify.annotations.NullMarked;\n"
                  + "import org.jspecify.annotations.Nullable;\n");
        }
        if (node.getQualifiedIdentifier().toString().startsWith("org.checkerframework.")) {
          definiteReplacements.put(inPlaceOfNodeAndTrailingNewline(node, unit), "");
        }
        return super.visitImport(node, aVoid);
      }

      @Override
      public Void visitAnnotation(AnnotationTree node, Void aVoid) {
        String simpleName = getSimpleName(node.getAnnotationType());
        if (SUBSTITUTES.containsKey(simpleName)) {
          // putIfAbsent in case we're removing the annotation entirely (for <@Nullable T>).
          definiteReplacements.putIfAbsent(
              inPlaceOf(node, unit), "@" + SUBSTITUTES.get(simpleName));
        } else if (simpleName.equals("AnnotatedFor")) {
          sawAnnotatedForNullness[0] =
              node.getArguments().stream()
                  .flatMap(
                      a ->
                          a instanceof NewArrayTree
                              ? ((NewArrayTree) a).getInitializers().stream()
                              : Stream.of(a))
                  .anyMatch(a -> ((LiteralTree) a).getValue().equals("nullness"));
          definiteReplacements.put(inPlaceOf(node, unit), "");
          possibleReplacements.put(inPlaceOf(node, unit), "@NullMarked");
        } else if (CF_ANNOTATIONS.contains(simpleName)) {
          definiteReplacements.put(inPlaceOf(node, unit), "");
        }
        return super.visitAnnotation(node, aVoid);
      }

      @Override
      public Void visitTypeParameter(TypeParameterTree node, Void aVoid) {
        if (node.getBounds().isEmpty()) {
          possibleReplacements.put(atEnd(node, unit), " extends @Nullable Object");
        } else if (soleBoundIsNonNullObject(node)) {
          definiteReplacements.put(inPlaceOf(node, unit), node.getName().toString());
          /*
           * Don't visit children: Doing so may produce overlapping edits to remove individual
           * annotations (@NonNull on the bound and/or an annotation on the type parameter itself).
           */
          return null;
        }
        for (AnnotationTree a : node.getAnnotations()) {
          definiteReplacements.put(inPlaceOf(a, unit), "");
        }
        return super.visitTypeParameter(node, aVoid);
      }

      @Override
      public Void visitAnnotatedType(AnnotatedTypeTree node, Void aVoid) {
        if (node.getUnderlyingType() instanceof WildcardTree) {
          for (AnnotationTree a : node.getAnnotations()) {
            definiteReplacements.put(inPlaceOf(a, unit), "");
          }
        }
        return super.visitAnnotatedType(node, aVoid);
      }

      @Override
      public Void visitVariable(VariableTree node, Void aVoid) {
        Tree parent = getCurrentPath().getParentPath().getLeaf();
        if (!(parent instanceof MethodTree)) {
          return super.visitVariable(node, aVoid);
        }
        MethodTree method = (MethodTree) parent;

        if (method.getReceiverParameter() == node) {
          if (method.getParameters().isEmpty()) {
            definiteReplacements.put(inPlaceOf(node, unit), "");
          } else {
            definiteReplacements.put(
                closedOpen(startPos(node), startPos(method.getParameters().get(0))), "");
          }
          /*
           * Don't visit children: Doing so may produce overlapping edits to remove individual
           * annotations (typically @Nullable on the receiver variable).
           */
          return null;
        }

        /*
         * TODO(cpovirk): The following is imperfect. Notably, some classes are only *partially*
         * null-hostile. An example is ConcurrentHashMap, whose 2-arg remove method rejects null
         * keys but tolerates null values, even though ConcurrentHashMap normally rejects both. But
         * maybe we'll just fix those up by hand, rather than add special cases here.
         */
        if (inJavaUtil
            && !inNullHostileClass
            && (method.getName().contentEquals("contains")
                || method.getName().contentEquals("containsKey")
                || method.getName().contentEquals("containsValue")
                || method.getName().contentEquals("get")
                || method.getName().contentEquals("indexOf")
                || method.getName().contentEquals("lastIndexOf")
                || method.getName().contentEquals("remove")
                || method.getName().contentEquals("removeFirstOccurrence")
                || method.getName().contentEquals("removeLastOccurrence"))
            && method.getParameters().size() == 1
            && (node.getType().getKind() == IDENTIFIER || node.getType().getKind() == MEMBER_SELECT)
            && getSimpleName(node.getType()).equals("Object")) {
          definiteReplacements.put(inPlaceOf(node, unit), "@Nullable Object " + node.getName());
          // Don't visit children.
          return null;
        }

        if (inJavaUtil
            && !inNullHostileClass
            && (method.getName().contentEquals("getOrDefault") // first parameter only (type Object)
                || method.getName().contentEquals("remove")) // both parameters
            && method.getParameters().size() == 2
            && (node.getType().getKind() == IDENTIFIER || node.getType().getKind() == MEMBER_SELECT)
            && getSimpleName(node.getType()).equals("Object")) {
          definiteReplacements.put(inPlaceOf(node, unit), "@Nullable Object " + node.getName());
          // Don't visit children.
          return null;
        }

        if (inJavaUtil
            && !inNullHostileClass
            && method.getName().contentEquals("containsAll")
            && method.getParameters().size() == 1) {
          definiteReplacements.put(inPlaceOf(node, unit), "Collection<?> " + node.getName());
          // Don't visit children.
          return null;
        }

        /*
         * Calling removeAll(collectionContainingNull) and retainAll(collectionContainingNull)
         * typically works even on null-hostile collections: They are often implemented by iterating
         * over the *receiver* collection and calling contains(e) on the *argument* collection.
         * Thus, if there's a problem, it comes from the class of the *argument*. Those methods are
         * even documented accordingly. So we make the parameter type `Collection<?>` even in
         * null-hostile classes.
         *
         * TODO(cpovirk): But there are still exceptions: ConcurrentSkipListSet iterates over the
         * argument collection and calls this.remove(e), which rejects null. It is documented
         * accordingly. It would be nice for us to annotate it accordingly. But maybe we'll just fix
         * that up by hand, rather than add a special case here.
         *
         * A *partial* exception is AbstractSet.removeAll, which *sometimes* uses a
         * ConcurrentSkipListSet-style implementation and sometimes does not (at least for now:
         * https://bugs.openjdk.java.net/browse/JDK-6394757). However, we want to accept
         * Collection<?> there because some AbstractSet subclasses permit this.remove(null).
         * TODO(cpovirk): It could be nice to add "fake overrides" with signature
         * `removeAll(Collection<? extends Object>)` in problematic subclasses, but we'd want to
         * make sure that checkers would actually apply them (since those overrides don't exist in
         * the java.** sources).
         */
        if (inJavaUtil
            && (method.getName().contentEquals("removeAll")
                || method.getName().contentEquals("retainAll"))
            && method.getParameters().size() == 1) {
          definiteReplacements.put(inPlaceOf(node, unit), "Collection<?> " + node.getName());
          // Don't visit children.
          return null;
        }

        /*
         * TODO(cpovirk): Consider also putting @Nullable on the parameter of all equals() methods,
         * @Nullable on the String and Throwable parameters of all Throwable constructors, and
         * anything else that can typically be done mechanically. Better yet, make such changes
         * upstream.
         */
        return super.visitVariable(node, aVoid);
      }
    }.scan(new TreePath(unit), null);
    if (sawAnnotatedForNullness[0]) {
      definiteReplacements.putAll(possibleReplacements);
    }
    return applyFixes(input, definiteReplacements);
  }

  private static boolean soleBoundIsNonNullObject(TypeParameterTree node) {
    if (node.getBounds().size() != 1) {
      return false;
    }
    Tree boundAsTree = getOnlyElement(node.getBounds());
    if (!(boundAsTree instanceof AnnotatedTypeTree)) {
      return false;
    }
    AnnotatedTypeTree boundAsAnnotatedTree = (AnnotatedTypeTree) boundAsTree;
    return getSimpleName(boundAsAnnotatedTree.getUnderlyingType()).equals("Object")
        && isUnannotatedOrNonNull(boundAsAnnotatedTree);
  }

  private static boolean isUnannotatedOrNonNull(AnnotatedTypeTree node) {
    switch (node.getAnnotations().size()) {
      case 0:
        return true;
      case 1:
        AnnotationTree annotation = getOnlyElement(node.getAnnotations());
        return getSimpleName(annotation.getAnnotationType()).equals("NonNull");
      default:
        // TODO(cpovirk): This could in principle come up. Handle it.
        throw new RuntimeException(
            "Handling of bounds with multiple annotations not yet implemented for " + node);
    }
  }

  private static Range<Integer> atStart(Tree classTree) {
    int startPos = startPos(classTree);
    return closedOpen(startPos, startPos);
  }

  private static Range<Integer> atEnd(Tree node, JCCompilationUnit unit) {
    int endPos = endPos(node, unit);
    return closedOpen(endPos, endPos);
  }

  private static Range<Integer> inPlaceOf(Tree node, JCCompilationUnit unit) {
    return closedOpen(startPos(node), endPos(node, unit));
  }

  private static Range<Integer> inPlaceOfNodeAndTrailingNewline(Tree node, JCCompilationUnit unit) {
    return closedOpen(startPos(node), endPos(node, unit) + 1);
  }

  private static int startPos(Tree tree) {
    int startPos = ((JCTree) tree).getStartPosition();
    verify(startPos >= 0);
    return startPos;
  }

  private static int endPos(Tree node, JCCompilationUnit unit) {
    int endPos = ((JCTree) node).getEndPosition(unit.endPositions);
    verify(endPos >= 0);
    return endPos;
  }

  /** Parses {@code input} as a Java compilation unit. */
  private static JCCompilationUnit parse(Context context, String input) {
    DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
    context.put(DiagnosticListener.class, diagnostics);
    Options.instance(context).put("allowStringFolding", "false");
    JavacFileManager fileManager = new JavacFileManager(context, true, UTF_8);
    try {
      fileManager.setLocation(PLATFORM_CLASS_PATH, ImmutableList.of());
    } catch (IOException e) {
      // impossible
      throw new IOError(e);
    }
    SimpleJavaFileObject source =
        new SimpleJavaFileObject(URI.create("source"), JavaFileObject.Kind.SOURCE) {
          @Override
          public CharSequence getCharContent(boolean ignoreEncodingErrors) {
            return input;
          }
        };
    Log.instance(context).useSource(source);
    ParserFactory parserFactory = ParserFactory.instance(context);
    JavacParser parser =
        parserFactory.newParser(
            input, /*keepDocComments=*/ true, /*keepEndPos=*/ true, /*keepLineMap=*/ true);
    JCCompilationUnit unit = parser.parseCompilationUnit();
    unit.sourcefile = source;
    if (diagnostics.getDiagnostics().stream().anyMatch(d -> d.getKind() == ERROR)) {
      throw new AssertionError(diagnostics.getDiagnostics());
    }
    return unit;
  }

  /** Applies the given replacements to the source text. */
  private static String applyFixes(String source, SortedMap<Range<Integer>, String> replacements) {
    if (replacements.isEmpty()) {
      return source;
    }
    StringBuilder sb = new StringBuilder(source);
    int offset = 0;
    for (Entry<Range<Integer>, String> replacement : replacements.entrySet()) {
      Range<Integer> range = replacement.getKey();
      String replaceWith = replacement.getValue();
      int start = offset + range.lowerEndpoint();
      int end = offset + range.upperEndpoint();
      sb.replace(start, end, replaceWith);
      offset += replaceWith.length() - (range.upperEndpoint() - range.lowerEndpoint());
    }
    return sb.toString();
  }

  /** Gets the simple name of a select or identifier tree. */
  private static String getSimpleName(Tree tree) {
    Name name =
        tree.accept(
            new SimpleTreeVisitor<Name, Void>() {
              @Override
              public Name visitMemberSelect(MemberSelectTree node, Void unused) {
                return node.getIdentifier();
              }

              @Override
              public Name visitIdentifier(IdentifierTree node, Void unused) {
                return node.getName();
              }
            },
            null);
    verifyNotNull(name, "name for %s %s", tree.getKind(), tree);
    return name.toString();
  }

  private static final ImmutableSet<String> CF_ANNOTATIONS =
      ImmutableSet.of(
          "A",
          "Acceleration",
          "AlwaysSafe",
          "Angle",
          "AnnotatedFor",
          "Area",
          "ArrayLen",
          "ArrayLenRange",
          "AssertNonNullIfNonNull",
          "AwtAlphaCompositingRule",
          "AwtColorSpace",
          "AwtCursorType",
          "AwtFlowLayout",
          "BinaryName",
          "BinaryNameInUnnamedPackage",
          "BoolVal",
          "Bottom",
          "BottomThis",
          "BottomVal",
          "C",
          "cd",
          "CFComment",
          "CanonicalName",
          "CanonicalNameOrEmpty",
          "ClassBound",
          "ClassGetName",
          "ClassGetSimpleName",
          "ClassVal",
          "ClassValBottom",
          "CompilerMessageKey",
          "CompilerMessageKeyBottom",
          "ConditionalPostconditionAnnotation",
          "ConversionCategory",
          "Covariant",
          "Current",
          "DefaultFor",
          "DefaultQualifier",
          "DefaultQualifierForUse",
          "DefaultQualifierInHierarchy",
          "degrees",
          "Deterministic",
          "DotSeparatedIdentifiers",
          "DoubleVal",
          "EnsuresKeyFor",
          "EnsuresKeyForIf",
          "EnsuresLockHeld",
          "EnsuresLockHeldIf",
          "EnsuresLTLengthOf",
          "EnsuresLTLengthOfIf",
          "EnsuresMinLenIf",
          "EnsuresNonNull",
          "EnsuresNonNullIf",
          "EnsuresQualifier",
          "EnsuresQualifierIf",
          "FBCBottom",
          "Fenum",
          "FenumBottom",
          "FenumTop",
          "FenumUnqualified",
          "FieldDescriptor",
          "FieldDescriptorForPrimitive",
          "FieldDescriptorForPrimitiveOrArrayInUnnamedPackage",
          "FieldInvariant",
          "Format",
          "FormatBottom",
          "FormatMethod",
          "FormatUtil",
          "ForName",
          "FqBinaryName",
          "FromByteCode",
          "FromStubFile",
          "FullyQualifiedName",
          "g",
          "GetClass",
          "GetConstructor",
          "GetMethod",
          "GTENegativeOne",
          "GuardedBy",
          "GuardedByBottom",
          "GuardedByUnknown",
          "GuardSatisfied",
          "h",
          "HasSubsequence",
          "Holding",
          "I18nChecksFormat",
          "I18nConversionCategory",
          "I18nFormat",
          "I18nFormatBottom",
          "I18nFormatFor",
          "I18nFormatUtil",
          "I18nInvalidFormat",
          "I18nMakeFormat",
          "I18nUnknownFormat",
          "I18nValidFormat",
          "Identifier",
          "IdentifierOrArray",
          "IgnoreInWholeProgramInference",
          "IndexFor",
          "IndexOrHigh",
          "IndexOrLow",
          "InheritedAnnotation",
          "Initialized",
          "InternalForm",
          "Interned",
          "InternedDistinct",
          "InternMethod",
          "IntRange",
          "IntRangeFromGTENegativeOne",
          "IntRangeFromNonNegative",
          "IntRangeFromPositive",
          "IntVal",
          "InvalidFormat",
          "InvisibleQualifier",
          "Invoke",
          "JavaExpression",
          "K",
          "KeyFor",
          "KeyForBottom",
          "kg",
          "km",
          "km2",
          "kmPERh",
          "LeakedToResult",
          "Length",
          "LengthOf",
          "LessThan",
          "LessThanBottom",
          "LessThanUnknown",
          "LiteralKind",
          "LocalizableKey",
          "LocalizableKeyBottom",
          "Localized",
          "LockHeld",
          "LockingFree",
          "LockPossiblyHeld",
          "LowerBoundBottom",
          "LowerBoundUnknown",
          "LTEqLengthOf",
          "LTLengthOf",
          "LTOMLengthOf",
          "Luminance",
          "m",
          "m2",
          "Mass",
          "MaybeAliased",
          "MaybeLeaked",
          "MaybePresent",
          "MayReleaseLocks",
          "MethodDescriptor",
          "MethodVal",
          "MethodValBottom",
          "min",
          "MinLen",
          "MinLenFieldInvariant",
          "MixedUnits",
          "mm",
          "mm2",
          "mol",
          "MonotonicNonNull",
          "MonotonicQualifier",
          "mPERs",
          "mPERs2",
          "NegativeIndexFor",
          "NewInstance",
          "NoDefaultQualifierForUse",
          "NonLeaked",
          "NonNegative",
          "NonNull",
          "NotOnlyInitialized",
          "Nullable",
          "NullnessUtil",
          "Opt",
          "PartialRegex",
          "PolyFenum",
          "PolyGuardedBy",
          "PolyIndex",
          "PolyInterned",
          "PolyKeyFor",
          "PolyLength",
          "PolyLowerBound",
          "PolymorphicQualifier",
          "PolyNull",
          "PolyPresent",
          "PolyRegex",
          "PolySameLen",
          "PolySignature",
          "PolySigned",
          "PolyTainted",
          "PolyUI",
          "PolyUIEffect",
          "PolyUIType",
          "PolyUnit",
          "PolyUpperBound",
          "PolyValue",
          "Positive",
          "PostconditionAnnotation",
          "PreconditionAnnotation",
          "Prefix",
          "Present",
          "PropertyKey",
          "PropertyKeyBottom",
          "Pure",
          "PurityUnqualified",
          "QualifierArgument",
          "QualifierForLiterals",
          "radians",
          "Regex",
          "RegexBottom",
          "RegexUtil",
          "ReleasesNoLocks",
          "RelevantJavaTypes",
          "ReportCall",
          "ReportCreation",
          "ReportInherit",
          "ReportOverride",
          "ReportReadWrite",
          "ReportUnqualified",
          "ReportUse",
          "ReportWrite",
          "RequiresNonNull",
          "RequiresQualifier",
          "ReturnsFormat",
          "s",
          "SafeEffect",
          "SafeType",
          "SameLen",
          "SameLenBottom",
          "SameLenUnknown",
          "SearchIndexBottom",
          "SearchIndexFor",
          "SearchIndexUnknown",
          "SideEffectFree",
          "SignatureBottom",
          "SignatureUnknown",
          "Signed",
          "SignednessBottom",
          "SignednessGlb",
          "SignednessUtil",
          "SignedPositive",
          "Speed",
          "StaticallyExecutable",
          "StringVal",
          "StubFiles",
          "Substance",
          "SubstringIndexBottom",
          "SubstringIndexFor",
          "SubstringIndexUnknown",
          "SubtypeOf",
          "SwingBoxOrientation",
          "SwingCompassDirection",
          "SwingElementOrientation",
          "SwingHorizontalOrientation",
          "SwingSplitPaneOrientation",
          "SwingTextOrientation",
          "SwingTitleJustification",
          "SwingTitlePosition",
          "SwingVerticalOrientation",
          "Tainted",
          "TargetLocations",
          "Temperature",
          "TerminatesExecution",
          "This",
          "Time",
          "TypeKind",
          "TypeUseLocation",
          "UI",
          "UIEffect",
          "UIPackage",
          "UIType",
          "UnderInitialization",
          "Unique",
          "UnitsBottom",
          "UnitsMultiple",
          "UnitsRelations",
          "UnitsTools",
          "UnknownClass",
          "UnknownCompilerMessageKey",
          "UnknownFormat",
          "UnknownInitialization",
          "UnknownInterned",
          "UnknownKeyFor",
          "UnknownLocalizableKey",
          "UnknownLocalized",
          "UnknownMethod",
          "UnknownPropertyKey",
          "UnknownRegex",
          "UnknownSignedness",
          "UnknownThis",
          "UnknownUnits",
          "UnknownVal",
          "Unqualified",
          "Unsigned",
          "Untainted",
          "Unused",
          "UpperBoundBottom",
          "UpperBoundFor",
          "UpperBoundUnknown",
          "UsesObjectEquals");

  /*
   * We insert only closedOpen ranges, so we can safely compare only by endpoints, without needing
   * to check for differences in bound type.
   */
  private static final Comparator<Range<Integer>> BY_START_THEN_END =
      comparing((Range<Integer> r) -> r.lowerEndpoint()).thenComparing(Range::upperEndpoint);

  private CheckerFrameworkToJspecifyRefactoring() {}
}
