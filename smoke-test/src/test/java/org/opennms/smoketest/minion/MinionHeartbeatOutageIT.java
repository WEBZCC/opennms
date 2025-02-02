/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2016-2021 The OpenNMS Group, Inc.
 * OpenNMS(R) is Copyright (C) 1999-2021 The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is a registered trademark of The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * OpenNMS(R) is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with OpenNMS(R).  If not, see:
 *      http://www.gnu.org/licenses/
 *
 * For more information contact:
 *     OpenNMS(R) Licensing <license@opennms.org>
 *     http://www.opennms.org/
 *     http://www.opennms.com/
 *******************************************************************************/
package org.opennms.smoketest.minion;

import static com.jayway.awaitility.Awaitility.await;
import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.Timeout;
import org.opennms.core.criteria.CriteriaBuilder;
import org.opennms.netmgt.dao.hibernate.EventDaoHibernate;
import org.opennms.netmgt.dao.hibernate.MinionDaoHibernate;
import org.opennms.netmgt.dao.hibernate.NodeDaoHibernate;
import org.opennms.netmgt.events.api.EventConstants;
import org.opennms.netmgt.model.OnmsEvent;
import org.opennms.netmgt.model.OnmsMonitoringSystem;
import org.opennms.netmgt.model.OnmsNode;
import org.opennms.netmgt.model.minion.OnmsMinion;
import org.opennms.smoketest.stacks.IpcStrategy;
import org.opennms.smoketest.stacks.OpenNMSStack;
import org.opennms.smoketest.stacks.StackModel;
import org.opennms.smoketest.utils.DaoUtils;
import org.opennms.smoketest.utils.TestContainerUtils;

/**
 * This test starts up Minion with the default JMS sink and makes sure
 * that heartbeat messages continue to be processed even if the Minion
 * and OpenNMS instances are restarted.
 * 
 * @author Seth
 */
@Category(org.opennms.smoketest.junit.FlakyTests.class)
//@Category(org.opennms.smoketest.junit.MinionTests.class)
public class MinionHeartbeatOutageIT {

    @Rule
    public final OpenNMSStack stack = OpenNMSStack.withModel(StackModel.newBuilder()
            .withMinion()
            .withIpcStrategy(getIpcStrategy())
            .build());

    @Rule
    public Timeout timeout = new Timeout(20, TimeUnit.MINUTES);

    @Test
    public void testHeartbeatOutages() throws Exception {
        Date startOfTest = new Date();

        // Wait for the Minion to show up
        await().atMost(90, SECONDS).pollInterval(5, SECONDS)
            .until(DaoUtils.countMatchingCallable(
                 stack.postgres().dao(MinionDaoHibernate.class),
                 new CriteriaBuilder(OnmsMinion.class)
                     .gt("lastUpdated", startOfTest)
                     .eq("location", stack.minion().getLocation())
                     .toCriteria()
                 ),
                 is(1)
             );

        // Make sure that the node is available
        await().atMost(180, SECONDS).pollInterval(5, SECONDS)
            .until(DaoUtils.countMatchingCallable(
                stack.postgres().dao(NodeDaoHibernate.class),
                new CriteriaBuilder(OnmsNode.class)
                .eq("foreignSource", "Minions")
                .eq("foreignId", stack.minion().getId())
                .toCriteria()
                ),
            equalTo(1)
        );

        // Make sure that the expected events are present

        /*
        assertEquals(1, DaoUtils.countMatchingCallable(
            stack.postgres().dao(.getDao(EventDaoHibernate.class),
            new CriteriaBuilder(OnmsEvent.class)
            .eq("eventUei", EventConstants.MONITORING_SYSTEM_ADDED_UEI)
            .like("eventParms", String.format("%%%s=%s%%", EventConstants.PARAM_MONITORING_SYSTEM_TYPE, OnmsMonitoringSystem.TYPE_MINION))
            .like("eventParms", String.format("%%%s=%s%%", EventConstants.PARAM_MONITORING_SYSTEM_ID, "00000000-0000-0000-0000-000000ddba11"))
            .like("eventParms", String.format("%%%s=%s%%", EventConstants.PARAM_MONITORING_SYSTEM_LOCATION, "MINION"))
            .toCriteria()
            ).call().intValue()
        );
        */

        assertEquals(1,
            stack.postgres().dao(EventDaoHibernate.class).findMatching(
                new CriteriaBuilder(OnmsEvent.class)
                    .alias("eventParameters", "eventParameters")
                    .eq("eventUei", EventConstants.MONITORING_SYSTEM_ADDED_UEI).toCriteria()).stream()
                    .filter(e -> e.getEventParameters().stream()
                            .anyMatch(p -> EventConstants.PARAM_MONITORING_SYSTEM_TYPE.equals(p.getName()) && OnmsMonitoringSystem.TYPE_MINION.equals(p.getValue())))
                    .filter(e -> e.getEventParameters().stream()
                            .anyMatch(p -> EventConstants.PARAM_MONITORING_SYSTEM_ID.equals(p.getName()) && stack.minion().getId().equals(p.getValue())))
                    .filter(e -> e.getEventParameters().stream()
                            .anyMatch(p -> EventConstants.PARAM_MONITORING_SYSTEM_LOCATION.equals(p.getName()) && stack.minion().getLocation().equals(p.getValue())))
                    .distinct()
                    .count()
        );

        assertEquals(0, DaoUtils.countMatchingCallable(
            stack.postgres().dao(EventDaoHibernate.class),
            new CriteriaBuilder(OnmsEvent.class)
            .eq("eventUei", EventConstants.MONITORING_SYSTEM_LOCATION_CHANGED_UEI)
            .toCriteria()
            ).call().intValue()
        );

        for (int i = 0; i < 3; i++) {
            TestContainerUtils.restartContainer(stack.minion());

            // Reset the startOfTest timestamp
            startOfTest = new Date();

            await().atMost(2, MINUTES).pollInterval(5, SECONDS)
                .until(DaoUtils.countMatchingCallable(
                     stack.postgres().dao(MinionDaoHibernate.class),
                     new CriteriaBuilder(OnmsMinion.class)
                         .gt("lastUpdated", startOfTest)
                         .eq("location", stack.minion().getLocation())
                         .toCriteria()
                     ),
                     is(1)
                 );
        }

        for (int i = 0; i < 2; i++) {
            TestContainerUtils.restartContainer(stack.opennms());

            // Reset the startOfTest timestamp
            startOfTest = new Date();

            await().atMost(5, MINUTES).pollInterval(5, SECONDS)
                .until(DaoUtils.countMatchingCallable(
                     stack.postgres().dao(MinionDaoHibernate.class),
                     new CriteriaBuilder(OnmsMinion.class)
                         .gt("lastUpdated", startOfTest)
                         .eq("location", stack.minion().getLocation())
                         .toCriteria()
                     ),
                     is(1)
                 );
        }

        for (int i = 0; i < 1; i++) {
            TestContainerUtils.restartContainer(stack.minion());

            // Reset the startOfTest timestamp
            startOfTest = new Date();

            await().atMost(2, MINUTES).pollInterval(5, SECONDS)
                .until(DaoUtils.countMatchingCallable(
                     stack.postgres().dao(MinionDaoHibernate.class),
                     new CriteriaBuilder(OnmsMinion.class)
                         .gt("lastUpdated", startOfTest)
                         .eq("location", stack.minion().getLocation())
                         .toCriteria()
                     ),
                     is(1)
                 );
        }
    }

    public IpcStrategy getIpcStrategy() {
        return IpcStrategy.JMS;
    }
}
