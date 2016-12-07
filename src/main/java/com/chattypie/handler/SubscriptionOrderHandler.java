package com.chattypie.handler;

import static java.lang.String.format;

import java.util.Optional;
import java.util.UUID;

import lombok.RequiredArgsConstructor;

import com.appdirect.sdk.appmarket.AppmarketEventHandler;
import com.appdirect.sdk.appmarket.events.APIResult;
import com.appdirect.sdk.appmarket.events.SubscriptionOrder;
import com.chattypie.persistence.model.CompanyAccount;
import com.chattypie.service.appmarket.CompanyAccountService;
import com.chattypie.service.chattypie.chatroom.Chatroom;
import com.chattypie.service.chattypie.chatroom.ChatroomService;
import com.chattypie.service.chattypie.greeting.AsyncNotificationService;

@RequiredArgsConstructor
public class SubscriptionOrderHandler implements AppmarketEventHandler<SubscriptionOrder> {

	private static final String CHATROOM_FIELD_NAME = "chatroomName";

	private final CompanyAccountService companyAccountService;
	private final ChatroomService chatroomService;
	private final AsyncNotificationService asyncNotificationService;

	@Override
	public APIResult handle(SubscriptionOrder event) {
		String idOfCompanyPlacingTheOrder = event.getCompanyInfo().getUuid();
		Optional<CompanyAccount> existingCompanyAccount = companyAccountService.findExistingCompanyAccountById(idOfCompanyPlacingTheOrder);
		CompanyAccount companyAccount = existingCompanyAccount.orElseGet(
			() -> companyAccountService.createCompanyAccountFor(idOfCompanyPlacingTheOrder)
		);

		String chatroomName = event.getConfiguration().getOrDefault(CHATROOM_FIELD_NAME, UUID.randomUUID().toString());
		Chatroom chatroom = chatroomService.createChatroomForAccount(companyAccount.getAccountId(), chatroomName);

		if (!existingCompanyAccount.isPresent()) {
			asyncNotificationService.sendNewCompanyGreeting(event.getCompanyInfo());
		}

		APIResult success = APIResult.success(format("Successfully placed order for account: %s", companyAccount));
		success.setAccountIdentifier(chatroom.getId());
		return success;
	}
}
