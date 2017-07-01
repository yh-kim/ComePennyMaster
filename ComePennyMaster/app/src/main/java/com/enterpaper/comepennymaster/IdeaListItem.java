package com.enterpaper.comepennymaster;


/**
 * Created by Kim on 2015-07-14.
 */
public class IdeaListItem {
    private String img_url;
    private String content;
    private String email;
    private String booth_name;
    private int ViewCount;
    private int commentCount;
    private int LikeCount;
    private int idea_id;

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
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBooth_name() {
        return booth_name;
    }

    public void setBooth_name(String booth_name) {
        this.booth_name = booth_name;
    }

    public int getViewCount() {
        return ViewCount;
    }

    public void setViewCount(int viewCount) {
        ViewCount = viewCount;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
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

    public IdeaListItem(String img_url, String content, String email, String booth_name, int viewCount, int commentCount, int likeCount, int idea_id) {

        this.img_url = img_url;
        this.content = content;
        this.email = email;
        this.booth_name = booth_name;
        ViewCount = viewCount;
        this.commentCount = commentCount;
        LikeCount = likeCount;
        this.idea_id = idea_id;
    }
}
