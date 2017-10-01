import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class msImageViewer extends PApplet {


SegImage segImage;
String[] images;
PImage[] vignettes;
int currentI = -1;
int IMG_SIZE = 2192;
//LabelManager dataManager;

public void setup()
{
  
  
  background(0);
  fill(255);
  text("LOADING ...",20,20);
  text("Logicielle cr\u00e9\u00e9 par Hugo Castaneda - Mars 2017 - Licence OSL 3.0",10,height-10);
  selectFolder("Selectionner le dossier contenant les images", "folderSelected");
}

public void folderSelected(File selection)
{
  String path = selection.getAbsolutePath();
  starting(path);
}

public void starting(String folderPath)
{
  images = getAllImageFiles(folderPath);
  vignettes = getImageVignettes(images,width/10);
  
  //dataManager = new LabelManager();
  segImage = new SegImage();
}

public void draw()
{
  if(segImage == null)
    return;
  background(0);
  if(segImage.isSelected())
    drawActualImage();
  else
    drawImageSelection();
    
  textAlign(CENTER);
  text("Logiciel msImageViewer cr\u00e9\u00e9 par Hugo Castaneda - Mars 2017 - Licence OSL 3.0",width/2,height-10);
  textAlign(LEFT);
}

//---------------------------------------
public void drawImageSelection()
{
  int x = 0;
  int y = 0;
  textSize(15);
  for(int i=0;i<vignettes.length;++i)
  {
    float w = vignettes[i].width;
    image(vignettes[i],x,y);
    fill(255,150);
    text(getTrueName(images[i]),x+10,y+w+20);
    if(mouseX > x && mouseX < x + w && mouseY > y && mouseY < y + w)
    {
      rect(x,y,w,w);
      if(mousePressed)
      {
        selectImage(i);
        return;
      }
    }
    x += vignettes[i].width;
    if(x > width-w)
    {
      y += w + 30;
      x = 0;
    }
  }
}

public void drawActualImage()
{
  segImage.draw();
}
//---------------------------------------
public void keyPressed()
{
  if(keyCode == ENTER)
    segImage = new SegImage();
  else if(keyCode == RIGHT)
  {
    if(currentI>-1 && currentI<images.length-1)
      selectImage(currentI+1);
  }
  else if(keyCode == LEFT)
    if(currentI>-1 && currentI>0)
      selectImage(currentI-1);
      
}
public void selectImage(int i)
{
  println("s " + i);
  currentI = i;
  segImage = new SegImage();
  segImage.selectImage(images[i]);
}
//---------------------------------------
public String[] getAllImageFiles(String path)
{
  File dir= new File(sketchPath(path));
  File[] files= dir.listFiles();
  String[] ret = new String[files.length];
  for (int i = 0; i <= files.length - 1; i++)
  {
    String absPath = files[i].getAbsolutePath();
    ret[i] = absPath;
  }
   return ret;
}
public PImage[] getImageVignettes(String[]path,float maxSize)
{
  PImage[] vigs = new PImage[path.length];
  for(int i=0;i<path.length;++i)
  {
    String truePath = path[i]+ "/_C16.bmp";
    vigs[i] = loadImage(truePath);
    vigs[i].resize(PApplet.parseInt(maxSize),PApplet.parseInt(maxSize));
  }
  return vigs;
}
public String getTrueName(String path)
{
  String[] splitter = path.replace("\\","/").split("/");
  return splitter[splitter.length-1];
}
public class LabeledData
{
  ArrayList<ImageElement>elements;
  String name;
  int c;
  //-----------------------------
  public LabeledData(String name)
  {
    this.name = name;
    elements = new ArrayList<ImageElement>();
  }
  
  public void setColor(int c)
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
        tint(255/2.1f);
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
    " <-- = img pr\u00e9c\u00e9dante\n --> = img suivante\n "+
    "Cliquer sur un blob pour voire les informations\n Cliquer dans le vide pour n\u00e9toyer la selection",width-20,height-20);
    textAlign(LEFT);
  }
  public float fillImage(PImage img,float x, float y, float w, float h)
  {
    float min = min(w,h);
    stroke(255);
    noFill();
    img.resize(PApplet.parseInt(min),PApplet.parseInt(min));
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
      float value = PApplet.parseFloat(parSplit[1].replace(".bmp",""));
      
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
  public void settings() {  fullScreen(); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "msImageViewer" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
