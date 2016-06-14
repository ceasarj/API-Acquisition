package acquisition;

import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.*;

import java.io.IOException;
import java.util.*;

import model.VideoModel;

public class YoutubeSource implements Source<VideoModel> {

    private YouTube youtube;
    private Stack<List<VideoModel>> videos;
    private String query;
    private String pageToken;

    private final long MAX_ITEMS = 50;

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
        List<VideoModel> videoModels = new ArrayList<>();
            try {
                // set up the search url
                YouTube.Search.List search = youtube.search()
                        .list("id,snippet")
                        .setKey(System.getenv("YOUTUBE_KEY"))
                        .setQ(query)
                        .setPageToken(pageToken)
                        .setMaxResults(MAX_ITEMS);

                // excecute request and get response
                SearchListResponse response = search.execute();

                // get token for the next page
                pageToken = response.getNextPageToken();

                // conver json response to a list
                List<SearchResult> searchResultList = response.getItems();
                Iterator<SearchResult> searchResultIterator = searchResultList.iterator();

                List<VideoModel> vms = new ArrayList<>();

                // iterate through all results
                while (searchResultIterator.hasNext()) {
                    SearchResult video = searchResultIterator.next();
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

            if (resultsList != null && resultsList.size() > 0) {
                // Only one item in the list because video is searched by ID.
                Video video = resultsList.get(0);
                videoStats =  video.getStatistics();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return videoStats;
    }

    private VideoModel getVideoModel(SearchResult video,VideoStatistics stats) {
        // set up the video model
        VideoModel vm = new VideoModel();
        vm.id = video.getId().getVideoId();
        vm.title = video.getSnippet().getTitle();
        vm.publishedDate = video.getSnippet().getPublishedAt().toString();
        vm.commentCount = stats.getCommentCount();
        vm.viewCount = stats.getViewCount();
        vm.likeCount = stats.getLikeCount();
        vm.dislikeCount = stats.getDislikeCount();
        vm.word = query;
        return vm;
    }

    public static void main(String[] args){
        YoutubeSource ys = new YoutubeSource("Batman");
        while(ys.hasNext()){
            ArrayList<VideoModel> vm = (ArrayList)ys.next();
            System.out.println(vm.get(0));
        }
    }
}
