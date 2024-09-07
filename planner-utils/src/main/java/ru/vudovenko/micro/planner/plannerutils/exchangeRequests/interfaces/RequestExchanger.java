package ru.vudovenko.micro.planner.plannerutils.exchangeRequests.interfaces;

public interface RequestExchanger {

    String baseUrl = "http://localhost:8765/planner-users/user/";
    String baseUrlData = "http://localhost:8765/planner-todo/data/";

    boolean isUserExisting(String userId);
}
