/*
 * Copyright 2018 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.google.cloud.tools.jib.builder;

import com.google.cloud.tools.jib.Timer;
import com.google.cloud.tools.jib.blob.BlobDescriptor;
import com.google.cloud.tools.jib.cache.CachedLayer;
import com.google.cloud.tools.jib.http.Authorization;
import com.google.cloud.tools.jib.image.Image;
import com.google.cloud.tools.jib.image.LayerPropertyNotFoundException;
import com.google.cloud.tools.jib.image.json.BuildableManifestTemplate;
import com.google.cloud.tools.jib.image.json.ImageToJsonTranslator;
import com.google.cloud.tools.jib.registry.RegistryClient;
import com.google.cloud.tools.jib.registry.RegistryException;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Pushes the final image.
 */
@org.checkerframework.framework.qual.AnnotatedFor("org.checkerframework.checker.nullness.NullnessChecker")
class PushImageStep implements Callable<Void> {

    private static final @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull String DESCRIPTION = "Pushing new image";

    private final @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull BuildConfiguration buildConfiguration;

    private final @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull ListeningExecutorService listeningExecutorService;

    private final @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull ListenableFuture<Authorization> pushAuthorizationFuture;

    private final @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull ListenableFuture<List<ListenableFuture<CachedLayer>>> pullBaseImageLayerFuturesFuture;

    private final @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull List<ListenableFuture<CachedLayer>> buildApplicationLayerFutures;

    private final @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull ListenableFuture<List<ListenableFuture<Void>>> pushBaseImageLayerFuturesFuture;

    private final @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull List<ListenableFuture<Void>> pushApplicationLayerFutures;

    private final @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull ListenableFuture<ListenableFuture<BlobDescriptor>> containerConfigurationBlobDescriptorFutureFuture;

    @org.checkerframework.dataflow.qual.SideEffectFree
    PushImageStep(@org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull BuildConfiguration buildConfiguration, @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull ListeningExecutorService listeningExecutorService, @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull ListenableFuture<Authorization> pushAuthorizationFuture, @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull ListenableFuture<List<ListenableFuture<CachedLayer>>> pullBaseImageLayerFuturesFuture, @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull List<ListenableFuture<CachedLayer>> buildApplicationLayerFutures, @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull ListenableFuture<List<ListenableFuture<Void>>> pushBaseImageLayerFuturesFuture, @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull List<ListenableFuture<Void>> pushApplicationLayerFutures, @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull ListenableFuture<ListenableFuture<BlobDescriptor>> containerConfigurationBlobDescriptorFutureFuture) {
        this.buildConfiguration = buildConfiguration;
        this.listeningExecutorService = listeningExecutorService;
        this.pushAuthorizationFuture = pushAuthorizationFuture;
        this.pullBaseImageLayerFuturesFuture = pullBaseImageLayerFuturesFuture;
        this.buildApplicationLayerFutures = buildApplicationLayerFutures;
        this.pushBaseImageLayerFuturesFuture = pushBaseImageLayerFuturesFuture;
        this.pushApplicationLayerFutures = pushApplicationLayerFutures;
        this.containerConfigurationBlobDescriptorFutureFuture = containerConfigurationBlobDescriptorFutureFuture;
    }

    /**
     * Depends on {@code pushBaseImageLayerFuturesFuture} and {@code
     * containerConfigurationBlobDescriptorFutureFuture}.
     */
    @org.checkerframework.dataflow.qual.Impure
    public @org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.Nullable Void call(@org.checkerframework.checker.initialization.qual.Initialized @org.checkerframework.checker.nullness.qual.NonNull PushImageStep this) throws ExecutionException, InterruptedException {
        List<ListenableFuture<?>> dependencies = new ArrayList<>();
        dependencies.add(pushAuthorizationFuture);
        dependencies.addAll(NonBlockingFutures.get(pushBaseImageLayerFuturesFuture));
        dependencies.addAll(pushApplicationLayerFutures);
        dependencies.add(NonBlockingFutures.get(containerConfigurationBlobDescriptorFutureFuture));
        return Futures.whenAllComplete(dependencies).call(this::afterPushBaseImageLayerFuturesFuture, listeningExecutorService).get();
    }

    /**
     * Depends on {@code pushAuthorizationFuture}, {@code pushBaseImageLayerFuturesFuture.get()},
     * {@code pushApplicationLayerFutures}, and (@code
     * containerConfigurationBlobDescriptorFutureFuture.get()}.
     */
    @org.checkerframework.dataflow.qual.Impure
    private @org.checkerframework.checker.initialization.qual.FBCBottom @org.checkerframework.checker.nullness.qual.Nullable Void afterPushBaseImageLayerFuturesFuture() throws IOException, RegistryException, ExecutionException, InterruptedException, LayerPropertyNotFoundException {
        try (Timer ignored = new Timer(buildConfiguration.getBuildLogger(), DESCRIPTION)) {
            RegistryClient registryClient = new RegistryClient(NonBlockingFutures.get(pushAuthorizationFuture), buildConfiguration.getTargetRegistry(), buildConfiguration.getTargetRepository());
            // TODO: Consolidate with BuildAndPushContainerConfigurationStep.
            // Constructs the image.
            Image image = new Image();
            for (Future<CachedLayer> cachedLayerFuture : NonBlockingFutures.get(pullBaseImageLayerFuturesFuture)) {
                image.addLayer(NonBlockingFutures.get(cachedLayerFuture));
            }
            for (Future<CachedLayer> cachedLayerFuture : buildApplicationLayerFutures) {
                image.addLayer(NonBlockingFutures.get(cachedLayerFuture));
            }
            ImageToJsonTranslator imageToJsonTranslator = new ImageToJsonTranslator(image);
            // Pushes the image manifest.
            BuildableManifestTemplate manifestTemplate = imageToJsonTranslator.getManifestTemplate(buildConfiguration.getTargetFormat(), NonBlockingFutures.get(NonBlockingFutures.get(containerConfigurationBlobDescriptorFutureFuture)));
            registryClient.pushManifest(manifestTemplate, buildConfiguration.getTargetTag());
        }
        return null;
    }
}
