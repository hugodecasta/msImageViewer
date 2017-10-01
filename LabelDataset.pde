public class LabeledData
{
  ArrayList<ImageElement>elements;
  String name;
  color c;
  //-----------------------------
  public LabeledData(String name)
  {
    this.name = name;
    elements = new ArrayList<ImageElement>();
  }
  
  public void setColor(color c)
  {
    this.c = c;
  }
  //-----------------------------
  public void addData(ImageElement elm)
  {
    if(!elements.contains(elm));
      elements.add(elm);
  }
  
  public ArrayList<ImageElement> getData()
  {
    return elements;
  }
  
  public void drawData(String param,float x, float y, float w, float h, float maxX, float maxY)
  {
    println("------------");
    int i = 0;
    for(ImageElement e : elements)
    {
      float value = e.getValue(param);
      float ty = h/maxY * value;
      ty = h - ty  + y;
      float tx = w/maxX * i + x;
      println(maxX/w * i);
      stroke(c);
      strokeWeight(3);
      point(tx,ty);
      strokeWeight(1);
      fill(c);
      //text(value,tx,ty-5);
      ++i;
    }
  }
}