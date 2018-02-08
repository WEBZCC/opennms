/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2018-2018 The OpenNMS Group, Inc.
 * OpenNMS(R) is Copyright (C) 1999-2018 The OpenNMS Group, Inc.
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

package org.opennms.netmgt.flows.classification.internal.validation;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import org.opennms.netmgt.flows.classification.error.ErrorTemplate;
import org.opennms.netmgt.flows.classification.exception.ClassificationException;
import org.opennms.netmgt.flows.classification.persistence.api.Rule;

public class ValidatorTestUtils {
    protected interface Block {
        void execute();
    }

    protected static void verify(final Rule rule) {
        verify(rule, null);
    }

    protected static void verify(final Block block) {
        verify(block, null);
    }

    protected static void verify(final Rule rule, final ErrorTemplate expectedErrorTemplate) {
        verify(() -> new RuleValidator().validate(rule), expectedErrorTemplate);
    }

    protected static void verify(final Block block, final ErrorTemplate expectedErrorTemplate) {
        try {
            block.execute();
            if (expectedErrorTemplate != null) {
                fail("Expected validation to fail, but succeeded");
            }
        } catch (ClassificationException ex) {
            // Arguments may vary, so just verify the template
            assertThat(ex.getError().getTemplate(), is(expectedErrorTemplate));
        }
    }
}
