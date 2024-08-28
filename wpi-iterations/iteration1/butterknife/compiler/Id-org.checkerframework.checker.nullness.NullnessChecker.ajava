package butterknife.compiler;

import android.support.annotation.Nullable;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.sun.tools.javac.code.Symbol;

/**
 * Represents an ID of an Android resource.
 */
@org.checkerframework.framework.qual.AnnotatedFor("org.checkerframework.checker.nullness.NullnessChecker")
final class Id {

    private static final @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull ClassName ANDROID_R = ClassName.get("android", "R");

    private static final @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull String R = "R";

    final  @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull int value;

    final @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull CodeBlock code;

    final  @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull boolean qualifed;

    @org.checkerframework.dataflow.qual.Impure
    Id( @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull int value) {
        this(value, null);
    }

    @org.checkerframework.dataflow.qual.Impure
    Id( @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull int value, @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.Nullable Symbol rSymbol) {
        this.value = value;
        if (rSymbol != null) {
            ClassName className = ClassName.get(rSymbol.packge().getQualifiedName().toString(), R, rSymbol.enclClass().name.toString());
            String resourceName = rSymbol.name.toString();
            this.code = className.topLevelClassName().equals(ANDROID_R) ? CodeBlock.of("$L.$N", className, resourceName) : CodeBlock.of("$T.$N", className, resourceName);
            this.qualifed = true;
        } else {
            this.code = CodeBlock.of("$L", value);
            this.qualifed = false;
        }
    }

    @org.checkerframework.dataflow.qual.Pure
    public  @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull boolean equals(@org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull Id this, @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.Nullable Object o) {
        return o instanceof Id && value == ((Id) o).value;
    }

    @org.checkerframework.dataflow.qual.Pure
    public  @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull int hashCode(@org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull Id this) {
        return value;
    }

    @org.checkerframework.dataflow.qual.Pure
    public String toString(@org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull Id this) {
        throw new UnsupportedOperationException("Please use value or code explicitly");
    }
}
