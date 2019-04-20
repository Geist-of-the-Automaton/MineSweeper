import java.awt.EventQueue;

public class Starter {

	@SuppressWarnings("unused")
	public static void main(String[] args) 
	{	
		EventQueue.invokeLater(() -> 
    	{
    		MineSweeper ms = new MineSweeper();
        });
	}
}
