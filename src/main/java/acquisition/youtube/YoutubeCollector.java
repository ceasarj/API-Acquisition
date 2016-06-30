package acquisition.youtube;

import acquisition.Collector;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Store youtube data into mysql db
 *
 */
public class YoutubeCollector implements Collector<VideoModel, VideoModel> {

    private final String  url;
    private final String user;
    private final String pass;

    public YoutubeCollector(){
        url = "jdbc:mysql://localhost/metronics";
        user = System.getenv("MYSQL_USER");
        pass = System.getenv("MYSQL_PASS");
    }

    @Override
    public Collection<VideoModel> mungee(Collection<VideoModel> source) {
        List<VideoModel> cleanedList = new ArrayList<>();

        for(VideoModel vm: source){
            // check if any of the data in the video model is not null
            if(!vm.isDirty()){
                cleanedList.add(vm);
            }
        }

        return cleanedList;
    }

    @Override
    public void save(Collection<VideoModel> data) {
        Connection con = null;

        String query = "INSERT into video_data"+
                "(video_id, title, published_date, view_count, like_count, dislike_count, comment_count )"+
                "values(?,?,?,?,?,?,?)";

        try {
            con = DriverManager.getConnection(user, pass, url);

            PreparedStatement psmt = con.prepareStatement(query);
            Statement stm = con.createStatement();
            Iterator<VideoModel> it = data.iterator();

            while(it.hasNext()){
                VideoModel vm = it.next();

                String selectVideo = "SELECT video_id FROM video_data WHERE video_id =" + vm.getId();

                ResultSet set = stm.executeQuery(selectVideo);

                // check if this video is already stored in the db
                if(!set.next()) {

                    psmt.setString(0, vm.getId());
                    psmt.setString(1, vm.getTitle());
                    psmt.setString(2, vm.getPublishedDate());
                    psmt.setInt(3, vm.getViewCount().intValue());
                    psmt.setInt(4, vm.getLikeCount().intValue());
                    psmt.setInt(5, vm.getDislikeCount().intValue());
                    psmt.setInt(5, vm.getCommentCount().intValue());

                    psmt.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if( con != null )
                    con.close();
            }
            catch( SQLException e ) {
                e.printStackTrace();
            }
        }
    }
}
