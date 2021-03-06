/*
 * Copyright 2017 AppDirect, Inc. and/or its affiliates
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.chattypie.domain.ownership.verification;

import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.web.client.RestTemplate;

@RunWith(MockitoJUnitRunner.class)
public class DomainOperationsServiceTest {

	private DomainOperationsService tested;
	private String chattyPieHostMock = "http://example.com";

	@Mock
	private RestTemplate restTemplateMock;

	@Before
	public void setUp() throws Exception {
		tested = new DomainOperationsService(restTemplateMock, chattyPieHostMock);
	}

	@Test
	public void testGetDomainOwnershipProof_whenCalled_thenAGetShouldBeInvokedOnTheChattyPieOwnershipProofEndpoint() throws Exception {
		//Given
		String testDomain = "dummy.com";
		String testAccountId = UUID.randomUUID().toString();
		ChattyPieDomainOwnershipProof expectedOwnershipProof = ChattyPieDomainOwnershipProof.builder()
				.build();
		final String expectedGetUrl = format("%s/accounts/%s/domains/%s/ownershipProof", chattyPieHostMock, testAccountId, testDomain);

		when(
				restTemplateMock.getForObject(
						expectedGetUrl,
						ChattyPieDomainOwnershipProof.class
				)
		).thenReturn(
				expectedOwnershipProof
		);

		//When
		final ChattyPieDomainOwnershipProof actualDomainOwnershipProof = tested.getDomainOwnershipProof(testAccountId, testDomain);

		//Then
		assertThat(actualDomainOwnershipProof).isEqualTo(expectedOwnershipProof);
	}
}
