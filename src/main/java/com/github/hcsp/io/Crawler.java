package com.github.hcsp.io;

import org.kohsuke.github.GHIssueState;
import org.kohsuke.github.GHPullRequest;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class Crawler {
    // 给定一个仓库名，例如"golang/go"，或者"gradle/gradle"，读取前n个Pull request并保存至csvFile指定的文件中，格式如下：
    // number,author,title
    // 12345,blindpirate,这是一个标题
    // 12345,FrankFang,这是第二个标题
    public static void savePullRequestsToCSV(String repo, int n, File csvFile) throws IOException {
        GitHub github = GitHub.connectAnonymously();
        GHRepository githubRepo = github.getRepository(repo);
        List<GHPullRequest> allPullRequestLists = githubRepo.getPullRequests(GHIssueState.OPEN);
        List<GHPullRequest> pullRequestLists = allPullRequestLists.subList(0, n);
        List<StringBuilder> lists = new ArrayList<>();
        lists.add(new StringBuilder("number,author,title\n"));
        for (GHPullRequest pullRequest :
                pullRequestLists) {
            StringBuilder stringBuilder = new StringBuilder();
            int id = pullRequest.getNumber();
            String author = pullRequest.getUser().getName();
            String title = pullRequest.getTitle();
            stringBuilder.append(id).append(",").append(author).append(",").append(title);
            lists.add(stringBuilder);
        }
        Files.write(csvFile.toPath(), lists);
    }

//    public static void main(String[] args) throws IOException {
//        File tmp = File.createTempFile("csv", "");
//        savePullRequestsToCSV("gradle/gradle", 10, tmp);
//        CSVReader reader = new CSVReader(new BufferedReader(new FileReader(tmp)));
//        List<String[]> lines = reader.readAll();
//    }
}
