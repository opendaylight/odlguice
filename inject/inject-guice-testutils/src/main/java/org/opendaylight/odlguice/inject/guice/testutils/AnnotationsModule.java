/*
 * Copyright (c) 2017 Red Hat, Inc. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.odlguice.inject.guice.testutils;

import com.google.inject.AbstractModule;
import org.opendaylight.infrautils.inject.guice.extensions.closeable.CloseableModule;
import org.opendaylight.infrautils.inject.guice.extensions.jsr250.Jsr250Module;

/**
 * Guice module with built-in Mycila Guice Extensions for JSR-250 &amp;
 * Closeable support for {@literal @}PreDestroy &amp; {@literal @}PostConstruct.
 *
 * @author Michael Vorburger.ch
 */
public class AnnotationsModule extends AbstractModule {

    @Override
    protected void configure() {
        install(new CloseableModule());
        install(new Jsr250Module());
    }

}
