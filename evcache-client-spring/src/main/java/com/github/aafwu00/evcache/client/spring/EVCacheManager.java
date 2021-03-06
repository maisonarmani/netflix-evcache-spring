/*
 * Copyright 2017-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.aafwu00.evcache.client.spring;

import com.netflix.evcache.EVCache.Builder;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.support.AbstractCacheManager;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;
import static java.util.Objects.requireNonNull;

/**
 * {@link CacheManager} backed by an {@link EVCacheImpl}.
 *
 * @author Taeho Kim
 */
public class EVCacheManager extends AbstractCacheManager {
    private final Set<EVCacheConfiguration> configurations;
    private final List<Builder.Customizer> customizers;
    /**
     * Whether to allow for {@code null} values
     */
    private boolean allowNullValues = true;

    public EVCacheManager(final Set<EVCacheConfiguration> configurations) {
        this(configurations, emptyList());
    }

    /**
     * Create a new EVCacheManager for the given configurations and customizers
     *
     * @param configurations To be applied by for {@link Builder}`s {@link com.netflix.evcache.EVCacheClientPoolConfigurationProperties}
     * @param customizers    To be applied by for {@link Builder}`s customizer
     */
    public EVCacheManager(final Set<EVCacheConfiguration> configurations,
                          final List<Builder.Customizer> customizers) {
        super();
        this.configurations = requireNonNull(configurations);
        this.customizers = requireNonNull(customizers);
    }

    @Override
    protected Collection<? extends Cache> loadCaches() {
        return configurations.stream()
                             .map(this::create)
                             .collect(Collectors.toList());
    }

    private EVCache create(final EVCacheConfiguration configuration) {
        return new EVCacheImpl(configuration.getName(), build(configuration), allowNullValues);
    }

    private com.netflix.evcache.EVCache build(final EVCacheConfiguration configuration) {
        return Builder.forApp(configuration.getAppName())
                      .withConfigurationProperties(configuration.getProperties())
                      .addCustomizers(customizers)
                      .build();
    }

    public void setAllowNullValues(final boolean allowNullValues) {
        this.allowNullValues = allowNullValues;
    }
}
