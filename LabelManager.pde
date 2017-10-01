public class LabelManager
{
  LabeledData yes,no;
  ArrayList<String>params;
  ArrayList<String>excludes;
  
  public LabelManager()
  {
    yes = new LabeledData("YES");
    no = new LabeledData("NO");
    
    yes.setColor(color(0,0,255));
    no.setColor(color(255,0,0));
    
    excludes = new ArrayList<String>();
    excludes.add("X");
    excludes.add("Y");
    excludes.add("W");
    excludes.add("H");
  }
  
  public void drawAll()
  {
    if(yes.getData().size()==0)
    return;
    if(params == null)
    {
      params = yes.getData().get(0).names;
    }
    
    int w = width / 3;
    int h = height / 2;
    int x = 0;
    int y = 0;
    int margin = 20;
    for(int i=0;i<params.size();++i)
    {
      if(excludes.contains(params.get(i)))
        continue;
      drawDistinct(params.get(i),x+margin, y+margin, w-margin*2, h-margin*2);
      x += w;
      if(x + w > width)
      {
        x = 0;
        y += h;
      }
    }
  }
  
  public void drawDistinct(String param,float x, float y, float w, float h)
  {
    noStroke();
    fill(255);
    rect(x,y,w,h);
    float padding = 30;
    float tx = x + padding;
    float ty = y + padding;
    float tw = w - padding*2;
    float th = h - padding*2;
    stroke(0);
    line(tx,ty,tx,ty+th);
    line(tx,ty+th,tx+tw,ty+th);
    fill(0);
    text(param,x+10,y+20);
    float maxValue = -111;
    for(ImageElement e : yes.getData())
      maxValue = max(e.getValue(param),maxValue);
    for(ImageElement e : no.getData())
      maxValue = max(e.getValue(param),maxValue);
    int nbData = max(no.getData().size(),yes.getData().size()); 
    yes.drawData(param,tx, ty, tw, th, nbData, maxValue);
    no.drawData(param,tx, ty, tw, th, nbData, maxValue);
  }

  public void addToYes(ImageElement elm)
  {
    yes.addData(elm);
  }
  
  public void addToNo(ImageElement elm)
  {
    no.addData(elm);
  }
}