import java.awt.Color;

public abstract class CengTreeNode
{
    static protected Integer order;
    static protected Integer minDegree;
    static protected Integer maxDegree;
    static protected Integer maxNumOfVideos;
    static protected Integer minNumOfVideos;
    private CengTreeNode parent;
    protected CengNodeType type;
    public Integer level;
    public Color color;

    public CengTreeNode(CengTreeNode parent)
    {
        this.parent = parent;
        this.color = CengGUI.getRandomBorderColor();
    }

    public CengTreeNode getParent()
    {
        return parent;
    }

    public void setParent(CengTreeNode parent)
    {
        this.parent = parent;
    }

    public CengNodeType getType()
    {
        return type;
    }

    public Color getColor()
    {
        return this.color;
    }

}
