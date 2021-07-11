import java.util.ArrayList;
import java.util.Comparator;

public class CengTreeNodeLeaf extends CengTreeNode
{
    private ArrayList<CengVideo> videos;
    public int numberOfVideos;

    public CengTreeNodeLeaf(CengTreeNode parent) {
        super(parent);
        super.type = CengNodeType.Leaf;
        this.videos = new ArrayList<>();
        this.numberOfVideos = 0;
    }

    public ArrayList<CengVideo> getVideos() {
        return videos;
    }

    public void setVideos(ArrayList<CengVideo> newVideos) {
        this.videos = newVideos;
        numberOfVideos = newVideos.size();
        this.videos.sort(Comparator.comparingInt(CengVideo::getKey));
    }

    public void deleteVideos(int last_index) {
        ArrayList<CengVideo> newVideos = new ArrayList<>();
        for (int i = 0; i < last_index; i++) {
            newVideos.add(videos.get(i));
        }
        this.numberOfVideos = newVideos.size();
        this.videos = newVideos;
        this.videos.sort(Comparator.comparingInt(CengVideo::getKey));
    }

    public boolean insert(CengVideo cengVideo) {
        if (isFull()) return false;
        numberOfVideos++;
        videos.add(cengVideo);
        videos.sort(Comparator.comparingInt(CengVideo::getKey));
        return true;
    }

    public boolean isFull() {
        return numberOfVideos == maxNumOfVideos;
    }

    public int videoCount()
    {
        return videos.size();
    }
    public Integer videoKeyAtIndex(Integer index) {
        if(index >= this.videoCount()) {
            return -1;
        } else {
            CengVideo video = this.videos.get(index);

            return video.getKey();
        }
    }

}