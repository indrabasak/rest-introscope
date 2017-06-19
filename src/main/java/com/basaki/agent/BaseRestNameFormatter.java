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

import com.basaki.agent.util.IAnnotation;
import com.basaki.agent.util.ParserHelper;
import com.basaki.agent.util.RestAnnotation;
import com.basaki.agent.util.RestAnnotationParam;
import com.wily.introscope.agent.IAgent;
import com.wily.introscope.agent.trace.INameFormatter;
import com.wily.introscope.agent.trace.InvocationData;
import com.wily.util.feedback.IModuleFeedbackChannel;
import java.util.List;

/**
 * {@code JBaseRestNameFormatter} is the abstract base class for REST (JAX-RS,
 * Spring) name formatters.
 *
 * @author Indra Basak
 * @since 6/18/17
 */
public abstract class BaseRestNameFormatter implements INameFormatter {

    private IModuleFeedbackChannel feedback;

    /**
     * Constructs a <code>JaxrsNameFormatter</code> which takes an agent as
     * parameter.
     *
     * @param agent Java agent reference
     */
    public BaseRestNameFormatter(IAgent agent) {
        feedback = agent.IAgent_getModuleFeedback();
    }

    public IModuleFeedbackChannel getFeedback() {
        return feedback;
    }

    /**
     * Retrieves Spring Controller annotation from the controller's class
     *
     * @param data invocation data
     * @return controller rest annotation object
     */
    public RestAnnotation findClassAnnotation(InvocationData data,
            String[] annotations) {
        RestAnnotation anno = null;

        Class<?> invocationClass = data.getInvocationObject().getClass();
        feedback.info("Invocation object " + invocationClass.getName());
        String annoStr = null;
        for (String annotation : annotations) {
            annoStr = ParserHelper.findClassAnnotation(invocationClass,
                    annotation);
            if (annoStr != null) {
                break;
            }
        }

        if (annoStr != null) {
            feedback.info(
                    "INameFormatter_format cntrl annotation: " + annoStr);
            anno = ParserHelper.parseAnnotation(annoStr);
        }

        return anno;
    }

    /**
     * Retrieves Spring Request Mapping annotation from the controller's invoked
     * method
     *
     * @param data invocation data
     * @return request mapping annotation object
     */
    public RestAnnotation findMethodAnnotation(InvocationData data,
            String annotation) {
        RestAnnotation anno = null;

        Object invocationObj = data.getInvocationObject();

        String methodName = data.getProbeInformation().getProbeIdentification()
                .getProbeMethodName();

        String methodDesc = data.getProbeInformation().getProbeIdentification()
                .getProbeMethodDescriptor();

        String annoStr =
                ParserHelper.findMethodAnnotation(invocationObj.getClass(),
                        methodName, methodDesc, annotation);

        if (annoStr != null) {
            feedback.info(
                    "INameFormatter_format method annotation: " + annoStr);
            anno = ParserHelper.parseAnnotation(annoStr);
        }

        return anno;
    }

    /**
     * Creates a path name from a front end application name, controller's
     * request mapping annotation, and invoked method's request mapping
     * annotation.
     *
     * @param appName    front end application name
     * @param cntrlAnno  controller's request mapping annotation
     * @param methodAnno invoked method's request mapping annotation
     * @return valid path name if one of the parameter is not null, otherwise
     * returns path name as 'nopath'
     */

    public String getPath(String appName, RestAnnotation cntrlAnno,
            RestAnnotation methodAnno) {
        String path = null;

        if (appName != null) {
            path = appName;
        }

        String cntrlMthdPath = getValue(cntrlAnno, "value");
        String value = getValue(methodAnno, "value");
        if (value != null) {
            String suffix = value.startsWith("/") ? value : "/" + value;
            cntrlMthdPath =
                    (cntrlMthdPath != null) ? (cntrlMthdPath + suffix) : suffix;
        }

        if (path != null && cntrlMthdPath != null) {
            path += "|" + cntrlMthdPath;
        } else if (path == null && cntrlMthdPath != null) {
            path = cntrlMthdPath;
        } else {
            path = "nopath";
        }

        return path.intern();
    }

    public String getValue(RestAnnotation anno, String paramName) {
        String paramValue = null;
        if (anno != null) {
            List<IAnnotation> params = anno.getParam(paramName);
            if (params != null && params.size() == 1) {
                paramValue = ((RestAnnotationParam) params.get(0)).getValue();
            }
        }

        return paramValue;
    }
}
