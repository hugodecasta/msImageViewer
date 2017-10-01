public class SegImage
{
  PImage c16, blob;
  String folderPath;
  ArrayList<ImageElement>elements;
  ImageElement selectedElm;
  //-----------------------------
  public SegImage()
  {
    elements = new ArrayList<ImageElement>();
  }
  
  public void selectImage(String folderPath)
  {
    this.folderPath = folderPath;
    c16 = loadImage(folderPath + "/_C16.bmp");
    blob = loadImage(folderPath + "/_BLOB.bmp");
    
    String[] images = getAllImageFiles(folderPath);
    elements = new ArrayList<ImageElement>();
    
    for(String image : images)
    {
      if(image.contains("="))
        elements.add(new ImageElement(image));
    }
  }
  //-----------------------------
  public boolean isSelected()
  {
    return c16 != null;
  }
  //-----------------------------
  public void draw()
  {
    float blob_y = height/2;
    float blob_Height = height/2;
    
    float affHeight = height-50;
    
    PImage backAff = c16;
    if(keyCode == SHIFT && keyPressed)
      backAff = blob;
      
    float c16_size = fillImage(backAff,0,0,width,affHeight);
    fill(255);
    textSize(30);
    text(getTrueName(folderPath),20,height-10);
    
    noFill();
    ImageElement mostLittle = null;
    for(ImageElement elm : elements)
    {
      if(elm.mouseOn(mouseX,mouseY,0,0,c16_size,c16_size))
        if(mostLittle == null)
          mostLittle = elm;
        else if(mostLittle.w > elm.w)
          mostLittle = elm;
      if(elm.used && !(keyCode == SHIFT && keyPressed))
      {
        stroke(0,150,0);
        elm.draw(0,0,c16_size,c16_size);
      }
    }
    if(mostLittle != null)
    {
      if(mostLittle.used)
        stroke(0,0,255);
      else if(mostLittle.brk)
        stroke(255,0,255);
      else
        stroke(255,0,0);
      mostLittle.draw(0,0,c16_size,c16_size);
    }
    if(mostLittle != null && mouseButton == LEFT)
    {
      selectedElm = mostLittle;
    }
    else if(mouseButton == LEFT)
    {
      selectedElm = null;
    }
      
    if(selectedElm != null)
    {
        tint(255/2.1);
        fillImage(backAff,0,0,width,affHeight);
        tint(255);
        selectedElm.drawImage(0,0,c16_size,c16_size);
        stroke(255,0,0);
        strokeWeight(2);
        selectedElm.drawSelect(0,0,c16_size,c16_size);
        strokeWeight(1);
        selectedElm.drawPanel(c16_size,0,width-c16_size,affHeight);
    }
    
    drawInstructions(c16_size);
  }
  
  private void drawInstructions(float startX)
  {
    fill(255);
    textSize(15);
    textAlign(RIGHT,BOTTOM);
    text(" ENTER=quitter cette image\n SHIFT=Blob_Coloring\n"+
    " <-- = img précédante\n --> = img suivante\n "+
    "Cliquer sur un blob pour voire les informations\n Cliquer dans le vide pour nétoyer la selection",width-20,height-20);
    textAlign(LEFT);
  }
  public float fillImage(PImage img,float x, float y, float w, float h)
  {
    float min = min(w,h);
    stroke(255);
    noFill();
    img.resize(int(min),int(min));
    image(img,x,y,min,min);
    rect(x,y,min,min);
    return min;
  }
}
//-----------------------------------
public class ImageElement
{
  ArrayList<String>names;
  ArrayList<Float>values;
  float x,y,w,h;
  boolean used,brk;
  String path;
  PImage image;
  public ImageElement(String path)
  {
    this.path = path;
    values = new ArrayList<Float>();
    names = new ArrayList<String>();
    String[] splitter = path.split(",");
    brk = path.contains("BRK");
    used = path.contains("YES");
    for(int i=1;i<splitter.length;++i)
    {
      String param = splitter[i];
      String[] parSplit = param.split("=");
      String name = parSplit[0];
      float value = float(parSplit[1].replace(".bmp",""));
      
      names.add(name);
      values.add(value);
      
      if(name.equals("X"))x = value;
      if(name.equals("Y"))y = value;
      if(name.equals("W"))w = value;
      if(name.equals("H"))h = value;
    }
    /*if(used)
      dataManager.addToYes(this);
    else
      dataManager.addToNo(this);*/
  }
  
  public Float getValue(String param)
  {
    int i=0;
    for(String s : names)
    {
      if(s.equals(param))
      {
        return values.get(i);
      }
      ++i;
    }
    return null;
  }
  
  public PImage getImage()
  {
    if(image == null)
      image = loadImage(path);
    return image;
  }
  
  public boolean mouseOn(int mx, int my,float oX,float oY,float oW,float oH)
  {
    float ratio = oW / IMG_SIZE;
    float newX = x * ratio + oX;
    float newY = y * ratio + oY;
    float newW = w * ratio;
    float newH = h * ratio;
    
    return (mx > newX && mx < newX+newW && my > newY && my < newY+newH);
  }
  
  public void draw(float oX,float oY,float oW,float oH)
  {
    float ratio = oW / IMG_SIZE;
    float newX = x * ratio + oX;
    float newY = y * ratio + oY;
    float newW = w * ratio;
    float newH = h * ratio;
    rect(newX,newY,newW,newH);
  }
  public void drawSelect(float oX,float oY,float oW,float oH)
  {
    float space = 10;
    float ratio = oW / IMG_SIZE;
    float newX = x * ratio + oX - space;
    float newY = y * ratio + oY - space;
    float newW = w * ratio + space*2;
    float newH = h * ratio+ space*2;
    float max = max(newW,newH);
    ellipse(newX+newW/2,newY+newH/2,max,max);
  }
  
  public void drawImage(float oX,float oY,float oW,float oH)
  {
    float ratio = oW / IMG_SIZE;
    float newX = x * ratio + oX;
    float newY = y * ratio + oY;
    float newW = w * ratio;
    float newH = h * ratio;
    image(getImage(),newX,newY,newW,newH);
  }
  
  public void drawPanel(float oX,float oY,float oW,float oH)
  {
    noStroke();
    int fontSize = 40;
    textSize(fontSize - 15);
    float cY = oY + fontSize;
    for(int i=0;i<names.size();++i)
    {
      String name = names.get(i);
      float value = values.get(i);
      fill(255,100);
      textAlign(RIGHT);
      text(name+" - ",oX+oW/2,cY);
      fill(255);
      textAlign(LEFT);
      text(value,oX+oW/2+5,cY);
      cY += fontSize;
    }
  }
}