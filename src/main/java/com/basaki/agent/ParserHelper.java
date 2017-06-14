/**
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
 **/

package com.basaki.agent;

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
@SuppressWarnings("squid:S106")
public class ParserHelper {

    /**
     * @param clazz
     * @param annoClazz
     * @return
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
     * @param clazz
     * @param methodName
     * @param methodDesc
     * @param annoClazz
     * @return
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
            param = param.substring(0, end);
            if (param != null) {
                param = param.trim();
                if (param.length() == 0) {
                    param = null;
                }
            }
            returnVal = param;
        }

        return returnVal;
    }

    public static void main(String[] args) {
        RequestMappingParams params = ParserHelper
                .parseRequestMapping(
                        "@org.springframework.web.bind.annotation.RequestMapping(headers=[], value=[/customers], produces=[], method=[], params=[], consumes=[])");

        if (params != null) {
            System.out.println(params.getValue());
        } else {
            System.out.println("null params");
        }

        params = ParserHelper
                .parseRequestMapping(
                        "@org.springframework.web.bind.annotation.RequestMapping(headers=[], value=[], produces=[application/xml, application/json], method=[GET], params=[], consumes=[])");

        if (params != null) {
            System.out.println(params.getMethod());
        } else {
            System.out.println("null params");
        }
    }
}
