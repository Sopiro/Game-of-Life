package org.drgnst.gol;

import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.Random;

import javax.swing.JFrame;

/**
 * @author "Baker : http://blog.naver.com/knwer782"
 *
 */
public class GameOfLife extends Canvas implements Runnable
{
	private static final long serialVersionUID = 1L;
	
	public static int frameSize = 360;
	public static String title = "Game Of Life";
	
	public Random r = new Random();
	public int gridSize = 100;
	public double generationSpeed = 20.0;
	
	public BufferedImage image;
	public int[] pixels;
	
	public boolean[] cGrid;
	public boolean[] pGrid;
	
	public GameOfLife()
	{
		Dimension d = new Dimension(frameSize, frameSize);
		setMinimumSize(d);
		setMaximumSize(d);
		setPreferredSize(d);
		
		image = new BufferedImage(gridSize, gridSize, BufferedImage.TYPE_INT_RGB);
		pixels = ((DataBufferInt)image.getRaster().getDataBuffer()).getData();
	}
	
	public void start()
	{
		cGrid = new boolean[pixels.length];
		pGrid = new boolean[pixels.length];
		
		for(int i = 0; i < cGrid.length; i++)
			cGrid[i] = r.nextInt(100) / 100.0 > 0.8 ? true : false;
			
//		try
//		{
//			BufferedImage b = ImageIO.read(GameOfLife.class.getResource("/gliderGun.png"));
//			int[] ppixels = b.getRGB(0, 0, b.getWidth(), b.getHeight(), null, 0, b.getWidth());
//			
//			for(int i = 0; i < ppixels.length; i++)
//			{
//				cGrid[i] = (ppixels[i] & 0xff) < 125 ? true : false;
//			}
//			
//		} catch (IOException e)
//		{
//			e.printStackTrace();
//		}
		
		new Thread(this).start();
	}
	
	public void run()
	{
		double frameCut = 1000000000.0 / generationSpeed;
		
		long currentTime = System.nanoTime();;
		long previouseTime = currentTime;
		long passedTime = 0;
		
		double unprocessedTime = 0.0;
		
		long frameCounter = System.currentTimeMillis();
		int generations = 1;
		
		while(true)
		{
			previouseTime = currentTime;
			currentTime = System.nanoTime();
			passedTime = currentTime - previouseTime;
			
			unprocessedTime += passedTime;
			
			if(unprocessedTime > frameCut)
			{
				unprocessedTime = 0;
				update();
				generations++;
			}
			
			if(System.currentTimeMillis() - frameCounter >= 1000)
			{
				frameCounter = System.currentTimeMillis();
				System.out.println("Generation : " + generations);
			}
			
			render();
		}
	}
	
	public void update()
	{
		for(int i = 0; i < pixels.length; i++)
			pGrid[i] = cGrid[i];
		
		for(int y = 0; y < gridSize; y++)
		{
			for(int x = 0; x < gridSize; x++)
			{
				int res = 0;
				
				int xx0 = x - 1;
				int yy0 = y - 1;
				int xx1 = x + 1;
				int yy1 = y + 1;
				
				if(x != 0)
					res += pGrid[xx0 + gridSize * y] ? 1 : 0;
				if(y != 0)
					res += pGrid[x + gridSize * yy0] ? 1 : 0;
				if(x != gridSize - 1)
					res += pGrid[xx1 + gridSize * y] ? 1 : 0;
				if(y != gridSize - 1)
					res += pGrid[x + gridSize * yy1] ? 1 : 0;
				if(x != 0 && y != 0)
					res += pGrid[xx0 + gridSize * yy0] ? 1 : 0;
				if(x != 0 && y != gridSize - 1)
					res += pGrid[xx0 + gridSize * yy1] ? 1 : 0;
				if(x != gridSize - 1 && y != 0)
					res += pGrid[xx1 + gridSize * yy0] ? 1 : 0;
				if(x != gridSize - 1 && y != gridSize - 1)
					res += pGrid[xx1 + gridSize * yy1] ? 1 : 0;
				
				if(!(pGrid[x + gridSize * y] && (res == 3 || res == 2)))
					cGrid[x + gridSize * y] = false;
				if(!pGrid[x + gridSize * y] && res == 3)
					cGrid[x + gridSize * y] = true;
			}
		}
	}
	
	public void render()
	{
		BufferStrategy bs = getBufferStrategy();
		if(bs == null)
		{
			createBufferStrategy(3);
			return;
		}
		
		Graphics g = bs.getDrawGraphics();

		for (int i = 0; i < pixels.length; i++)
			pixels[i] = 0;
		
		for (int i = 0; i < pixels.length; i++)
			pixels[i] = cGrid[i] ? 0xffffff : 0;
		
		
		g.drawImage(image, 0, 0, frameSize, frameSize, null);
		g.dispose();
		bs.show();
	}

	public static void main(String[] args)
	{
		JFrame frame = new JFrame();
		frame.setTitle(title);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.setAlwaysOnTop(true);
		
		GameOfLife gol = new GameOfLife();
		frame.add(gol);
		frame.pack();
		frame.setLocationRelativeTo(null);
		
		frame.setVisible(true);
		
		gol.start();
	}
}
