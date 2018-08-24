package com.pdvend.githubrepoviewer.model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Special deserializer to conform the issue server response to the app Issue model.
 *
 * As the Github response lacks the repository name field, is necessary to parse that information
 * from the repository_url field.
 *
 * Here is also defined if the item is a pure issue or a pull request, which are the same entity
 * on Github with the difference of pull request having the 'pull_request' field.
 */
public class IssueDeserializer implements JsonDeserializer<Issue> {

    @Override
    public Issue deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        final JsonObject jsonObject = json.getAsJsonObject();

        final JsonElement jsonId = jsonObject.get("id");
        final int id = jsonId.getAsInt();

        final JsonElement jsonRepoUrl = jsonObject.get("repository_url");
        final String repoUrl = jsonRepoUrl.getAsString();
        final String repo = attemptToClearRepoUrl(repoUrl);

        final JsonElement jsonTitle = jsonObject.get("title");
        final String title = jsonTitle.getAsString();

        final JsonElement jsonNumber = jsonObject.get("number");
        final int number = jsonNumber.getAsInt();

        final JsonElement jsonBody = jsonObject.get("body");
        final String body = jsonBody.getAsString();

        final JsonElement jsonPull = jsonObject.get("pull_request");

        return new Issue(id, repo, number, title, body, jsonPull != null);
    }

    /**
     * Attempt to clear the url by removing the repos endpoint root.
     * e.g. https://api.github.com/repos/owner/repo = owner/repo
     * @param url
     * @return
     */
    private String attemptToClearRepoUrl(String url){
        Pattern p = Pattern.compile( "(?<=repos/).*" );
        Matcher m = p.matcher( url );
        if ( m.find() ) {
            return m.group(0);
        }

        return url;
    }
}
