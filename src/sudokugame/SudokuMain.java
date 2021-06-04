package sudokugame;

import java.awt.EventQueue;

public class SudokuMain {
            public static void main(String[] args) 
        {
            EventQueue.invokeLater(new Runnable() {
		public void run() {
			try {
				SudokuGame window = new SudokuGame("new title");

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	});
	}
}
