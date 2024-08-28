package butterknife.compiler;

import com.squareup.javapoet.CodeBlock;
import static butterknife.compiler.BindingSet.CONTEXT_COMPAT;
import static butterknife.compiler.BindingSet.CONTEXT_COMPAT_ANDROIDX;
import static butterknife.compiler.BindingSet.UTILS;
import static butterknife.internal.Constants.NO_RES_ID;

@org.checkerframework.framework.qual.AnnotatedFor("org.checkerframework.checker.nullness.NullnessChecker")
final class FieldDrawableBinding implements ResourceBinding {

    private final @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.MonotonicNonNull Id id;

    private final @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull String name;

    private final @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.MonotonicNonNull Id tintAttributeId;

    private final  @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull boolean androidX;

    @org.checkerframework.dataflow.qual.SideEffectFree
    FieldDrawableBinding(@org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.Nullable Id id, @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull String name, @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.Nullable Id tintAttributeId,  @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull boolean useAndroidX) {
        this.id = id;
        this.name = name;
        this.tintAttributeId = tintAttributeId;
        this.androidX = useAndroidX;
    }

    @org.checkerframework.dataflow.qual.Pure
    public @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.Nullable Id id(@org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull FieldDrawableBinding this) {
        return id;
    }

    @org.checkerframework.dataflow.qual.Pure
    public  @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull boolean requiresResources(@org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull FieldDrawableBinding this,  @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull int sdk) {
        return false;
    }

    @org.checkerframework.dataflow.qual.Impure
    public @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull CodeBlock render(@org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull FieldDrawableBinding this,  @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull int sdk) {
        if (tintAttributeId.value != NO_RES_ID) {
            return CodeBlock.of("target.$L = $T.getTintedDrawable(context, $L, $L)", name, UTILS, id.code, tintAttributeId.code);
        }
        if (sdk >= 21) {
            return CodeBlock.of("target.$L = context.getDrawable($L)", name, id.code);
        }
        return CodeBlock.of("target.$L = $T.getDrawable(context, $L)", name, androidX ? CONTEXT_COMPAT_ANDROIDX : CONTEXT_COMPAT, id.code);
    }
}
