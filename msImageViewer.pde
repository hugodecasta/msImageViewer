
SegImage segImage;
String[] images;
PImage[] vignettes;
int currentI = -1;
int IMG_SIZE = 2192;
//LabelManager dataManager;

void setup()
{
  fullScreen();
  
  background(0);
  fill(255);
  text("LOADING ...",20,20);
  text("Logicielle créé par Hugo Castaneda - Mars 2017 - Licence OSL 3.0",10,height-10);
  selectFolder("Selectionner le dossier contenant les images", "folderSelected");
}

void folderSelected(File selection)
{
  String path = selection.getAbsolutePath();
  starting(path);
}

void starting(String folderPath)
{
  images = getAllImageFiles(folderPath);
  vignettes = getImageVignettes(images,width/10);
  
  //dataManager = new LabelManager();
  segImage = new SegImage();
}

void draw()
{
  if(segImage == null)
    return;
  background(0);
  if(segImage.isSelected())
    drawActualImage();
  else
    drawImageSelection();
    
  textAlign(CENTER);
  text("Logiciel msImageViewer créé par Hugo Castaneda - Mars 2017 - Licence OSL 3.0",width/2,height-10);
  textAlign(LEFT);
}

//---------------------------------------
void drawImageSelection()
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

void drawActualImage()
{
  segImage.draw();
}
//---------------------------------------
void keyPressed()
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
void selectImage(int i)
{
  println("s " + i);
  currentI = i;
  segImage = new SegImage();
  segImage.selectImage(images[i]);
}
//---------------------------------------
String[] getAllImageFiles(String path)
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
PImage[] getImageVignettes(String[]path,float maxSize)
{
  PImage[] vigs = new PImage[path.length];
  for(int i=0;i<path.length;++i)
  {
    String truePath = path[i]+ "/_C16.bmp";
    vigs[i] = loadImage(truePath);
    vigs[i].resize(int(maxSize),int(maxSize));
  }
  return vigs;
}
String getTrueName(String path)
{
  String[] splitter = path.replace("\\","/").split("/");
  return splitter[splitter.length-1];
}