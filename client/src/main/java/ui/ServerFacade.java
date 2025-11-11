package ui;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import Exception.ResponseException;

import com.google.gson.Gson;
import model.*;

public class ServerFacade {
    private final HttpClient mClient = HttpClient.newHttpClient();
    private final String mServerUrl;

    public ServerFacade(String aUrl)
    {
        mServerUrl = aUrl;
    }

    public RegisterResult register(RegisterRequest aRequest) throws ResponseException
    {
        var request = buildRequest("POST", "/user", aRequest);
        var response = sendRequest(request);
        return handleResponse(response, RegisterResult.class);
    }

    public LoginResult login(LoginRequest aRequest) throws ResponseException
    {
        var request = buildRequest("POST", "/session", aRequest);
        var response = sendRequest(request);

        return handleResponse(response, LoginResult.class);
    }

    public void logout(LogoutRequest aRequest) throws ResponseException
    {
        var request = buildRequest("DELETE", "/session", aRequest);
        var response = sendRequest(request);
        handleResponse(response, null);
    }

    public ListGamesResult listGames(ListGamesRequest aRequest) throws ResponseException
    {
        var request = buildRequest("GET", "/user", aRequest);
        var response = sendRequest(request);
        return handleResponse(response, ListGamesResult.class);
    }

    public CreateGameResult createGame(CreateGameRequest aRequest) throws ResponseException
    {
        var request = buildRequest("POST", "/user", aRequest);
        var response = sendRequest(request);
        return handleResponse(response, CreateGameResult.class);
    }

    public void joinGame(JoinGameRequest aRequest) throws ResponseException
    {
        var request = buildRequest("PUT", "/user", aRequest);
        var response = sendRequest(request);
        handleResponse(response, null);
    }

    public void clear() throws ResponseException
    {
        var request = buildRequest("DELETE", "/user", null);
        var response = sendRequest(request);
        handleResponse(response, null);
    }

    private HttpRequest buildRequest(String aMethod, String aPath, Object aBody)
    {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(mServerUrl + aPath))
                .method(aMethod, makeRequestBody(aBody));
        if (aBody != null)
        {
            request.setHeader("Content-Type", "application/json");
        }
        return request.build();
    }

    private BodyPublisher makeRequestBody(Object aRequest)
    {
        if (aRequest != null)
        {
            return BodyPublishers.ofString(new Gson().toJson(aRequest));
        }
        else
        {
            return BodyPublishers.noBody();
        }
    }


    private HttpResponse<String> sendRequest(HttpRequest aRequest) throws ResponseException
    {
        try
        {
            return mClient.send(aRequest, HttpResponse.BodyHandlers.ofString());
        }
        catch (Exception e)
        {
            throw new ResponseException(ResponseException.Code.ServerError, e.getMessage());
        }
    }

    private <T> T handleResponse(HttpResponse<String> aResponse, Class<T> aResponseClass) throws ResponseException
    {
        var status = aResponse.statusCode();
        if (status != 200)
        {

            var body = aResponse.body();
            if (body != null)
            {
                throw new ResponseException(ResponseException.fromHttpStatusCode(status), ResponseException.statusMessage(status));
            }
            throw new ResponseException(ResponseException.fromHttpStatusCode(status), "other failure: " + status);
        }
        if (aResponseClass != null)
        {
            return new Gson().fromJson(aResponse.body(), aResponseClass);
        }
        return null;
    }

}
