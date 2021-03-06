/*
 * Copyright (C) 2010 Mycila (mathieu.carbou@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.opendaylight.odlguice.inject.guice.extensions.injection.tests;

import com.google.common.collect.Iterables;
import java.io.File;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import org.opendaylight.odlguice.inject.guice.extensions.injection.Reflect;

/**
 * This code originated in https://github.com/mycila/guice and was forked into
 * OpenDaylight.
 * @author Mathieu Carbou (mathieu.carbou@gmail.com)
 */
public final class Perf {
    private Perf() {

    }

    public static void main(String[] args) throws Exception {
        perfTestMembers();
    }

    private static void perfTestMembers() throws Exception {
        List<Class<?>> classes = new LinkedList<Class<?>>();
        JarFile jarFile = new JarFile(new File("C:\\Program Files\\Java\\jdk1.7.0_15\\jre\\lib\\rt.jar"));
        Enumeration<JarEntry> enums = jarFile.entries();
        while (enums.hasMoreElements()) {
            JarEntry entry = enums.nextElement();
            if (entry.getName().endsWith(".class")) {
                if (entry.getName().startsWith("javax/swing")
                    || entry.getName().startsWith("java/awt")
                    || entry.getName().startsWith("java/awt")) {
                    classes.add(Class
                            .forName(entry.getName().replace('/', '.').substring(0, entry.getName().length() - 6)));
                }
            }
        }

        // start visual VM
        Thread.sleep(10000);

        for (int i = 0; i < 100; i++) {
            long time = System.nanoTime();
            for (Class<?> c : classes) {
                Iterables.size(Reflect.findAllAnnotatedMethods(c, Deprecated.class));
            }
            long end = System.nanoTime();
        }
    }
}
