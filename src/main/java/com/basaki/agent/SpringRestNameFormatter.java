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

package com.basaki.agent;

import com.basaki.agent.util.ParserHelper;
import com.basaki.agent.util.RestAnnotation;
import com.wily.introscope.agent.IAgent;
import com.wily.introscope.agent.trace.InvocationData;
import com.wily.util.StringUtils;
import com.wily.util.feedback.IModuleFeedbackChannel;

/**
 * <code>SpringRestNameFormatter</code> formats a metric path associated with a
 * Spring REST web service.
 * <pre>
 *  &#064;Controller
 *  public class BookService {
 *      &#064;RequestMapping(method = RequestMethod.GET, value = /books/{id},
 *      produces = {MediaType.APPLICATION_JSON_VALUE})
 *      &#064;ResponseBody
 *      public Book read(@ApiParam(value = "Book ID", required = true)
 *      &#064;PathVariable("id") UUID id) {
 *      return service.read(id);
 *      }
 *  }
 * </pre>
 * <p/>
 * PBD associated with this name formatter:
 * <p/>
 * <p>
 * <pre>
 *     SetTracerClassMapping: SpringRestBlamePointTracer
 * com.wily.introscope.agent.trace.hc2.BlamePointTracer
 * com.wily.introscope.probebuilder.validate.ResourceNameValidator
 *     SetTracerParameter: SpringRestBlamePointTracer nameformatter
 * com.basaki.agent.SpringRestNameFormatter
 *     SetTracerParameter: SpringRestBlamePointTracer reentrancy instance
 *     SetTracerOrdering: SpringRestBlamePointTracer -99000
 *
 *     SetFlag: SpringRest
 *     TurnOn: SpringRest
 *
 *     IdentifyAnnotatedClassAs: org.springframework.stereotype.Controller
 * SpringRest
 *     TraceAnnotatedMethodsIfFlagged: SpringRest org.springframework.web.bind.annotation.RequestMapping
 * SpringRestBlamePointTracer "Spring|REST|{path}|{op}|{classname}|{method}"
 * </pre>
 *
 * @author Indra Basak
 * @since Oct, 2014
 */
@SuppressWarnings({"squid:S1226"})
public class SpringRestNameFormatter extends BaseRestNameFormatter {

    private static final String PATH_NAME_HOLDER = "{path}";

    private static final String OP_NAME_HOLDER = "{op}";

    private static final String CONTROLLER_ANNOTATION =
            "org.springframework.stereotype.Controller";

    private static final String REST_CONTROLLER_ANNOTATION =
            "org.springframework.web.bind.annotation.RestController";

    private static final String REQUEST_MAPPING_ANNOTATION =
            "org.springframework.web.bind.annotation.RequestMapping";

    private IModuleFeedbackChannel feedback;

    /**
     * Constructs a <code>SpringRestNameFormatter</code> which takes an agent as
     * parameter.
     *
     * @param agent Java agent reference
     */
    public SpringRestNameFormatter(IAgent agent) {
        super(agent);
    }

    /**
     * Retrieves a formatted metric name path for REST service. Replaces the
     * '{path}' and '{op}' place holders.
     *
     * @param name metric path
     * @param data invocation data
     * @return name formatted metric path for Spring REST service
     */
    public String INameFormatter_format(String name, InvocationData data) {
        String appName =
                ParserHelper.getFrontendAppName(data.getFrontBoundary());

        String[] annotations =
                {CONTROLLER_ANNOTATION, REST_CONTROLLER_ANNOTATION};
        RestAnnotation ctrlAnno = findClassAnnotation(data, annotations);
        RestAnnotation methodAnno =
                findMethodAnnotation(data, REQUEST_MAPPING_ANNOTATION);

        String path = getPath(appName, ctrlAnno, methodAnno);
        name = StringUtils.replace(name, PATH_NAME_HOLDER, path);

        String value = getValue(methodAnno, "method");
        String op = value != null ? value : "noop";

        name = StringUtils.replace(name, OP_NAME_HOLDER, op);

        return name;
    }
}
