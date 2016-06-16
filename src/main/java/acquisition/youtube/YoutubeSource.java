package acquisition.youtube;

import acquisition.Source;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.*;

import java.io.IOException;
import java.util.*;

/**
 * Using youtube Api to grab video data.
 *
 * @author Ceasar J.
 * @version 1.0
 */

public class YoutubeSource implements Source<VideoModel> {

    private YouTube youtube;
    private Stack<List<VideoModel>> videos;
    private String query;
    private String pageToken;
    private final long MAX_ITEMS = 50;

    /**
     * Constructor.
     * @param query (required) search youtube videos that matches the query.
     */
    public YoutubeSource(String query){
        this.query = query;
        this.videos = new Stack<>();
        this.pageToken = "";

        this.youtube = new YouTube.Builder(
                new NetHttpTransport(),
                new JacksonFactory(), request -> {
        }).setApplicationName("video-statistics").build();
    }

    @Override
    public boolean hasNext() {
        searchVideo();
        return !videos.empty();
    }

    @Override
    public Collection<VideoModel> next() {
        return videos.pop();
    }

    private void searchVideo() {
        // list of videos in the current page
        List<VideoModel> videoModels = new ArrayList<>();

        try {
            // set up the search url
            YouTube.Search.List search = youtube.search()
                    .list("id,snippet")
                    .setKey(System.getenv("YOUTUBE_KEY"))
                    .setQ(query)
                    .setPageToken(pageToken)
                    .setMaxResults(MAX_ITEMS);


            SearchListResponse response = search.execute();

            pageToken = response.getNextPageToken();

            Iterator<SearchResult> searchResultIterator = response.getItems().iterator();

            // iterate through all results
            while (searchResultIterator.hasNext()) {
                SearchResult video = searchResultIterator.next();

                //resource id needed to determine the type of search result
                ResourceId rId = video.getId();

                // check for only youtube videos
                // some results can be channels etc..
                if (rId.getKind().equals("youtube#video")) {
                    VideoStatistics stats = getVideoStatistics(video.getId().getVideoId());
                    videoModels.add(getVideoModel(video, stats));
                }
            }

            // check if last page contains any videos from search results
            if(videoModels.size() > 0)
                videos.push(videoModels);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private VideoStatistics getVideoStatistics(String videoID){
        VideoStatistics videoStats = null;

        try {
            // set parameters of request
            YouTube.Videos.List videos = youtube.videos()
                    .list("id,statistics")
                    .setId(videoID)
                    .setKey(System.getenv("YOUTUBE_KEY"));

            // make request and get response
            VideoListResponse responseList = videos.execute();

            List<Video> resultsList = responseList.getItems();

            // Only one item in the list because video is searched by ID.
            Video video = resultsList.get(0);

            videoStats =  video.getStatistics();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return videoStats;
    }

    private VideoModel getVideoModel(SearchResult video,VideoStatistics stats) {
        VideoModel vm = new VideoModel();

        vm.setId(video.getId().getVideoId());
        vm.setTitle(video.getSnippet().getTitle());
        vm.setPublishedDate(video.getSnippet().getPublishedAt().toString());
        vm.setCommentCount(stats.getCommentCount());
        vm.setViewCount(stats.getViewCount());
        vm.setLikeCount(stats.getLikeCount());
        vm.setDislikeCount(stats.getDislikeCount());
        vm.setQuery(query);

        return vm;
    }

}
