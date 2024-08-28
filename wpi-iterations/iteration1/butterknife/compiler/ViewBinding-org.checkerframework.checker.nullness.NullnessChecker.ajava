package butterknife.compiler;

import butterknife.internal.ListenerClass;
import butterknife.internal.ListenerMethod;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

@org.checkerframework.framework.qual.AnnotatedFor("org.checkerframework.checker.nullness.NullnessChecker")
final class ViewBinding {

    private final @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull Id id;

    private final @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull Map<ListenerClass, Map<ListenerMethod, Set<MethodViewBinding>>> methodBindings;

    private final @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.MonotonicNonNull FieldViewBinding fieldBinding;

    @org.checkerframework.dataflow.qual.SideEffectFree
    ViewBinding(@org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull Id id, @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull Map<ListenerClass, Map<ListenerMethod, Set<MethodViewBinding>>> methodBindings, @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.Nullable FieldViewBinding fieldBinding) {
        this.id = id;
        this.methodBindings = methodBindings;
        this.fieldBinding = fieldBinding;
    }

    @org.checkerframework.dataflow.qual.Pure
    public @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull Id getId() {
        return id;
    }

    @org.checkerframework.dataflow.qual.Pure
    public @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.Nullable FieldViewBinding getFieldBinding() {
        return fieldBinding;
    }

    @org.checkerframework.dataflow.qual.Pure
    public @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull Map<ListenerClass, Map<ListenerMethod, Set<MethodViewBinding>>> getMethodBindings() {
        return methodBindings;
    }

    @org.checkerframework.dataflow.qual.Impure
    public @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull List<MemberViewBinding> getRequiredBindings() {
        List<MemberViewBinding> requiredBindings = new ArrayList<>();
        if (fieldBinding != null && fieldBinding.isRequired()) {
            requiredBindings.add(fieldBinding);
        }
        for (Map<ListenerMethod, Set<MethodViewBinding>> methodBinding : methodBindings.values()) {
            for (Set<MethodViewBinding> set : methodBinding.values()) {
                for (MethodViewBinding binding : set) {
                    if (binding.isRequired()) {
                        requiredBindings.add(binding);
                    }
                }
            }
        }
        return requiredBindings;
    }

    @org.checkerframework.dataflow.qual.Pure
    public  @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull boolean isSingleFieldBinding() {
        return methodBindings.isEmpty() && fieldBinding != null;
    }

    @org.checkerframework.dataflow.qual.Pure
    public  @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull boolean requiresLocal() {
        if (isBoundToRoot()) {
            return false;
        }
        if (isSingleFieldBinding()) {
            return false;
        }
        return true;
    }

    @org.checkerframework.dataflow.qual.Pure
    public  @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull boolean isBoundToRoot() {
        return ButterKnifeProcessor.NO_ID.equals(id);
    }

    public static final class Builder {

        private final @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull Id id;

        private final @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull Map<ListenerClass, Map<ListenerMethod, Set<MethodViewBinding>>> methodBindings = new LinkedHashMap<>();

        @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.MonotonicNonNull FieldViewBinding fieldBinding;

        @org.checkerframework.dataflow.qual.SideEffectFree
        Builder(@org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull Id id) {
            this.id = id;
        }

        @org.checkerframework.dataflow.qual.Pure
        public  @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull boolean hasMethodBinding(@org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull ListenerClass listener, @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull ListenerMethod method) {
            Map<ListenerMethod, Set<MethodViewBinding>> methods = methodBindings.get(listener);
            return methods != null && methods.containsKey(method);
        }

        @org.checkerframework.dataflow.qual.Impure
        public void addMethodBinding(@org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull ListenerClass listener, @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull ListenerMethod method, @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull MethodViewBinding binding) {
            Map<ListenerMethod, Set<MethodViewBinding>> methods = methodBindings.get(listener);
            Set<MethodViewBinding> set = null;
            if (methods == null) {
                methods = new LinkedHashMap<>();
                methodBindings.put(listener, methods);
            } else {
                set = methods.get(method);
            }
            if (set == null) {
                set = new LinkedHashSet<>();
                methods.put(method, set);
            }
            set.add(binding);
        }

        @org.checkerframework.dataflow.qual.Impure
        public void setFieldBinding(@org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull FieldViewBinding fieldBinding) {
            if (this.fieldBinding != null) {
                throw new AssertionError();
            }
            this.fieldBinding = fieldBinding;
        }

        @org.checkerframework.dataflow.qual.Pure
        public @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull ViewBinding build() {
            return new ViewBinding(id, methodBindings, fieldBinding);
        }
    }
}
