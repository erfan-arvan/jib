package butterknife.compiler;

import android.support.annotation.Nullable;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;

@org.checkerframework.framework.qual.AnnotatedFor("org.checkerframework.checker.nullness.NullnessChecker")
final class FieldTypefaceBinding implements ResourceBinding {

    private static final @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull ClassName RESOURCES_COMPAT = ClassName.get("android.support.v4.content.res", "ResourcesCompat");

    private static final @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull ClassName RESOURCES_COMPAT_ANDROIDX = ClassName.get("androidx.core.content.res", "ResourcesCompat");

    private static final @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull ClassName TYPEFACE = ClassName.get("android.graphics", "Typeface");

    /**
     * Keep in sync with {@link android.graphics.Typeface} constants.
     */
    enum TypefaceStyles {

        NORMAL(0), BOLD(1), ITALIC(2), BOLD_ITALIC(3);

        final  @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull int value;

        @org.checkerframework.dataflow.qual.Impure
        TypefaceStyles( @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull int value) {
            this.value = value;
        }

        @org.checkerframework.dataflow.qual.Impure
        static @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.Nullable TypefaceStyles fromValue( @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull int value) {
            for (TypefaceStyles style : values()) {
                if (style.value == value) {
                    return style;
                }
            }
            return null;
        }
    }

    private final @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull Id id;

    private final @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull String name;

    private final @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.MonotonicNonNull TypefaceStyles style;

    private final  @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull boolean useAndroidX;

    @org.checkerframework.dataflow.qual.SideEffectFree
    FieldTypefaceBinding(@org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull Id id, @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull String name, @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.Nullable TypefaceStyles style,  @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull boolean useAndroidX) {
        this.id = id;
        this.name = name;
        this.style = style;
        this.useAndroidX = useAndroidX;
    }

    @org.checkerframework.dataflow.qual.Pure
    public @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull Id id(@org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull FieldTypefaceBinding this) {
        return id;
    }

    @org.checkerframework.dataflow.qual.Pure
    public  @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull boolean requiresResources(@org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull FieldTypefaceBinding this,  @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull int sdk) {
        return sdk >= 26;
    }

    @org.checkerframework.dataflow.qual.Impure
    public @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull CodeBlock render(@org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull FieldTypefaceBinding this,  @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull int sdk) {
        CodeBlock typeface = sdk >= 26 ? CodeBlock.of("res.getFont($L)", id.code) : CodeBlock.of("$T.getFont(context, $L)", useAndroidX ? RESOURCES_COMPAT_ANDROIDX : RESOURCES_COMPAT, id.code);
        if (style != TypefaceStyles.NORMAL) {
            typeface = CodeBlock.of("$1T.create($2L, $1T.$3L)", TYPEFACE, typeface, style);
        }
        return CodeBlock.of("target.$L = $L", name, typeface);
    }
}
