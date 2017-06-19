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

package com.basaki.agent.spring;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * {@code HelloWorldRestSpringService} is an example Spring REST service class
 * with a RestController annotation
 * <p/>
 *
 * @author Indra Basak
 * @since 6/19/17
 */
@RestController("/bonjour")
public class HelloWorldRestSpringService {

    @RequestMapping(method = RequestMethod.GET, value = "/{msg}",
            produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    public String getMessage(@PathVariable("msg") String msg) {
        return "Spring say : " + msg;
    }
}
