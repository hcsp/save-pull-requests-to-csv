package com.github.hcsp.io;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.support.ClassicRequestBuilder;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;

public class Crawler {
    // 给定一个仓库名，例如"golang/go"，或者"gradle/gradle"，读取前n个Pull request并保存至csvFile指定的文件中，格式如下：
    // number,author,title
    // 12345,blindpirate,这是一个标题
    // 12345,FrankFang,这是第二个标题
    public static void savePullRequestsToCSV(String repo, int n, File csvFile) throws IOException {
        List<GithubPullRequest> pulls = parsePullRequestFromJSON(getRepositoryPullRequestJSON(repo));
        List<String> csvContents = pulls.stream()
                .map(pull -> pull.getNumber() + "," + pull.getUser().getLogin() + "," + pull.getTitle())
                .collect(Collectors.toList());
        csvContents.add(0, "number,author,title");
        Files.write(csvFile.toPath(), csvContents, StandardCharsets.UTF_8);
    }

    public static class GithubPullRequest {
        public static class User {
            private String login;

            public String getLogin() {
                return login;
            }

            public void setLogin(String login) {
                this.login = login;
            }
        }

        private int number;
        private String title;

        private User user;

        public int getNumber() {
            return number;
        }

        public void setNumber(int number) {
            this.number = number;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public User getUser() {
            return user;
        }

        public void setUser(User user) {
            this.user = user;
        }
    }

    public static String getRepositoryPullRequestJSON(String repo) throws IOException {
        return httpGet("https://api.github.com/repos/" + repo + "/pulls");
    }

    public static List<GithubPullRequest> parsePullRequestFromJSON(String json) {
        Gson gson = new Gson();
        TypeToken<List<GithubPullRequest>> type = new TypeToken<List<GithubPullRequest>>() {
        };
        return gson.fromJson(json, type);
    }

    private static String httpGet(String url) throws IOException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            ClassicHttpRequest httpGet = ClassicRequestBuilder.get(url).build();
            System.out.println("Loading resource...");
            return httpClient.execute(httpGet, (response) -> EntityUtils.toString(response.getEntity()));
        }
    }
}
