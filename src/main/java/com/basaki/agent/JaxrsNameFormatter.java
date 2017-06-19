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

import com.basaki.agent.util.ParserHelper;
import com.basaki.agent.util.RestAnnotation;
import com.wily.introscope.agent.IAgent;
import com.wily.introscope.agent.trace.InvocationData;
import com.wily.util.StringUtils;

/**
 * {@code JaxrsNameFormatter} is a metric name formatter for REST classes
 * marked
 * with JAXRS @Path annotation.
 * <pre>
 *  &#064;Path("/books")
 *  public class BookService {
 *      &#064;GET
 *      public Response getBook() {
 *          return Response.status(200).entity("getBook is called").build();
 *      }
 *  }
 * </pre>
 * <p/>
 * <p>
 * PBD associated with this name formatter:
 * <p/>
 * <p>
 * <pre>
 *     SetTracerClassMapping: JaxrsBlamePointTracer com.wily.introscope.agent.trace.hc2.BlamePointTracer
 * com.wily.introscope.probebuilder.validate.ResourceNameValidator
 *     SetTracerParameter: JaxrsBlamePointTracer nameformatter
 * com.basaki.agent.JaxrsNameFormatter
 *     SetTracerParameter: JaxrsBlamePointTracer reentrancy instance
 *     SetTracerOrdering: JaxrsBlamePointTracer -99001
 *
 *     SetFlag: JaxrsFlag
 *     TurnOn: JaxrsFlag
 *     IdentifyAnnotatedClassAs: javax.ws.rs.Path JaxrsFlag
 *     TraceAnnotatedMethodsIfFlagged: JaxrsFlag javax.ws.rs.GET
 * JaxrsBlamePointTracer "REST|JAXRS|{path}|GET|{classname}|{method}"
 *     TraceAnnotatedMethodsIfFlagged: JaxrsFlag javax.ws.rs.POST
 * JaxrsBlamePointTracer "REST|JAXRS|{path}|POST|{classname}|{method}"
 *     TraceAnnotatedMethodsIfFlagged: JaxrsFlag javax.ws.rs.PUT
 * JaxrsBlamePointTracer "REST|JAXRS|{path}|PUT|{classname}|{method}"
 *     TraceAnnotatedMethodsIfFlagged: JaxrsFlag javax.ws.rs.DELETE
 * JaxrsBlamePointTracer "REST|JAXRS|{path}|DELETE|{classname}|{method}"
 *     TraceAnnotatedMethodsIfFlagged: JaxrsFlag javax.ws.rs.HEAD
 * JaxrsBlamePointTracer "REST|JAXRS|{path}|HEAD|{classname}|{method}"
 * </pre>
 *
 * @author Indra Basak
 * @since Oct, 2014
 */
@SuppressWarnings({"squid:S1226", "squid:S2259"})
public class JaxrsNameFormatter extends BaseRestNameFormatter {

    private static final String PATH_NAME_HOLDER = "{path}";

    private static final String PATH_ANNOTATION = "javax.ws.rs.Path";

    /**
     * Constructs a <code>JaxrsNameFormatter</code> which takes an agent as
     * parameter.
     *
     * @param agent Java agent reference
     */
    public JaxrsNameFormatter(IAgent agent) {
        super(agent);
    }

    /**
     * Retrieves a formatted metric name path for REST service. Replaces the
     * '{path}' place holder.
     *
     * @param name metric path
     * @param data invocation data
     * @return name formatted metric path for JAXRS REST service
     */
    public String INameFormatter_format(String name, InvocationData data) {
        String appName = ParserHelper.getFrontendAppName(data
                .getFrontBoundary());
        getFeedback().debug("INameFormatter_format app name: " + appName);

        String[] annotations = {PATH_ANNOTATION};
        RestAnnotation classPathAnno = findClassAnnotation(data, annotations);
        RestAnnotation methodPathAnno =
                findMethodAnnotation(data, PATH_ANNOTATION);

        String path = getPath(appName, classPathAnno, methodPathAnno);
        name = StringUtils.replace(name, PATH_NAME_HOLDER, path);

        return name;
    }
}
