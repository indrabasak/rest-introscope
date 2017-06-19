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

package com.basaki.agent.util;

/**
 * {@code RequestMappingParams} is an object representation of
 * {@code org.springframework.web.bind.annotation.RequestMapping} annotation.
 * <p/>
 *
 * @author Indra Basak
 * @since Oct, 2014
 */
public class RequestMappingParams {

    private String value;

    private String method;

    /**
     * Retrieves the path mapping URI (e.g. /customers).
     *
     * @return path mapping URI
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets the the path mapping URI
     *
     * @param value the path mapping URI
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Retrieves the HTTP request method (e.g., GET, POST, HEAD, PUT, PATCH,
     * DELETE).
     *
     * @return the HTTP request method
     */
    public String getMethod() {
        return method;
    }

    /**
     * Sets the HTTP request method e.g., GET, POST, HEAD, PUT, PATCH, DELETE).
     *
     * @param method the HTTP request method
     */
    public void setMethod(String method) {
        this.method = method;
    }

    @Override
    public String toString() {
        StringBuilder bldr = new StringBuilder(getClass().getName())
                .append("[");
        bldr.append("\n\tvalue: ").append(value).append("\n\tmethod: ")
                .append(method).append("]");

        return bldr.toString();
    }
}
