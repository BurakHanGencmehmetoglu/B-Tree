import java.util.ArrayList;
import java.util.Collections;

public class CengTreeNodeInternal extends CengTreeNode
{
    private ArrayList<Integer> keys;
    private ArrayList<CengTreeNode> children;

    public int degree;

    public CengTreeNodeInternal(CengTreeNode parent) {
        super(parent);
        super.type = CengNodeType.Internal;
        this.degree = 0;
        this.keys = new ArrayList<>();
        this.children = new ArrayList<>();
    }

    public ArrayList<Integer> getKeys() {
        return keys;
    }

    public void setKeys(ArrayList<Integer> newKeys) {
        Collections.sort(newKeys);
        this.keys = newKeys;
    }

    public void deleteKeys(int last_index) {
        ArrayList<Integer> newKeys = new ArrayList<>();

        for (int i = 0; i < last_index; i++) {
            newKeys.add(keys.get(i));
        }
        Collections.sort(newKeys);
        this.keys = newKeys;
    }

    public void setChildren(ArrayList<CengTreeNode> children) {
        this.children = children;
        degree = children.size();
    }

    public ArrayList<CengTreeNode> getAllChildren()
    {
        return this.children;
    }

    public void deleteChildren(int last_index) {
        ArrayList<CengTreeNode> newChildren = new ArrayList<>();
        for (int i=0;i<last_index;i++) {
            newChildren.add(children.get(i));
        }
        this.degree = newChildren.size();
        this.children = newChildren;
    }

    public void appendChildPointer(CengTreeNode pointer) {
        degree++;
        children.add(pointer);
    }

    public int findIndexOfPointer(CengTreeNode pointer) {
        for (int i = 0; i < children.size(); i++) if (children.get(i) == pointer) return i;
        return -1;
    }

    public void insertChildPointer(CengTreeNode pointer, int index) {
        if (index > children.size()) children.add(pointer);
        else children.add(index, pointer);
        degree++;
    }

    public boolean isOverfull() {
        return this.degree == maxDegree + 1;
    }

    public Integer keyCount()
    {
        return this.keys.size();
    }

    public Integer keyAtIndex(Integer index) {
        if(index >= this.keyCount() || index < 0)
        {
            return -1;
        }
        else
        {
            return this.keys.get(index);
        }
    }
}