/*
 * Copyright [2017] [Indra Basak]
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.basaki.agent.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * {@code MethodUtil} contains utility methods for finding a class method and
 * annotations.
 * <p/>
 *
 * @author Indra Basak
 * @since Oct, 2014
 */
@SuppressWarnings("squid:CommentedOutCodeLine")
public class MethodUtil {

    /**
     * Finds a {@link Method} object based on class, method name, and a
     * method descriptor.
     *
     * @param clazz      class of the method to be retrieved
     * @param methodName method name
     * @param methodDesc method descriptor
     * @return {@link Method} object if the method is found, null otherwise
     */
    public static Method findMethod(Class<?> clazz, String methodName,
            String methodDesc) {
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.getName().equals(methodName)) {
                String desc = getMethodDescriptor(method.getReturnType(),
                        method.getParameterTypes());

                if (desc.equals(methodDesc)) {
                    return method;
                }
            }
        }
        return null;
    }

    /**
     * Retrieves the annotation string associated with a method based on the
     * annotation class
     *
     * @param method          class method
     * @param annotationClazz annotation class in string form
     * @return annotation string specified with the method
     */
    public static String getAnnotation(Method method, String annotationClazz) {
        Annotation[] annotations = method.getAnnotations();

        for (Annotation anno : annotations) {
            String annoStr = anno.toString();
            if (annoStr.startsWith("@" + annotationClazz)) {
                return annoStr;
            }
        }
        return null;
    }

    /**
     * Creates a method descriptor based on method return type and parameter
     * types.
     *
     * @param returnType    method return class
     * @param argumentTypes an array of method parameter type classes in the
     *                      right order
     * @return method descriptor
     */
    private static String getMethodDescriptor(final Class<?> returnType,
            final Class<?>[] argumentTypes) {
        StringBuilder buf = new StringBuilder();
        buf.append('(');
        for (int i = 0; i < argumentTypes.length; ++i) {
            getDescriptor(buf, argumentTypes[i]);
        }
        buf.append(')');
        getDescriptor(buf, returnType);
        return buf.toString();
    }

    /**
     * Appends to an existing descriptor based on class type
     *
     * @param buf string buffer containing method descriptor
     * @param c   the class to be added to the descriptor
     */
    private static void getDescriptor(final StringBuilder buf,
            final Class<?> c) {
        Class<?> d = c;
        while (true) {
            if (d.isPrimitive()) {
                buf.append(getPrimitiveType(d));
                return;
            } else if (d.isArray()) {
                buf.append('[');
                d = d.getComponentType();
            } else {
                buf.append('L');
                String name = d.getName();
                int len = name.length();
                for (int i = 0; i < len; ++i) {
                    char car = name.charAt(i);
                    buf.append(car == '.' ? '/' : car);
                }
                buf.append(';');
                return;
            }
        }
    }

    private static char getPrimitiveType(Class<?> d) {
        char ch;
        if (d == Integer.TYPE) {
            ch = 'I';
        } else if (d == Void.TYPE) {
            ch = 'V';
        } else if (d == Boolean.TYPE) {
            ch = 'Z';
        } else if (d == Byte.TYPE) {
            ch = 'B';
        } else if (d == Character.TYPE) {
            ch = 'C';
        } else if (d == Short.TYPE) {
            ch = 'S';
        } else if (d == Double.TYPE) {
            ch = 'D';
        } else if (d == Float.TYPE) {
            ch = 'F';
        } else {
            //if (d == Long.TYPE)
            ch = 'J';
        }

        return ch;
    }

    public static void main(String[] args) {
        MethodUtil.findMethod(String.class, "substring", null);
    }
}
