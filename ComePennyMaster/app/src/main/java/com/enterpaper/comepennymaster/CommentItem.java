package com.enterpaper.comepennymaster;

/**
 * Created by Kim on 2015-11-02.
 */
public class CommentItem {

    private String user_comment_img;
    private String comment_content;
    private String Email;
    private String comment_time;
    private int comment_id;

    public CommentItem(String user_comment_img, String comment_content, String email, String comment_time, int comment_id) {
        this.user_comment_img = user_comment_img;
        this.comment_content = comment_content;
        Email = email;
        this.comment_time = comment_time;
        this.comment_id = comment_id;
    }

    public String getUser_comment_img() {
        return user_comment_img;
    }

    public void setUser_comment_img(String user_comment_img) {
        this.user_comment_img = user_comment_img;
    }

    public String getComment_content() {
        return comment_content;
    }

    public void setComment_content(String comment_content) {
        this.comment_content = comment_content;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getComment_time() {
        return comment_time;
    }

    public void setComment_time(String comment_time) {
        this.comment_time = comment_time;
    }

    public int getComment_id() {
        return comment_id;
    }

    public void setComment_id(int comment_id) {
        this.comment_id = comment_id;
    }
}