package com.enterpaper.comepennymaster;


/**
 * Created by Kim on 2015-07-14.
 */
public class IdeaListItem {
    private String img_url;
    private String content;
    private String Email;
    private int ViewCount;
    private int LikeCount;
    private int idea_id;

    public IdeaListItem(String img_url, String content, String email, int viewCount, int likeCount, int idea_id) {
        this.img_url = img_url;
        this.content = content;
        Email = email;
        ViewCount = viewCount;
        LikeCount = likeCount;
        this.idea_id = idea_id;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public int getViewCount() {
        return ViewCount;
    }

    public void setViewCount(int viewCount) {
        ViewCount = viewCount;
    }

    public int getLikeCount() {
        return LikeCount;
    }

    public void setLikeCount(int likeCount) {
        LikeCount = likeCount;
    }

    public int getIdea_id() {
        return idea_id;
    }

    public void setIdea_id(int idea_id) {
        this.idea_id = idea_id;
    }
}
