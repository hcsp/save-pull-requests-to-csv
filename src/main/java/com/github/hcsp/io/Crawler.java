package com.github.hcsp.io;

import com.alibaba.fastjson.JSON;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Crawler {
    // 给定一个仓库名，例如"golang/go"，或者"gradle/gradle"，读取前n个Pull request并保存至csvFile指定的文件中，格式如下：
    // number,author,title
    // 12345,blindpirate,这是一个标题
    // 12345,FrankFang,这是第二个标题
    public static final OkHttpClient client = new OkHttpClient();

    private static List<GitHubPullRequest> getGitHubPullRequestList(String repo, int number) throws IOException {
        List<GitHubPullRequest> result = new ArrayList<>();


        HttpUrl.Builder urlBuilder = HttpUrl.parse("https://api.github.com/repos/" + repo + "/pulls").newBuilder();
        urlBuilder.addQueryParameter("per_page", Integer.toString(number));
        String url = urlBuilder.build().toString();

        Request request = new Request.Builder()
                .url(url)
                .build();
        try (Response response = client.newCall(request).execute()) {
            List<GitHubPullResponse> gitHubPullResponses = JSON.parseArray(Objects.requireNonNull(response.body()).string(), GitHubPullResponse.class);
            for (GitHubPullResponse item :
                    gitHubPullResponses) {
                result.add(new GitHubPullRequest(item.getNumber(), item.getTitle(), item.getUser().getLogin()));
            }
        }
        return result;
    }

    public static void savePullRequestsToCSV(String repo, int n, File csvFile) throws IOException {
        List<GitHubPullRequest> gitHubPullRequestList = getGitHubPullRequestList(repo, n);
        try (BufferedWriter writer = Files.newBufferedWriter(csvFile.toPath(), StandardCharsets.UTF_8)) {
            writer.append("number,author,title\n");
            ColumnPositionMappingStrategy strategy = new ColumnPositionMappingStrategy();
            strategy.setType(GitHubPullRequest.class);

            StatefulBeanToCsv beanToCsv = new StatefulBeanToCsvBuilder(writer)
                    .withMappingStrategy(strategy)
                    .build();
            beanToCsv.write(gitHubPullRequestList);

        } catch (CsvDataTypeMismatchException | CsvRequiredFieldEmptyException | IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        File file = new File("./pr.csv");
        savePullRequestsToCSV("golang/go", 31, file);
    }
}
