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

import com.wily.introscope.agent.IAgent;
import com.wily.introscope.agent.trace.INameFormatter;
import com.wily.introscope.agent.trace.InvocationData;
import com.wily.util.StringUtils;
import com.wily.util.feedback.IModuleFeedbackChannel;

/**
 * {@code JaxrsNameFormatter} is a metric name formatter for REST classes
 * marked
 * with JAXRS @Path annotation.
 * <pre>
 *  @Path("/books")
 *  public class BookService {
 *      @GET
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
@SuppressWarnings({"squid:S1226"})
public class JaxrsNameFormatter implements INameFormatter {

    public static final String PATH_NAME_HOLDER = "{path}";

    public static final String PATH_ANNOTATION = "javax.ws.rs.Path";

    private IAgent fAgent;

    private IModuleFeedbackChannel fFeedback;

    /**
     * Constructs a <code>JaxrsNameFormatter</code> which takes an agent as
     * parameter.
     *
     * @param agent Java agent reference
     */
    public JaxrsNameFormatter(IAgent agent) {
        fAgent = agent;
        fFeedback = fAgent.IAgent_getModuleFeedback();
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
        String clazzPath = null;
        String methodPath = null;

        fFeedback.debug("INameFormatter_format app name: " + appName);

        Class<?> invocationClass = data.getInvocationObject().getClass();
        fFeedback.info("Invocation object " + invocationClass.getName());

        String annoStr = ParserHelper.findClassAnnotation(invocationClass,
                PATH_ANNOTATION);
        if (annoStr != null) {
            fFeedback.debug("INameFormatter_format cntrl req map: " + annoStr);
            // do something about clazz path
            clazzPath = annoStr;
        }

        methodPath = findMethodPath(data);
        String path = getPath(appName, clazzPath, methodPath);
        name = StringUtils.replace(name, PATH_NAME_HOLDER, path);

        return name;
    }

    /**
     * Retrieves Spring Request Mapping annotation from the controller's invoked
     * method
     *
     * @param data invocation data
     * @return request mapping params object
     */
    private String findMethodPath(InvocationData data) {
        String path = null;
        Object invocationObj = data.getInvocationObject();

        String methodName = data.getProbeInformation().getProbeIdentification()
                .getProbeMethodName();
        fFeedback.debug("findOperation - method name: " + methodName);

        String methodDesc = data.getProbeInformation().getProbeIdentification()
                .getProbeMethodDescriptor();
        fFeedback.debug("findOperation - method desc: " + methodDesc);

        String annoStr =
                ParserHelper.findMethodAnnotation(invocationObj.getClass(),
                        methodName, methodDesc, PATH_ANNOTATION);

        if (annoStr != null) {
            fFeedback.info("findOperation - annotation: " + annoStr);
            // may be parse path anno
            path = annoStr;
        }

        return path;
    }

    /**
     * Creates a path name from a front end application name, controller's path
     * annotation, and invoked method's path mapping annotation.
     *
     * @param appName    front end application name
     * @param clazzPath  path specified at class level
     * @param methodPath invoked method's request path
     * @return valid path name if one of the parameter is not null, otherwise
     * returns path name as 'nopath'
     */
    private String getPath(String appName, String clazzPath,
            String methodPath) {
        String path = null;

        if (appName != null) {
            path = appName;
        }

        String cntrlMthdPath = null;
        if (clazzPath != null) {
            cntrlMthdPath = clazzPath;
        }

        if (methodPath != null) {
            String suffix = methodPath.startsWith("/") ? methodPath
                    : ("/" + methodPath);
            cntrlMthdPath = (cntrlMthdPath != null) ? (cntrlMthdPath + suffix)
                    : suffix;
        }

        if (path != null && cntrlMthdPath != null) {
            path += "|" + cntrlMthdPath;
        } else if (path == null && cntrlMthdPath != null) {
            path = cntrlMthdPath;
        } else {
            path = "nopath";
        }

        if (path != null) {
            path = path.intern();
        }

        return path;
    }
}
