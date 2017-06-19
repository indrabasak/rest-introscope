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
 * {@code RequestMappingParser} parses a
 * {@code org.springframework.web.bind.annotation.RequestMapping}
 * annotation and converts in into a {@link RequestMappingParams} object.
 * <p/>
 *
 * @author Indra Basak
 * @since Oct, 2014
 */
@SuppressWarnings({"squid:S106"})
public class ParserHelper {

    private ParserHelper() {

    }

    /**
     * Retrieves the annotation string of the a method annotation.
     *
     * @param clazz     name of the class which has the annotation
     * @param annoClazz annotation class
     * @return string representation of the annotation specified in the
     * parameter
     */
    public static String findClassAnnotation(Class<?> clazz, String annoClazz) {
        String matcher = annoClazz;
        if (!annoClazz.startsWith("@")) {
            matcher = "@" + annoClazz;
        }

        Annotation[] annotations = clazz.getAnnotations();
        for (Annotation anno : annotations) {
            String stringRep = anno.toString();
            if (stringRep.startsWith(matcher)) {

                return stringRep;
            }
        }

        return null;
    }

    /**
     * Retrieves the annotation string of the a method annotation.
     *
     * @param clazz      name of the class which contains the method
     * @param methodName name of the method which has the annotation
     * @param methodDesc method descriptor
     * @param annoClazz  annotation class
     * @return string representation of the annotation specified in the
     * parameter
     */
    public static String findMethodAnnotation(Class<?> clazz, String methodName,
            String methodDesc, String annoClazz) {
        String annoStr = null;

        Method method = MethodUtil.findMethod(clazz, methodName, methodDesc);
        if (method != null) {
            annoStr = MethodUtil.getAnnotation(method, annoClazz);
        }

        return annoStr;
    }

    /**
     * Retrieves front end application name from the front end boundary
     *
     * @param frontBoundary front end boundary path
     * @return front end application name
     */
    public static String getFrontendAppName(String frontBoundary) {
        String app = null;

        if (frontBoundary != null
                && frontBoundary.startsWith("Frontends|Apps|")) {
            String[] tokens = frontBoundary.split("\\|");
            if (tokens.length > 2) {
                app = tokens[2];
            }
        }
        return app;
    }

    /**
     * Parses a <code>RequestMapping</code> string representation and converts
     * it into <code>RequestMappingParams</code> object.
     *
     * @param reqMapping string representation of request mapping annotation
     * @return object representation of request mapping annotation
     */
    public static RequestMappingParams parseRequestMapping(String reqMapping) {
        RequestMappingParams params = null;
        // @org.springframework.web.bind.annotation.RequestMapping(headers=[],
        // value=[/customers], produces=[], method=[], params=[], consumes=[])
        if (reqMapping != null
                && reqMapping
                .startsWith(
                        "@org.springframework.web.bind.annotation.RequestMapping")) {
            params = new RequestMappingParams();
            params.setValue(parse(reqMapping, "value=["));
            String value = parse(reqMapping, "method=[");
            if (value != null) {
                params.setMethod(value.toUpperCase());
            }
        }

        return params;
    }

    public static RestAnnotation parseAnnotation(String annoStr) {
        RestAnnotation anno = null;

        if (annoStr != null && annoStr.startsWith("@")) {
            anno = new RestAnnotation();

            char[] chars = annoStr.toCharArray();
            int startIndex = 1;
            int currentIndex = parseAnnotationName(chars, 1, anno);
            if (startIndex == currentIndex) {
                return null;
            }
            parseParams(chars, currentIndex, anno);
        }

        return anno;
    }

    private static int parseAnnotationName(char[] chars, int beginIndex,
            RestAnnotation anno) {
        int returnIndex = beginIndex;
        StringBuilder bldr = new StringBuilder();
        for (int i = beginIndex; i < chars.length; i++) {
            if (chars[i] != '(') {
                bldr.append(chars[i]);
            } else {
                String annotionClass = bldr.toString();
                anno.setAnnotationClass(annotionClass);
                returnIndex = i;
                break;
            }
        }

        return returnIndex;
    }

    private static int parseParams(char[] chars, int beginIndex,
            RestAnnotation anno) {
        int returnIndex = beginIndex;
        StringBuilder bldr = new StringBuilder();
        String key = null;
        String value;
        boolean done = false;
        for (int i = beginIndex; i < chars.length; i++) {
            returnIndex = i;
            if (done) {
                break;
            }

            switch (chars[i]) {
                case '(':
                case '[':
                    //do nothing
                    break;
                case '=':
                    key = bldr.toString().trim();
                    bldr.setLength(0);
                    break;
                case ']':
                    value = bldr.toString().trim();
                    addParamValue(key, value, anno);
                    key = null;
                    bldr.setLength(0);
                    break;
                case ',':
                    value = bldr.toString().trim();
                    if (!value.isEmpty() && key != null) {
                        RestAnnotationParam param = new RestAnnotationParam();
                        param.setValue(value);
                        anno.addParam(key, param);
                    }
                    bldr.setLength(0);
                    break;
                case ')':
                    value = bldr.toString().trim();
                    addParamValue(key, value, anno);
                    done = true;
                    break;
                default:
                    bldr.append(chars[i]);
                    break;
            }
        }

        return returnIndex;
    }

    private static void addParamValue(String key, String value,
            RestAnnotation anno) {
        if (key != null && !value.isEmpty()) {
            RestAnnotationParam param = new RestAnnotationParam();
            param.setValue(value);
            anno.addParam(key, param);
        }
    }

    /**
     * Parses a request mapping annotation string and retrieves a parameter
     * value for an annotation parameter specified as a parameter.
     *
     * @param reqMapping  request mapping annotation string
     * @param paramPrefix annotation parameter prefix
     * @return parameter value
     */
    private static String parse(String reqMapping, String paramPrefix) {
        String returnVal = null;
        int start = reqMapping.indexOf(paramPrefix);
        if (start > -1) {
            start += paramPrefix.length();
            String param = reqMapping.substring(start);

            int end = param.indexOf(']');
            if (end == -1) {
                end = param.indexOf(')');
            }

            param = param.substring(0, end);
            if (param.isEmpty()) {
                param = null;
            }

            returnVal = param;
        }

        return returnVal;
    }
}
