package com.github.hcsp.io;

import org.kohsuke.github.GHIssueState;
import org.kohsuke.github.GHPullRequest;
import org.kohsuke.github.GitHub;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class Crawler {
    // 给定一个仓库名，例如"golang/go"，或者"gradle/gradle"，读取前n个Pull request并保存至csvFile指定的文件中，格式如下：
    // number,author,title
    // 12345,blindpirate,这是一个标题
    // 12345,FrankFang,这是第二个标题

    /**
     * 将指定仓库的前 n 个 pull request 写入 csvFile 指定的文件中
     *
     * @param repo    仓库名
     * @param n       获取前 n 个pull request
     * @param csvFile csvFile 指定的文件
     */
    public static void savePullRequestsToCSV(String repo, int n, File csvFile) {
        try {
            List<GHPullRequest> pullRequests = getPullRequests(repo, n);
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(csvFile));
            bufferedWriter.write("number,author,title" + "\n");
            for (GHPullRequest pullRequest : pullRequests) {
                bufferedWriter.write(pullRequest.getNumber() + "," + pullRequest.getUser().getLogin() + "," + pullRequest.getTitle() + "\n");
            }
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取指定仓库的前 n 个pull request
     *
     * @param repo 仓库名
     * @param n    获取前 n 个pull request
     * @return 指定仓库的前 n 个pull request
     * @throws IOException IOException
     */
    private static List<GHPullRequest> getPullRequests(String repo, int n) throws IOException {
        return GitHub.connectAnonymously().getRepository(repo).getPullRequests(GHIssueState.ALL).subList(0, n + 1);
    }
}
