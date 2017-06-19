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

import com.basaki.agent.util.IAnnotation;
import com.basaki.agent.util.ParserHelper;
import com.basaki.agent.util.RequestMappingParams;
import com.basaki.agent.util.RestAnnotation;
import com.basaki.agent.util.RestAnnotationParam;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * {@code ParserHelperTest} is an unit test for {@link ParserHelper}.
 * <p/>
 *
 * @author Indra Basak
 * @since Oct, 2014
 */
public class ParserHelperTest {

    @Test
    public void testFindClassAnnotation() {
        String annoStr = ParserHelper.findClassAnnotation(
                AnnotationTestClass.class, "org.junit.runner.RunWith");
        assertNotNull(annoStr);
        System.out.println(annoStr);
    }

    @Test
    public void testFindMethodAnnotation() {
        String annoStr = ParserHelper.findMethodAnnotation(
                AnnotationTestClass.class, "doSomething", "()V",
                "org.junit.Test");
        assertNotNull(annoStr);
        System.out.println(annoStr);
    }

    @Test
    public void testGetFrontendAppName() {
        String appName = ParserHelper
                .getFrontendAppName("Frontends|Apps|myapp|hello");
        assertNotNull(appName);
        assertEquals("myapp", appName);
    }

    @Test
    public void testValue() {
        RequestMappingParams params = ParserHelper
                .parseRequestMapping(
                        "@org.springframework.web.bind.annotation.RequestMapping(headers=[], value=[/customers], produces=[], method=[], params=[], consumes=[])");
        assertNotNull(params);
        assertEquals("/customers", params.getValue());
    }

    @Test
    public void testMethod() {
        RequestMappingParams params = ParserHelper
                .parseRequestMapping(
                        "@org.springframework.web.bind.annotation.RequestMapping(headers=[], value=[], produces=[application/xml, application/json], method=[GET], params=[], consumes=[])");
        assertNotNull(params);
        assertEquals("GET", params.getMethod());
        System.out.println(params);
    }

    @Test
    public void testNullString() {
        RequestMappingParams params = ParserHelper.parseRequestMapping(null);
        assertNull(params);
    }

    @Test
    public void testParseAnnotation() {
        RestAnnotation anno = ParserHelper.parseAnnotation(
                "@org.springframework.web.bind.annotation.RequestMapping(headers=[], value=[], produces=[application/xml, application/json], method=[GET], params=[], consumes=[])");
        assertNotNull(anno);
        assertEquals("org.springframework.web.bind.annotation.RequestMapping",
                anno.getAnnotationClass());
        assertEquals(2, anno.getParamKeys().length);
        List<IAnnotation> params = anno.getParam("method");
        assertEquals(1, params.size());
        assertEquals("GET", ((RestAnnotationParam) params.get(0)).getValue());
        params = anno.getParam("produces");
        assertEquals(2, params.size());
    }

    @RunWith(value = Parameterized.class)
    private class AnnotationTestClass {
        @Test
        public void doSomething() {

        }
    }
}
