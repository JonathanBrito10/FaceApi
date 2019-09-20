package teste;

import org.bytedeco.javacv.FrameGrabber.Exception;

import Service.Captura;

public class teste {

	public static void main(String[] args) {

		Captura captura = new Captura();
		try {
			captura.capturar(3);
		} catch (Exception | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
	}

}
