package Service;


import org.bytedeco.javacpp.DoublePointer;


import org.bytedeco.javacpp.IntPointer;
import static org.bytedeco.opencv.global.opencv_imgproc.FONT_HERSHEY_PLAIN;
import org.bytedeco.opencv.opencv_core.*;
import org.bytedeco.opencv.opencv_face.*; 

import static org.bytedeco.opencv.global.opencv_imgproc.COLOR_BGRA2GRAY;
import static org.bytedeco.opencv.global.opencv_imgproc.cvtColor;
import static org.bytedeco.opencv.global.opencv_imgproc.putText;
import static org.bytedeco.opencv.global.opencv_imgproc.rectangle;
import static org.bytedeco.opencv.global.opencv_imgproc.resize;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bytedeco.opencv.opencv_objdetect.CascadeClassifier;
import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.FrameGrabber.Exception;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.javacv.OpenCVFrameGrabber;

/*
 *
 * @author Jonathan Brito
 * Classe Responsável pelo reconhecimento da Face do usuario
 * Após a Captura da Face e Treinamento é gerado um arquivo yml
 * com as informações das Faces que é inspecinado aqui pelo reconhecedor
 */
public class Reconhecimento {

	
	static boolean Sair;
	


	
	
    public static  boolean reconhecer(int id) throws FrameGrabber.Exception {
    	
        
   	 
    	//Inicializa a Camera
        OpenCVFrameConverter.ToMat converteMat = new OpenCVFrameConverter.ToMat();
        OpenCVFrameGrabber camera = new OpenCVFrameGrabber(0);
        camera.start();
        
        List <String> pessoas;
        pessoas =  usuarios();
        
        //Inicializa Dependencias de Detecção e Reconhecimento
        CascadeClassifier detectorFace = new CascadeClassifier("src\\recursos\\haarcascade_frontalface_alt.xml");
        FaceRecognizer reconhecedor = EigenFaceRecognizer.create();            
        reconhecedor.read("src\\recursos\\classificadorEigenFaces.yml");  
        reconhecedor.setThreshold(2600);
        CanvasFrame cFrame = new CanvasFrame("Reconhecimento", CanvasFrame.getDefaultGamma() / camera.getGamma());
        Frame frameCapturado = null;
        Mat imagemColorida = new Mat();
        
        //Faz o Reconhecimento        
        while ((frameCapturado = camera.grab()) != null) {
            imagemColorida = converteMat.convert(frameCapturado);
            Mat imagemCinza = new Mat();
            cvtColor(imagemColorida, imagemCinza, COLOR_BGRA2GRAY);
            RectVector facesDetectadas = new RectVector();
            detectorFace.detectMultiScale(imagemCinza, facesDetectadas, 1.1, 2, 0, new Size(100,100), new Size(500,500));
            for (int i = 0; i < facesDetectadas.size(); i++) {
                Rect dadosFace = facesDetectadas.get(i);
                rectangle(imagemColorida, dadosFace, new Scalar(0,255,0,0));
                Mat faceCapturada = new Mat(imagemCinza, dadosFace);
                	IntPointer rotulo = new IntPointer(1);
                	DoublePointer confianca = new DoublePointer(1);
                System.out.println("w="+faceCapturada.size(0)+"  /  h="+faceCapturada.size(1));
                if ((faceCapturada.size(0) == 160) || (faceCapturada.size(1) == 160)){
                    continue;
                }  
                resize(faceCapturada, faceCapturada, new Size(160,160));
                reconhecedor.predict(faceCapturada, rotulo, confianca);
                int predicao = rotulo.get(0);
                String nome;
                if (predicao == -1) {                	
                	nome = "Desconhecido"; 
                              
                } else {
                	nome = pessoas.get(predicao);
                  	
                  	if( predicao == id) {
                  		Sair =  true;
                        
                  		break;
                   	}
                }            
                int x = Math.max(dadosFace.tl().x() - 10, 0);
                int y = Math.max(dadosFace.tl().y() - 10, 0);
                putText(imagemColorida, nome, new Point(x, y), FONT_HERSHEY_PLAIN, 1.4, new Scalar(0,255,0,0));             
            }
            if (cFrame.isVisible()) {
            cFrame.showImage(frameCapturado);
            
            }            
            if(Sair)            	
            break;              
            }   
        System.out.println("Usuario Correto");
        
        cFrame.dispose();		
		camera.stop();
		return true;
        
 }
	public static List<String> usuarios () {
	       File diretorioFotos = new File(System.getProperty("user.dir")+"\\Pessoa");
	       List<String>   sArray = new ArrayList();
	       List <String> pessoas = new ArrayList();
	       pessoas.add("");
	        for (File users : diretorioFotos.listFiles()){        	
	        	sArray.add(users.getName().toString());
	        }
	        int userCount = sArray.size()/25;
         
         for(int i = 1 ; i <= userCount; i++) {
         	pessoas.add(String.valueOf(i));
         }
         System.out.println(pessoas);
         
         return pessoas; 
	       
	}
}


 		//Outros Reconhecedores
// FaceRecognizer reconhecedor = FisherFaceRecognizer.create();            
// reconhecedor.read("src\\recursos\\classificadorEigenFaces.yml");        

//FaceRecognizer reconhecedor = LBPHFaceRecognizer.create();
//reconhecedor.read("src\\recursos\\classificadorLBPH.yml");

 
