public class CengVideo
{
    private Integer key;

    private String videoTitle;
    private String channelName;
    private String category;

    public CengVideo(Integer key, String videoTitle, String channelName, String category)
    {
        this.key = key;
        this.videoTitle = videoTitle;
        this.channelName = channelName;
        this.category = category;
    }

    public Integer getKey()
    {
        return key;
    }
    public String getVideoTitle()
    {
        return videoTitle;
    }
    public String getChannelName()
    {
        return channelName;
    }
    public String getCategory()
    {
        return category;
    }

    public String fullName()
    {
        return "" + getKey() + "|" + getVideoTitle() + "|" + getChannelName() + "|" + getCategory();
    }
}