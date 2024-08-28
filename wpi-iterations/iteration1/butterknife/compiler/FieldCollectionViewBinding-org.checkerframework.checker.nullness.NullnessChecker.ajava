package butterknife.compiler;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import java.util.List;
import static butterknife.compiler.BindingSet.UTILS;
import static butterknife.compiler.BindingSet.requiresCast;

@org.checkerframework.framework.qual.AnnotatedFor("org.checkerframework.checker.nullness.NullnessChecker")
final class FieldCollectionViewBinding {

    enum Kind {

        ARRAY("arrayFilteringNull"), LIST("listFilteringNull");

        final @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull String factoryName;

        @org.checkerframework.dataflow.qual.Impure
        Kind(@org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull String factoryName) {
            this.factoryName = factoryName;
        }
    }

    final @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull String name;

    private final @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull TypeName type;

    private final @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull Kind kind;

    private final  @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull boolean required;

    private final @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull List<Id> ids;

    @org.checkerframework.dataflow.qual.SideEffectFree
    FieldCollectionViewBinding(@org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull String name, @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull TypeName type, @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull Kind kind, @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull List<Id> ids,  @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull boolean required) {
        this.name = name;
        this.type = type;
        this.kind = kind;
        this.ids = ids;
        this.required = required;
    }

    @org.checkerframework.dataflow.qual.Impure
    @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull CodeBlock render( @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull boolean debuggable) {
        CodeBlock.Builder builder = CodeBlock.builder().add("target.$L = $T.$L(", name, UTILS, kind.factoryName);
        for (int i = 0; i < ids.size(); i++) {
            if (i > 0) {
                builder.add(", ");
            }
            builder.add("\n");
            Id id = ids.get(i);
            boolean requiresCast = requiresCast(type);
            if (!debuggable) {
                if (requiresCast) {
                    builder.add("($T) ", type);
                }
                builder.add("source.findViewById($L)", id.code);
            } else if (!requiresCast && !required) {
                builder.add("source.findViewById($L)", id.code);
            } else {
                builder.add("$T.find", UTILS);
                builder.add(required ? "RequiredView" : "OptionalView");
                if (requiresCast) {
                    builder.add("AsType");
                }
                builder.add("(source, $L, \"field '$L'\"", id.code, name);
                if (requiresCast) {
                    TypeName rawType = type;
                    if (rawType instanceof ParameterizedTypeName) {
                        rawType = ((ParameterizedTypeName) rawType).rawType;
                    }
                    builder.add(", $T.class", rawType);
                }
                builder.add(")");
            }
        }
        return builder.add(")").build();
    }
}
