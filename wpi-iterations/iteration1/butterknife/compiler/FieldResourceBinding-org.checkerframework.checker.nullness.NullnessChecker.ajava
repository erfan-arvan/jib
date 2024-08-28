package butterknife.compiler;

import android.support.annotation.Nullable;
import com.google.common.collect.ImmutableList;
import com.google.errorprone.annotations.Immutable;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@org.checkerframework.framework.qual.AnnotatedFor("org.checkerframework.checker.nullness.NullnessChecker")
final class FieldResourceBinding implements ResourceBinding {

    enum Type {

        BITMAP(new ResourceMethod(BindingSet.BITMAP_FACTORY, "decodeResource", true, 1)),
        BOOL("getBoolean"),
        COLOR(new ResourceMethod(BindingSet.CONTEXT_COMPAT, "getColor", false, 1), new ResourceMethod(null, "getColor", false, 23)),
        COLOR_ANDROIDX(new ResourceMethod(BindingSet.CONTEXT_COMPAT_ANDROIDX, "getColor", false, 1), new ResourceMethod(null, "getColor", false, 23)),
        COLOR_STATE_LIST(new ResourceMethod(BindingSet.CONTEXT_COMPAT, "getColorStateList", false, 1), new ResourceMethod(null, "getColorStateList", false, 23)),
        COLOR_STATE_LIST_ANDROIDX(new ResourceMethod(BindingSet.CONTEXT_COMPAT_ANDROIDX, "getColorStateList", false, 1), new ResourceMethod(null, "getColorStateList", false, 23)),
        DIMEN_AS_INT("getDimensionPixelSize"),
        DIMEN_AS_FLOAT("getDimension"),
        FLOAT(new ResourceMethod(BindingSet.UTILS, "getFloat", false, 1)),
        INT("getInteger"),
        INT_ARRAY("getIntArray"),
        STRING("getString"),
        STRING_ARRAY("getStringArray"),
        TEXT_ARRAY("getTextArray"),
        TYPED_ARRAY("obtainTypedArray");

        private final @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull ImmutableList<ResourceMethod> methods;

        @org.checkerframework.dataflow.qual.Impure
        Type(@org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull ResourceMethod@org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull ... methods) {
            List<ResourceMethod> methodList = new ArrayList<>(methods.length);
            Collections.addAll(methodList, methods);
            Collections.sort(methodList);
            Collections.reverse(methodList);
            this.methods = ImmutableList.copyOf(methodList);
        }

        @org.checkerframework.dataflow.qual.Impure
        Type(@org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull String methodName) {
            methods = ImmutableList.of(new ResourceMethod(null, methodName, true, 1));
        }

        @org.checkerframework.dataflow.qual.Pure
        @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull ResourceMethod methodForSdk( @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull int sdk) {
            for (ResourceMethod method : methods) {
                if (method.sdk <= sdk) {
                    return method;
                }
            }
            throw new AssertionError();
        }
    }

    static final class ResourceMethod implements Comparable<ResourceMethod> {

        final @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.MonotonicNonNull ClassName typeName;

        final @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull String name;

        final  @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull boolean requiresResources;

        final  @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull int sdk;

        @org.checkerframework.dataflow.qual.SideEffectFree
        ResourceMethod(@org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.Nullable ClassName typeName, @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull String name,  @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull boolean requiresResources,  @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull int sdk) {
            this.typeName = typeName;
            this.name = name;
            this.requiresResources = requiresResources;
            this.sdk = sdk;
        }

        @org.checkerframework.dataflow.qual.Pure
        public  @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull int compareTo(@org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull ResourceMethod this, @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull ResourceMethod other) {
            return Integer.compare(sdk, other.sdk);
        }
    }

    private final @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull Id id;

    private final @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull String name;

    private final @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull Type type;

    @org.checkerframework.dataflow.qual.SideEffectFree
    FieldResourceBinding(@org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull Id id, @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull String name, @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull Type type) {
        this.id = id;
        this.name = name;
        this.type = type;
    }

    @org.checkerframework.dataflow.qual.Pure
    public @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull Id id(@org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull FieldResourceBinding this) {
        return id;
    }

    @org.checkerframework.dataflow.qual.Pure
    public  @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull boolean requiresResources(@org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull FieldResourceBinding this,  @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull int sdk) {
        return type.methodForSdk(sdk).requiresResources;
    }

    @org.checkerframework.dataflow.qual.Impure
    public @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull CodeBlock render(@org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull FieldResourceBinding this,  @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull int sdk) {
        ResourceMethod method = type.methodForSdk(sdk);
        if (method.typeName == null) {
            if (method.requiresResources) {
                return CodeBlock.of("target.$L = res.$L($L)", name, method.name, id.code);
            }
            return CodeBlock.of("target.$L = context.$L($L)", name, method.name, id.code);
        }
        if (method.requiresResources) {
            return CodeBlock.of("target.$L = $T.$L(res, $L)", name, method.typeName, method.name, id.code);
        }
        return CodeBlock.of("target.$L = $T.$L(context, $L)", name, method.typeName, method.name, id.code);
    }
}
