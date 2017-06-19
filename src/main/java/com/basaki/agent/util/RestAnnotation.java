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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * {@code RestAnnotation} represents a Java annotation.
 *
 * @author Indra Basak
 * @since 6/15/17
 */
public class RestAnnotation implements IAnnotation {

    private String annotationClass;

    private Map<String, List<IAnnotation>> params =
            new HashMap<String, List<IAnnotation>>();

    public String getAnnotationClass() {
        return annotationClass;
    }

    public void setAnnotationClass(String annotationClass) {
        this.annotationClass = annotationClass;
    }

    public void addParam(String key, IAnnotation value) {
        List<IAnnotation> list = params.get(key);
        if (list == null) {
            list = new ArrayList<IAnnotation>();
            params.put(key, list);
        }

        list.add(value);
    }

    public List<IAnnotation> getParam(String key) {
        try {
            return params.get(key);
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    public String[] getParamKeys() {
        return params.keySet().toArray(new String[0]);
    }

    @Override
    public String toString() {
        return "RestAnnotation{" +
                "annotationClass='" + annotationClass + '\'' +
                ", params=" + params +
                '}';
    }
}
