package Service;


import java.io.File;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Rect;
import org.bytedeco.opencv.opencv_core.RectVector;
import org.bytedeco.opencv.opencv_core.Scalar;
import org.bytedeco.opencv.opencv_core.Size;
import static org.bytedeco.opencv.global.opencv_imgcodecs.imwrite;
import static org.bytedeco.opencv.global.opencv_imgproc.COLOR_BGRA2GRAY;
import static org.bytedeco.opencv.global.opencv_imgproc.cvtColor;
import static org.bytedeco.opencv.global.opencv_imgproc.rectangle;
import static org.bytedeco.opencv.global.opencv_imgproc.resize;
import org.bytedeco.opencv.opencv_objdetect.CascadeClassifier;

import utils.Treinamento;

import org.bytedeco.javacpp.annotation.CriticalRegion;
import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.javacv.OpenCVFrameGrabber;

/**
 *   @author Jonathan Brito
 *  Essa Classe é responsável por capturar as fotos da face transformar em Matriz e armazenar no diretorio
 *  Pede por parametro o Id do Usuário para que seja salvo com seu Id.  
 */
public class Captura {
    boolean PodeTirarFoto;
	private File dirFoto = null;

	//Faz a Captura das Fotos da Face
	public void capturar(int id) throws FrameGrabber.Exception, InterruptedException {

		int numeroAmostras = 25;
		int amostra = 1;
		int idPessoa = id;
			// Inicializa Camera
			OpenCVFrameConverter.ToMat converteMat = new OpenCVFrameConverter.ToMat();
			OpenCVFrameGrabber camera = new OpenCVFrameGrabber(0);
			camera.start();
			//Instancia o Classificador(Algoritmo responsável por fazer a detecção da Face)
			CascadeClassifier detectorFace = new CascadeClassifier("src\\recursos\\haarcascade_frontalface_alt.xml");
			//Instancia um novo Frame e uma Matriz
			CanvasFrame cFrame = new CanvasFrame("Preview", CanvasFrame.getDefaultGamma() / camera.getGamma());
			Frame frameCapturado = null;
			Mat imagemColorida = new Mat();

			//Faz a Captura
		while ((frameCapturado = camera.grab()) != null) {
			imagemColorida = converteMat.convert(frameCapturado);
			Mat imagemCinza = new Mat();
			cvtColor(imagemColorida, imagemCinza, COLOR_BGRA2GRAY);
			RectVector facesDetectadas = new RectVector();
			//Seta o Classificador para pegar a matriz e o Vetor Ao redor da Face e fazer a Detecção
			detectorFace.detectMultiScale(imagemCinza, facesDetectadas, 1.1, 1, 0, new Size(150, 150),
					new Size(500, 500));
			for (int i = 0; i < facesDetectadas.size(); i++) {
				Rect dadosFace = facesDetectadas.get(0);
				//Mostra um Retângulo ao Redor do Rosto...(Função Visual, não Interfer nas Funções da Api)
				rectangle(imagemColorida, dadosFace, new Scalar(0, 0, 255, 0));
				// Gera a Matriz da Face e Redimensiona a mesma
				Mat faceCapturada = new Mat(imagemCinza, dadosFace);
				resize(faceCapturada, faceCapturada, new Size(160, 160));				
					//Salva A imagem Capturada.
					if (amostra <= numeroAmostras) {
					dirFoto = criarDiretorio();
					imwrite(dirFoto.getAbsolutePath() + "\\pessoa." + idPessoa + "." + amostra + ".jpg", faceCapturada);
					//Incrementa o Numero de Amostra de fotos para que a Detecção não ultrapasse o Limite de Detecção
					amostra++;
					 
					}
			}
			if (cFrame.isVisible()) {
				cFrame.showImage(frameCapturado);
			}
			//Finaliza a Detecção qdo atingir a quantidade de imagens requisitadas
			if (amostra > numeroAmostras) {
				break;
			}
		}
				
		
		
		cFrame.dispose();		
		camera.stop();
		
		//Ao sair do laço chama a classe que ira Fazer o Treinamento
				Treinamento treinamento = new Treinamento();
				treinamento.treinar(idPessoa);
		
	}

	//Função que Cria o Diretorio de Fotos Caso não exista
	File criarDiretorio() {
		File dirFotos = new File(System.getProperty("user.dir") + "\\Pessoa");
		if (!dirFotos.exists()) {
			dirFotos.mkdir();
			if (dirFotos.isDirectory()) {
				return dirFotos;
			}
		}
		return dirFotos;
	}


}
