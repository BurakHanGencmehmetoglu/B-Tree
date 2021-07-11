import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class CengTree
{
    public CengTreeNode root;
    public ArrayList<CengTreeNode> visitedNodesForSearching;

    public CengTree(Integer order) {
        CengTreeNode.order = order;
        CengTreeNode.minDegree = order+1;
        CengTreeNode.maxDegree = (2*order)+1;
        CengTreeNode.maxNumOfVideos = 2*order;
        CengTreeNode.minNumOfVideos = order;
        visitedNodesForSearching = new ArrayList<>();
        this.root = null;
    }

    public CengTreeNodeLeaf findLeafNode(int videoID) {
        CengTreeNodeInternal rootAsInternal = (CengTreeNodeInternal) root;
        ArrayList<Integer> keys = rootAsInternal.getKeys();
        int i;
        for (i = 0; i < rootAsInternal.degree - 1; i++) {
            if (videoID < keys.get(i)) {
                break;
            }
        }

        CengTreeNode child = rootAsInternal.getAllChildren().get(i);
        if (child instanceof CengTreeNodeLeaf) {
            return (CengTreeNodeLeaf) child;
        } else {
            return findLeafNode((CengTreeNodeInternal) child,videoID);
        }
    }

    public CengTreeNodeLeaf findLeafNode(CengTreeNodeInternal internal, int videoID) {

        ArrayList<Integer> keys = internal.getKeys();
        int i;

        for (i = 0; i < internal.degree - 1; i++) {
            if (videoID < keys.get(i)) {
                break;
            }
        }
        CengTreeNode child = internal.getAllChildren().get(i);
        if (child instanceof CengTreeNodeLeaf) {
            return (CengTreeNodeLeaf) child;
        } else {
            return findLeafNode((CengTreeNodeInternal) child,videoID);
        }
    }

    public int getMidpoint() {
        return (int) Math.ceil((CengTreeNode.maxDegree + 1) / 2.0) - 1;
    }

    public ArrayList<CengVideo> splitVideo(CengTreeNodeLeaf cengTreeNodeLeaf, int split) {

        ArrayList<CengVideo> videos = cengTreeNodeLeaf.getVideos();
        ArrayList<CengVideo> halfVideo = new ArrayList<>();

        for (int i = split; i < videos.size(); i++) {
            halfVideo.add(videos.get(i));
        }
        cengTreeNodeLeaf.deleteVideos(split);
        return halfVideo;
    }

    public ArrayList<Integer> splitKeys(CengTreeNodeInternal cengTreeNodeInternal,int split) {

        ArrayList<Integer> keys = cengTreeNodeInternal.getKeys();
        ArrayList<Integer> halfKeys = new ArrayList<>();

        for (int i=split+1;i < keys.size();i++) {
            halfKeys.add(keys.get(i));
        }
        cengTreeNodeInternal.deleteKeys(split);
        Collections.sort(halfKeys);
        return halfKeys;
    }

    public ArrayList<CengTreeNode> splitChildPointers(CengTreeNodeInternal internal,int split) {
        ArrayList<CengTreeNode> pointers = internal.getAllChildren();
        ArrayList<CengTreeNode> halfPointers = new ArrayList<>();

        for (int i = split+1;i<pointers.size();i++) {
            halfPointers.add(pointers.get(i));
        }
        internal.deleteChildren(split+1);
        return halfPointers;
    }

    public void splitInternalNode(CengTreeNodeInternal internal) {
        CengTreeNodeInternal parent = (CengTreeNodeInternal) internal.getParent();
        int midpoint = getMidpoint();
        int newParentKey = internal.getKeys().get(midpoint);
        ArrayList<Integer> halfKeys = splitKeys(internal,midpoint);
        ArrayList<CengTreeNode> halfPointers = splitChildPointers(internal,midpoint);

        CengTreeNodeInternal sibling = new CengTreeNodeInternal(null);
        sibling.setKeys(halfKeys);
        sibling.setChildren(halfPointers);

        for (CengTreeNode cengTreeNode: halfPointers) {
            if (cengTreeNode.getParent() != null) {
                cengTreeNode.setParent(sibling);
            }
        }

        if (parent == null) {
            ArrayList<Integer> keys = new ArrayList<>();
            keys.add(newParentKey);
            CengTreeNodeInternal newRoot = new CengTreeNodeInternal(null);
            newRoot.setKeys(keys);
            newRoot.appendChildPointer(internal);
            newRoot.appendChildPointer(sibling);
            this.root = newRoot;
            internal.setParent(newRoot);
            sibling.setParent(newRoot);
        } else {
            parent.getKeys().add(newParentKey);
            Collections.sort(parent.getKeys());
            int pointerIndex = parent.findIndexOfPointer(internal) + 1;
            parent.insertChildPointer(sibling,pointerIndex);
            sibling.setParent(parent);
        }

    }

    public void addVideo(CengVideo video) {
        if (root == null) {
            CengTreeNodeLeaf cengTreeNodeLeaf = new CengTreeNodeLeaf(null);
            cengTreeNodeLeaf.insert(video);
            this.root = cengTreeNodeLeaf;
        }
        else {
            CengTreeNodeLeaf leafNode = (root instanceof CengTreeNodeLeaf) ? (CengTreeNodeLeaf) root : findLeafNode(video.getKey());

            if (!leafNode.insert(video)) {
                leafNode.getVideos().add(video);
                leafNode.numberOfVideos++;
                leafNode.getVideos().sort(Comparator.comparingInt(CengVideo::getKey));
                int midpoint = getMidpoint();
                ArrayList<CengVideo> halfVideo = splitVideo(leafNode,midpoint);
                boolean flag = false;
                CengTreeNodeInternal parent;

                if (leafNode.getParent() == null) {
                    flag = true;
                    ArrayList<Integer> parent_keys = new ArrayList<>();
                    parent_keys.add(halfVideo.get(0).getKey());
                    parent = new CengTreeNodeInternal(null);
                    parent.setKeys(parent_keys);
                    parent.appendChildPointer(leafNode);
                    leafNode.setParent(parent);
                }

                else {
                    int newParentKey = halfVideo.get(0).getKey();
                    parent = (CengTreeNodeInternal) leafNode.getParent();
                    parent.getKeys().add(newParentKey);
                    Collections.sort(parent.getKeys());
                }

                CengTreeNodeLeaf newLeafNode = new CengTreeNodeLeaf(leafNode.getParent());
                newLeafNode.setVideos(halfVideo);
                parent = (CengTreeNodeInternal) leafNode.getParent();
                int pointerIndex = parent.findIndexOfPointer(leafNode) + 1;
                parent.insertChildPointer(newLeafNode,pointerIndex);

                if (flag) {
                    this.root = leafNode.getParent();
                }
                else {
                    parent = (CengTreeNodeInternal) leafNode.getParent();
                    while (parent != null) {
                        if (parent.isOverfull()) {
                            splitInternalNode(parent);
                        } else {
                            break;
                        }
                        parent = (CengTreeNodeInternal) parent.getParent();
                    }
                }

            }
        }
    }

    public boolean isTreeContainKey(Integer key) {
       if (this.root == null) return false;
       else {
           CengTreeNodeLeaf leaf = (this.root instanceof CengTreeNodeLeaf) ? (CengTreeNodeLeaf) this.root : findLeafNode(key);
           ArrayList<CengVideo> videos = leaf.getVideos();
           for (CengVideo video : videos) {
               if (video.getKey().equals(key))
                   return true;
           }
           return false;
       }
    }

    public String getTabs(int tabCount) {
        return "\t".repeat(Math.max(0, tabCount));
    }

    public void searchAndPrintPath(int videoID,int tabCount) {
        visitedNodesForSearching.add(root);
        CengTreeNodeInternal rootAsInternal = (CengTreeNodeInternal) root;
        ArrayList<Integer> keys = rootAsInternal.getKeys();
        int i;
        for (i = 0; i < rootAsInternal.degree - 1; i++) {
            if (videoID < keys.get(i)) {
                break;
            }
        }
        String tabs = getTabs(tabCount);
        System.out.print(tabs+"<index>"+"\n");
        for (Integer key : keys) {
            System.out.print(tabs+key.toString()+"\n");
        }
        System.out.print(tabs+"</index>"+"\n");
        CengTreeNode child = rootAsInternal.getAllChildren().get(i);
        if (child instanceof CengTreeNodeLeaf) {
            visitedNodesForSearching.add(child);
            CengTreeNodeLeaf childAsLeaf = (CengTreeNodeLeaf) child;
            Integer videoKey = 0;
            String videoTitle = "";
            String channelName = "";
            String category = "";
            for (CengVideo video:childAsLeaf.getVideos()) {
                if (video.getKey().equals(videoID)) {
                    videoKey = video.getKey();
                    videoTitle = video.getVideoTitle();
                    channelName = video.getChannelName();
                    category = video.getCategory();
                    break;
                }
            }
            System.out.println(getTabs(tabCount+1)+"<record>"+videoKey.toString()+"|"+videoTitle+"|"+channelName+"|"+category+"</record>");
        } else {
            searchAndPrintPath((CengTreeNodeInternal) child,videoID,tabCount+1);
        }
    }

    public void searchAndPrintPath(CengTreeNodeInternal internal, int videoID,int tabCount) {
        visitedNodesForSearching.add(internal);
        ArrayList<Integer> keys = internal.getKeys();
        int i;
        for (i = 0; i < internal.degree - 1; i++) {
            if (videoID < keys.get(i)) {
                break;
            }
        }
        String tabs = getTabs(tabCount);
        System.out.print(tabs+"<index>"+"\n");
        for (Integer key : keys) {
            System.out.print(tabs+key.toString()+"\n");
        }
        System.out.print(tabs+"</index>"+"\n");
        CengTreeNode child = internal.getAllChildren().get(i);
        if (child instanceof CengTreeNodeLeaf) {
            visitedNodesForSearching.add(child);
            CengTreeNodeLeaf childAsLeaf = (CengTreeNodeLeaf) child;
            Integer videoKey = 0;
            String videoTitle = "";
            String channelName = "";
            String category = "";
            for (CengVideo video:childAsLeaf.getVideos()) {
                if (video.getKey().equals(videoID)) {
                    videoKey = video.getKey();
                    videoTitle = video.getVideoTitle();
                    channelName = video.getChannelName();
                    category = video.getCategory();
                    break;
                }
            }
            System.out.println(getTabs(tabCount+1)+"<record>"+videoKey.toString()+"|"+videoTitle+"|"+channelName+"|"+category+"</record>");
        } else {
            searchAndPrintPath((CengTreeNodeInternal) child,videoID,tabCount+1);
        }
    }

    public ArrayList<CengTreeNode> searchVideo(Integer key) {
        if (!isTreeContainKey(key)) {
            System.out.println("Could not find "+key.toString()+".");
            return null;
        }
        else if (root instanceof CengTreeNodeLeaf) {
            CengTreeNodeLeaf rootAsLeaf = (CengTreeNodeLeaf) root;
            visitedNodesForSearching.clear();
            visitedNodesForSearching.add(this.root);
            Integer videoKey = 0;
            String videoTitle = "";
            String channelName = "";
            String category = "";
            for (CengVideo video:rootAsLeaf.getVideos()) {
                if (video.getKey().equals(key)) {
                    videoKey = video.getKey();
                    videoTitle = video.getVideoTitle();
                    channelName = video.getChannelName();
                    category = video.getCategory();
                    break;
                }
            }
            System.out.println("<record>"+videoKey.toString()+"|"+videoTitle+"|"+channelName+"|"+category+"</record>");
            return visitedNodesForSearching;
        }
        else {
            visitedNodesForSearching.clear();
            searchAndPrintPath(key,0);
            return visitedNodesForSearching;
        }
    }

    public void printTreeRecursively(CengTreeNodeInternal internal,int tabCount) {
        String tabs = getTabs(tabCount);
        System.out.println(tabs+"<index>");
        for (Integer key : internal.getKeys()) {
            System.out.println(tabs+key.toString());
        }
        System.out.println(tabs+"</index>");
        if (internal.getAllChildren().get(0) instanceof CengTreeNodeLeaf) {
            ArrayList<CengTreeNode> childrens = internal.getAllChildren();
            for (CengTreeNode node : childrens) {
                CengTreeNodeLeaf nodeAsLeaf = (CengTreeNodeLeaf) node;
                System.out.println(getTabs(tabCount+1)+"<data>");
                for (CengVideo video:nodeAsLeaf.getVideos()) {
                    System.out.println(getTabs(tabCount+1)+"<record>"+video.getKey().toString()+"|"+video.getVideoTitle()+"|"+video.getChannelName()+"|"+video.getCategory()+"</record>");
                }
                System.out.println(getTabs(tabCount+1)+"</data>");
            }
        }
        else {
            ArrayList<CengTreeNode> childrens = internal.getAllChildren();
            for (CengTreeNode node:childrens) {
                printTreeRecursively((CengTreeNodeInternal) node,tabCount+1);
            }
        }
    }

    public void printTree() {
        if (root==null) return;
        if (root instanceof CengTreeNodeLeaf) {
            CengTreeNodeLeaf rootAsLeaf = (CengTreeNodeLeaf) root;
            System.out.println("<data>");
            for (CengVideo video:rootAsLeaf.getVideos()) {
                System.out.println("<record>"+video.getKey().toString()+"|"+video.getVideoTitle()+"|"+video.getChannelName()+"|"+video.getCategory()+"</record>");
            }
            System.out.println("</data>");
        }
        else printTreeRecursively((CengTreeNodeInternal) root,0);
    }


}
