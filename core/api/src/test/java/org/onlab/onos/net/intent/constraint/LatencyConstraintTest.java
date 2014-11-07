/*
 * Copyright 2014 Open Networking Laboratory
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.onlab.onos.net.intent.constraint;

import com.google.common.testing.EqualsTester;
import org.junit.Before;
import org.junit.Test;
import org.onlab.onos.net.Annotations;
import org.onlab.onos.net.DefaultAnnotations;
import org.onlab.onos.net.DefaultLink;
import org.onlab.onos.net.DefaultPath;
import org.onlab.onos.net.DeviceId;
import org.onlab.onos.net.Link;
import org.onlab.onos.net.Path;
import org.onlab.onos.net.PortNumber;
import org.onlab.onos.net.provider.ProviderId;
import org.onlab.onos.net.resource.LinkResourceService;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;

import static org.easymock.EasyMock.createMock;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.onlab.onos.net.DefaultLinkTest.cp;
import static org.onlab.onos.net.DeviceId.deviceId;
import static org.onlab.onos.net.Link.Type.DIRECT;

public class LatencyConstraintTest {

    private static final DeviceId DID1 = deviceId("of:1");
    private static final DeviceId DID2 = deviceId("of:2");
    private static final DeviceId DID3 = deviceId("of:3");
    private static final PortNumber PN1 = PortNumber.portNumber(1);
    private static final PortNumber PN2 = PortNumber.portNumber(2);
    private static final PortNumber PN3 = PortNumber.portNumber(3);
    private static final PortNumber PN4 = PortNumber.portNumber(4);
    private static final ProviderId PROVIDER_ID = new ProviderId("of", "foo");
    private static final String LATENCY_KEY = "latency";
    private static final String LATENCY1 = "3.0";
    private static final String LATENCY2 = "4.0";

    private LatencyConstraint sut;
    private LinkResourceService linkResourceService;

    private Path path;
    private Link link1;
    private Link link2;

    @Before
    public void setUp() {
        linkResourceService = createMock(LinkResourceService.class);

        Annotations annotations1 = DefaultAnnotations.builder().set(LATENCY_KEY, LATENCY1).build();
        Annotations annotations2 = DefaultAnnotations.builder().set(LATENCY_KEY, LATENCY2).build();

        link1 = new DefaultLink(PROVIDER_ID, cp(DID1, PN1), cp(DID2, PN2), DIRECT, annotations1);
        link2 = new DefaultLink(PROVIDER_ID, cp(DID2, PN3), cp(DID3, PN4), DIRECT, annotations2);
        path = new DefaultPath(PROVIDER_ID, Arrays.asList(link1, link2), 10);
    }

    /**
     * Tests the path latency is less than the supplied constraint.
     */
    @Test
    public void testLessThanLatency() {
        sut = new LatencyConstraint(Duration.of(10, ChronoUnit.MICROS));

        assertThat(sut.validate(path, linkResourceService), is(true));
    }

    /**
     * Tests the path latency is more than the supplied constraint.
     */
    @Test
    public void testMoreThanLatency() {
        sut = new LatencyConstraint(Duration.of(3, ChronoUnit.MICROS));

        assertThat(sut.validate(path, linkResourceService), is(false));
    }

    /**
     * Tests the link latency is equal to "latency" annotated value.
     */
    @Test
    public void testCost() {
        sut = new LatencyConstraint(Duration.of(10, ChronoUnit.MICROS));

        assertThat(sut.cost(link1, linkResourceService), is(closeTo(Double.parseDouble(LATENCY1), 1.0e-6)));
        assertThat(sut.cost(link2, linkResourceService), is(closeTo(Double.parseDouble(LATENCY2), 1.0e-6)));
    }

    /**
     * Tests equality of the instances.
     */
    @Test
    public void testEquality() {
        LatencyConstraint c1 = new LatencyConstraint(Duration.of(1, ChronoUnit.SECONDS));
        LatencyConstraint c2 = new LatencyConstraint(Duration.of(1000, ChronoUnit.MILLIS));

        LatencyConstraint c3 = new LatencyConstraint(Duration.of(2, ChronoUnit.SECONDS));
        LatencyConstraint c4 = new LatencyConstraint(Duration.of(2000, ChronoUnit.MILLIS));

        new EqualsTester()
                .addEqualityGroup(c1, c2)
                .addEqualityGroup(c3, c4)
                .testEquals();
    }
}
