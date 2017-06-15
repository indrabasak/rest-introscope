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

import com.basaki.agent.jaxrs.HelloWorldJaxrsService;
import com.wily.introscope.agent.IAgent;
import com.wily.introscope.agent.blame.ComponentTracer;
import com.wily.introscope.agent.enterprise.EnterpriseAgent;
import com.wily.introscope.agent.extension.IExtensionLocatorPolicy;
import com.wily.introscope.agent.trace.FrontendTracer;
import com.wily.introscope.agent.trace.ITracerFactory;
import com.wily.introscope.agent.trace.InvocationData;
import com.wily.introscope.agent.trace.ProbeIdentification;
import com.wily.introscope.agent.trace.ProbeInformation;
import com.wily.introscope.stat.blame.BlameStackSnapshotPolicy;
import com.wily.util.extension.IExtensionLocator;
import com.wily.util.feedback.ApplicationFeedback;
import com.wily.util.feedback.IModuleFeedbackChannel;
import com.wily.util.heartbeat.IntervalHeartbeat;
import com.wily.util.io.ExtendedFile;
import com.wily.util.properties.AttributeListing;
import com.wily.util.text.IStringLocalizer;
import com.wily.util.text.StringLocalizerHandle;
import com.wily.util.thread.DefaultThreadFactory;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * {@code JaxrsNameFormatterTest} is an unit test for {@link JaxrsNameFormatter}
 * class.
 * <p/>
 *
 * @author Indra Basak
 * @since 6/14/17
 */
public class JaxrsNameFormatterTest {

    private IAgent agent;

    private ApplicationFeedback feedback;

    private JaxrsNameFormatter formatter;

    @Before
    public void setUp() {
        feedback = new ApplicationFeedback("testApplication", "testModule");
        IExtensionLocatorPolicy extensionLocatorPolicy =
                new IExtensionLocatorPolicy() {

                    @Override
                    public IExtensionLocator createExtensionLocator(
                            IModuleFeedbackChannel iModuleFeedbackChannel,
                            IStringLocalizer iStringLocalizer,
                            ExtendedFile extendedFile) {
                        return null;
                    }
                };

        IntervalHeartbeat heartbeat = new IntervalHeartbeat("test-heartbeat",
                new DefaultThreadFactory(false), feedback,
                StringLocalizerHandle.getStringLocalizer(), 20000);

        agent = new EnterpriseAgent(feedback,
                "com.basaki.agent.JaxrsNameFormatterTest",
                extensionLocatorPolicy,
                StringLocalizerHandle.getStringLocalizer(), heartbeat);

        formatter = new JaxrsNameFormatter(agent);
    }

    @Test
    public void testINameFormatter_format() {
        String metricPath =
                formatter.INameFormatter_format("REST|JAXRS|{path}|GET",
                        getInvocationData());
        assertEquals("REST|JAXRS|/hello/{param}|GET", metricPath);
    }

    private InvocationData getInvocationData() {
        ComponentTracer tracer =
                new ComponentTracer(agent, BlameStackSnapshotPolicy.kFullBlame);

        ProbeIdentification probe =
                new ProbeIdentification("HelloWorldJaxrsService", "getMessage",
                        "(Ljava/lang/String;)Ljavax/ws/rs/core/Response;",
                        "com.basaki.agent.jaxrs.HelloWorldJaxrsService");

        FrontendTracer frontendTracer =
                new FrontendTracer(agent, new AttributeListing(), probe,
                        new HelloWorldJaxrsService());
        ITracerFactory[] tracerFactory = new ITracerFactory[1];
        tracerFactory[0] = frontendTracer;

        ProbeInformation info =
                new ProbeInformation(agent, probe, tracerFactory);

        InvocationData data =
                InvocationData.debug_createInvocationData(agent, info,
                        new HelloWorldJaxrsService());
        data.IMethodTracer_startTrace();
        data.IMethodTracer_finishTrace();

        return data;
    }
}
