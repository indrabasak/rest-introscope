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


import java.lang.reflect.Method;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * {@code MethodUtilTest} is an unit test for {@link MethodUtil} class.
 * <p/>
 *
 * @author Indra Basak
 * @since Oct, 2014
 */
public class MethodUtilTest {

    @Test
    public void testfindValidMethod() {
        Method method = MethodUtil.findMethod(String.class, "substring",
                "(II)Ljava/lang/String;");
        assertNotNull(method);
    }

    @Test
    public void testfindInvalidMethod() {
        Method method = MethodUtil.findMethod(String.class, "substring",
                "(II)Ljava/lang/Strinx;");
        assertNull(method);
    }

    @Test
    public void testGetAnnotation() {
        Method method =
                MethodUtil.findMethod(AnnotationTestClass.class, "doSomething",
                        "()V");
        assertNotNull(method);
        String annoStr = MethodUtil.getAnnotation(method, "org.junit.Test");
        assertNotNull(annoStr);
        System.out.println("method: " + method);
        System.out.println("annoStr: " + annoStr);
    }

    private class AnnotationTestClass {
        @Test
        public void doSomething() {

        }
    }
}
