package ru.vudovenko.micro.planner.plannerutils.exchangeRequests.interfaces;

public interface RequestExchanger {

    String baseUrl = "http://localhost:8765/planner-users/user/";

    boolean isUserExisting(Long userId);
}
