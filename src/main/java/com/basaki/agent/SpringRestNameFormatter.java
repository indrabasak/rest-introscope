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
 * <code>SpringRestNameFormatter</code> formats a metric path associated with a
 * Spring REST web service.
 * <pre>
 *  @Controller
 *  public class BookService {
 *      @RequestMapping(method = RequestMethod.GET, value = /books/{id},
 *      produces = {MediaType.APPLICATION_JSON_VALUE})
 *      @ResponseBody
 *      public Book read(@ApiParam(value = "Book ID", required = true)
 *      @PathVariable("id") UUID id) {
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
public class SpringRestNameFormatter implements INameFormatter {

    public static final String PATH_NAME_HOLDER = "{path}";

    public static final String OP_NAME_HOLDER = "{op}";

    public static final String REQUEST_MAPPING_ANNOTATION =
            "org.springframework.web.bind.annotation.RequestMapping";

    private IAgent fAgent;

    private IModuleFeedbackChannel fFeedback;

    /**
     * Constructs a <code>SpringRestNameFormatter</code> which takes an agent as
     * parameter.
     *
     * @param agent Java agent reference
     */
    public SpringRestNameFormatter(IAgent agent) {
        fAgent = agent;
        fFeedback = fAgent.IAgent_getModuleFeedback();
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
        RequestMappingParams cntrlParams = null;
        RequestMappingParams methodParams = null;
        fFeedback.debug("INameFormatter_format app name: " + appName);

        Class<?> invocationClass = data.getInvocationObject().getClass();
        fFeedback.info("Invocation object " + invocationClass.getName());

        String annoStr = ParserHelper.findClassAnnotation(invocationClass,
                REQUEST_MAPPING_ANNOTATION);
        if (annoStr != null) {
            fFeedback.info("INameFormatter_format cntrl req map: " + annoStr);
            cntrlParams = ParserHelper.parseRequestMapping(annoStr);
        }

        methodParams = findMethodParams(data);

        String path = getPath(appName, cntrlParams, methodParams);
        name = StringUtils.replace(name, PATH_NAME_HOLDER, path);

        String op = "noop";
        if (methodParams != null && methodParams.getMethod() != null) {
            op = methodParams.getMethod();
        }

        name = StringUtils.replace(name, OP_NAME_HOLDER, op);

        return name;
    }

    /**
     * Retrieves Spring Request Mapping annotation from the controller's invoked
     * method
     *
     * @param data invocation data
     * @return request mapping params object
     */
    private RequestMappingParams findMethodParams(InvocationData data) {
        RequestMappingParams params = null;
        Object invocationObj = data.getInvocationObject();

        String methodName = data.getProbeInformation().getProbeIdentification()
                .getProbeMethodName();

        String methodDesc = data.getProbeInformation().getProbeIdentification()
                .getProbeMethodDescriptor();

        String annoStr =
                ParserHelper.findMethodAnnotation(invocationObj.getClass(),
                        methodName, methodDesc, REQUEST_MAPPING_ANNOTATION);
        if (annoStr != null) {
            params = ParserHelper.parseRequestMapping(annoStr);
        }

        return params;
    }

    /**
     * Creates a path name from a front end application name, controller's
     * request mapping annotation, and invoked method's request mapping
     * annotation.
     *
     * @param appName      front end application name
     * @param cntrlParams  controller's request mapping annotation
     * @param methodParams invoked method's request mapping annotation
     * @return valid path name if one of the parameter is not null, otherwise
     * returns path name as 'nopath'
     */
    private String getPath(String appName, RequestMappingParams cntrlParams,
            RequestMappingParams methodParams) {
        String path = null;

        if (appName != null) {
            path = appName;
        }

        String cntrlMthdPath = null;
        if (cntrlParams != null && cntrlParams.getValue() != null) {
            cntrlMthdPath = cntrlParams.getValue();
        }

        if (methodParams != null && methodParams.getValue() != null) {
            String suffix =
                    methodParams.getValue().startsWith("/") ? methodParams
                            .getValue() : ("/" + methodParams.getValue());
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
