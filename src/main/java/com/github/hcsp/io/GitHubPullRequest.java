package com.github.hcsp.io;

import com.opencsv.bean.CsvBindByPosition;

public class GitHubPullRequest {
    // Pull request的编号
    @CsvBindByPosition(position = 0)
    int number;
    // Pull request的标题
    @CsvBindByPosition(position = 2)
    String title;
    // Pull request的作者的 GitHub 用户名
    @CsvBindByPosition(position = 1)
    String author;

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

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    GitHubPullRequest(int number, String title, String author) {
        this.number = number;
        this.title = title;
        this.author = author;
    }
}
