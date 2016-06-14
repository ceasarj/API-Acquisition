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
    public String id;
    public String title;
    public String publishedDate;

    // Statistics
    public BigInteger dislikeCount;
    public BigInteger likeCount;
    public BigInteger commentCount;
    public BigInteger viewCount;
    public String word;

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
