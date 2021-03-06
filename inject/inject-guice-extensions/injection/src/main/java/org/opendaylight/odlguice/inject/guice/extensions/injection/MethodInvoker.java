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
package org.opendaylight.odlguice.inject.guice.extensions.injection;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.inject.internal.BytecodeGen;
import com.google.inject.internal.cglib.core.$CodeGenerationException;
import com.google.inject.internal.cglib.reflect.$FastMethod;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.concurrent.ExecutionException;

/**
 * This code originated in https://github.com/mycila/guice and was forked into
 * OpenDaylight.
 * @author Mathieu Carbou (mathieu.carbou@gmail.com)
 */
@SuppressWarnings("checkstyle:finalClass")
public class MethodInvoker implements Member, AnnotatedElement {

    private static final LoadingCache<Method, MethodInvoker> INVOKERS = CacheBuilder.newBuilder().weakKeys()
            .build(new CacheLoader<Method, MethodInvoker>() {
                @SuppressWarnings("unchecked")
                @Override
                public MethodInvoker load(final Method method) throws Exception {
                    int modifiers = method.getModifiers();
                    if (!Modifier.isPrivate(modifiers) && !Modifier.isProtected(modifiers)) {
                        try {
                            final $FastMethod fastMethod = BytecodeGen
                                    .newFastClassForMember(method.getDeclaringClass(), method).getMethod(method);
                            return new MethodInvoker(method) {
                                @Override
                                public Object invoke(Object target, Object... parameters) {
                                    try {
                                        return fastMethod.invoke(target, parameters);
                                    } catch (InvocationTargetException e) {
                                        throw MycilaGuiceException.toRuntime(e);
                                    }
                                }
                            };
                        } catch ($CodeGenerationException e) {
                            /* fall-through */
                        }
                    }
                    if (!Modifier.isPublic(modifiers)
                            || !Modifier.isPublic(method.getDeclaringClass().getModifiers())) {
//                        method.setAccessible(true);
                        AccessController.doPrivileged((PrivilegedAction) () -> {
                            method.setAccessible(true);
                            return null;
                        });
                    }
                    return new MethodInvoker(method);
                }
            });

    public final Method method;

    private MethodInvoker(Method method) {
        this.method = method;
    }

    public Object invoke(Object target, Object... parameters) {
        try {
            return method.invoke(target, parameters);
        } catch (IllegalAccessException e) {
            throw MycilaGuiceException.toRuntime(e);
        } catch (InvocationTargetException e) {
            throw MycilaGuiceException.toRuntime(e);
        }
    }

    public static MethodInvoker on(Method method) {
        try {
            return INVOKERS.get(method);
        } catch (ExecutionException e) {
            throw MycilaGuiceException.toRuntime(e);
        }
    }

    @Override
    public boolean isAnnotationPresent(Class<? extends Annotation> annotationClass) {
        return Reflect.isAnnotationPresent(method, annotationClass);
    }

    @Override
    public Annotation[] getAnnotations() {
        return method.getAnnotations();
    }

    @Override
    public int getModifiers() {
        return method.getModifiers();
    }

    @Override
    public String getName() {
        return method.getName();
    }

    @Override
    public Class<?> getDeclaringClass() {
        return method.getDeclaringClass();
    }

    @Override
    public Annotation[] getDeclaredAnnotations() {
        return method.getDeclaredAnnotations();
    }

    @Override
    public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
        return method.getAnnotation(annotationClass);
    }

    @Override
    public boolean isSynthetic() {
        return method.isSynthetic();
    }

    @Override
    public String toString() {
        return method.toString();
    }
}
