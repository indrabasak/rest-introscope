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

import com.basaki.agent.util.RequestMappingParams;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * {@code RequestMappingParamsTest} is an unit test for {@link
 * RequestMappingParams}.
 * <p/>
 *
 * @author Indra Basak
 * @since Oct, 2014
 */
public class RequestMappingParamsTest {

    @Test
    public void testValue() {
        RequestMappingParams params = new RequestMappingParams();
        params.setValue("/customer");
        assertEquals("/customer", params.getValue());
    }

    @Test
    public void testMethod() {
        RequestMappingParams params = new RequestMappingParams();
        params.setMethod("POST");
        assertEquals("POST", params.getMethod());
    }
}
