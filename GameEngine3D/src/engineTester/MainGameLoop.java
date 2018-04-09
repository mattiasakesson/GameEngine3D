package engineTester;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import entities.Camera;
import entities.Entity;
import entities.Light;
import entities.Player;
import guis.GuiRenderer;
import guis.GuiTexture;
import models.RawModel;
import models.TexturedModel;
import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import renderEngine.OBJLoader;
import terrain.Terrain;
import textures.ModelTexture;
import textures.TerrainTexture;
import textures.TerrainTexturePack;

public class MainGameLoop {

	public static void main(String[] args) {
		
		DisplayManager.createDisplay();
		Loader loader = new Loader();

		//****************TERRAIN TEXTURE STUFF******************************
		TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("grassy"));
		TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("mud"));
		TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("grassFlowers"));
		TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("path"));
		
		TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture,
				rTexture,gTexture,bTexture);
		
		TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("blendMap"));
		//**********************************************
		
		
		Terrain terrain = new Terrain(0,0,loader,texturePack, blendMap,"heightMap");
		//Terrain terrain2 = new Terrain(-1,-1,loader,texturePack, blendMap,"heightMap");
		
		TexturedModel staticModel = new TexturedModel(OBJLoader.loadObjModel("tree", loader),
				new ModelTexture(loader.loadTexture("tree")));
        
        TexturedModel grass = new TexturedModel(OBJLoader.loadObjModel("grassModel", loader),
        		new ModelTexture(loader.loadTexture("grassTexture")));
        grass.getTexture().setTransparency(true);
        grass.getTexture().setFakeLighting(true);
        
        ModelTexture fernTextureAtlas = new ModelTexture(loader.loadTexture("fernM"));
        fernTextureAtlas.setNumberOfRows(2);
        TexturedModel fern = new TexturedModel(OBJLoader.loadObjModel("fern", loader),
        		fernTextureAtlas	);
        fern.getTexture().setTransparency(true);
        
        List<Entity> entities = new ArrayList<Entity>();
        Random random = new Random();
        for(int i=0;i<500;i++){
	        	float x = random.nextFloat()*800;
	        	float z = random.nextFloat()*800;
	        	float y = terrain.getHeightOfTerrain(x, z);
	        entities.add(new Entity(staticModel, new Vector3f(x,y,z),
	        		0,0,0,5+random.nextFloat()*5));
        }
        
        for(int i=0;i<1000;i++){
	        	float x = random.nextFloat()*800;
	        	float z = random.nextFloat()*800;
	        	float y = terrain.getHeightOfTerrain(x, z);
	        entities.add(new Entity(grass, new Vector3f(x,y,z),
	        		0,0,0,random.nextFloat()*1));
        }
        
        
        
        
        for(int i=0;i<120;i++){
	        	float x = random.nextFloat()*800;
	        	float z = random.nextFloat()*800;
	        	float y = terrain.getHeightOfTerrain(x, z);
            entities.add(new Entity(fern, random.nextInt(4), new Vector3f(x,y,z),
	        		0,0,0,1+random.nextFloat()*1));
        }
		Light light = new Light(new Vector3f(20000,20000,2000),new Vector3f(1,1,1));
		
		MasterRenderer renderer = new MasterRenderer();
		
		
		RawModel bunnyModel = OBJLoader.loadObjModel("stanfordBunny", loader);
		TexturedModel stanfordBunny = new TexturedModel(bunnyModel,
        		new ModelTexture(loader.loadTexture("white")));
		
		Player player  = new Player(stanfordBunny, new Vector3f(0, 0,-50), 0, 0, 0, 1);
		Camera camera = new Camera(player);
		
		//List<GuiTexture> guis = new ArrayList<GuiTexture>();
		//GuiTexture gui = new GuiTexture(loader.loadTexture("socuwan"), new Vector2f(0.5f,0.5f), new Vector2f(0.25f,0.25f));
		//guis.add(gui);
		
		GuiRenderer guiRenderer = new GuiRenderer(loader);
		
		while(!Display.isCloseRequested()) {
			camera.move();
			player.move(terrain);
			renderer.processEntity(player);
			renderer.processTerrain(terrain);
			//renderer.processTerrain(terrain2);
			for(Entity entity:entities){
                renderer.processEntity(entity);
            }
			
			renderer.render(light, camera);
			DisplayManager.updateDisplay();
		}
		
		guiRenderer.cleanUp();
		renderer.cleanUp();
		loader.cleanUp();
		DisplayManager.closeDisplay();

	}

}
