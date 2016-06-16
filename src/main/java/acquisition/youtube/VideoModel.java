package acquisition.youtube;

import com.google.api.services.youtube.model.CommentThread;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ceejay562 on 4/28/2016.
 */
public class VideoModel {
    // snippets
    private String id;
    private String title;
    private String publishedDate;

    // Statistics
    private BigInteger dislikeCount;
    private BigInteger likeCount;
    private BigInteger commentCount;
    private BigInteger viewCount;
    private String query;

    public void setQuery(String query) {
        this.query = query;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setPublishedDate(String publishedDate) {
        this.publishedDate = publishedDate;
    }

    public void setDislikeCount(BigInteger dislikeCount) {
        this.dislikeCount = dislikeCount;
    }

    public void setLikeCount(BigInteger likeCount) {
        this.likeCount = likeCount;
    }

    public void setCommentCount(BigInteger commentCount) {
        this.commentCount = commentCount;
    }

    public void setViewCount(BigInteger viewCount) {
        this.viewCount = viewCount;
    }

    public String getTitle() {
        return title;
    }

    public String getPublishedDate() {
        return publishedDate;
    }

    public BigInteger getDislikeCount() {
        return dislikeCount;
    }

    public BigInteger getLikeCount() {
        return likeCount;
    }

    public BigInteger getCommentCount() {
        return commentCount;
    }

    public BigInteger getViewCount() {
        return viewCount;
    }

    public String getQuery() {
        return query;
    }

    public String getId() {

        return id;
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("Title: ")
          .append(title)
          .append("\n")
          .append("ViewCount: ")
          .append(viewCount);


        return sb.toString();
    }

}
