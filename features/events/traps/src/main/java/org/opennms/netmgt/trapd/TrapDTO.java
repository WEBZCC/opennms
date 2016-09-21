/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2011-2014 The OpenNMS Group, Inc.
 * OpenNMS(R) is Copyright (C) 1999-2014 The OpenNMS Group, Inc.
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

package org.opennms.netmgt.trapd;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.opennms.core.camel.MinionDTO;
import org.opennms.core.utils.InetAddressUtils;
import org.opennms.netmgt.snmp.SnmpResult;

@XmlRootElement(name = "trap-dto")
@XmlAccessorType(XmlAccessType.NONE)
public class TrapDTO extends MinionDTO {

	private static final long serialVersionUID = -6023265128058526987L;

	public static final String COMMUNITY = "community";
	public static final String PDU_LENGTH = "pduLength";
	public static final String VERSION = "version";
	public static final String TIMESTAMP = "timestamp";
	public static final String TRAP_ADDRESS = "trapAddress";

	protected TrapDTO() {
		// No-arg constructor for JAXB
		super();
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this)
				.append("systemId", super.getHeaders().get(SYSTEM_ID))
				.append("location", super.getHeaders().get(LOCATION))
				.append("sourceAddress", super.getHeaders().get(SOURCE_ADDRESS))
				.append("sourcePort", super.getHeaders().get(SOURCE_PORT))
				.append("community", super.getHeaders().get(COMMUNITY))
				.append("pduLength", super.getHeaders().get(PDU_LENGTH))
				.append("version", super.getHeaders().get(VERSION))
				.append("timestamp", super.getHeaders().get(TIMESTAMP))
				.append("trapAddress", super.getHeaders().get(TRAP_ADDRESS))
				.append("body", super.getBody()).toString();
	}

	@XmlElementWrapper(name = "results")
	@XmlElement(name = "result")
	private List<SnmpResult> results = new ArrayList<>(0);

	public void setCommunity(String m_community) {
		super.putHeader(COMMUNITY, m_community);
	}

	public void setPduLength(String m_pduLength) {
		super.putHeader(PDU_LENGTH, m_pduLength);
	}

	public void setVersion(String m_version) {
		super.putHeader(VERSION, m_version);
	}

	public void setTimestamp(Long m_timestamp) {
		super.putHeader(TIMESTAMP, Long.toString(m_timestamp));
	}

	public void setTrapAddress(InetAddress m_trapAddress) {
		super.putHeader(TRAP_ADDRESS, InetAddressUtils.str(m_trapAddress));
	}

	public void setAgentAddress(InetAddress m_agentAddress) {
		super.putHeader(SOURCE_ADDRESS, InetAddressUtils.str(m_agentAddress));
	}

	public List<SnmpResult> getResults() {
		return results;
	}

	public void setResults(List<SnmpResult> results) {
		this.results = results;
	}
	
	public void setSystemId(String m_systemId) {
		super.putHeader(MinionDTO.SYSTEM_ID, m_systemId);
	}

	public void setLocation(String m_location) {
		super.putHeader(MinionDTO.LOCATION, m_location);
	}

}