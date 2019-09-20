package utils;


import java.io.File;
import java.io.FilenameFilter;
import java.nio.IntBuffer;
import static org.bytedeco.opencv.global.opencv_core.*;

import org.bytedeco.javacv.FrameGrabber.Exception;
import org.bytedeco.opencv.opencv_core.*;
import org.bytedeco.opencv.opencv_face.*;

import com.sun.javafx.beans.IDProperty;

import Service.Reconhecimento;

import static org.bytedeco.opencv.global.opencv_imgcodecs.*;
import static org.bytedeco.opencv.global.opencv_imgproc.resize;

/**
 * @author Jonathan Brito
 * Classe que entra no diretótio das imagens lista os arquivos 
 * faz a Classificação entre imagens de cada usuário e gera 
 * um arquivo yml com essas informações
 */


public class Treinamento {
	
    public static void treinar(int id) {
       //entra no diretório das imagens
    	File diretorio = new File(System.getProperty("user.dir") + "\\Pessoa");
        FilenameFilter filtroImagem = new FilenameFilter() {
            public boolean accept(File dir, String nome) {
                return nome.endsWith(".jpg") || nome.endsWith(".gif") || nome.endsWith(".png");
            }
        };
        //Cria uma Array de Arquivos e armazena em uma matriz de Vetores        
        File[] arquivos = diretorio.listFiles(filtroImagem);
        MatVector fotos = new MatVector(arquivos.length);
        Mat rotulos = new Mat(arquivos.length, 1, CV_32SC1);
        IntBuffer rotulosBuffer = rotulos.createBuffer();
        int contador = 0;
        	//Lista, analiza e redimensiona as Imagens dentro do Diretorio
            //Salva as imagens dentro da Matriz de Vetores Criada
           	for (File imagem: arquivos) {
        		Mat foto = imread(imagem.getAbsolutePath(), IMREAD_GRAYSCALE);
        		int classe = Integer.parseInt(imagem.getName().split("\\.")[1]);
        		System.out.println(imagem.getName().split("\\.")[1] + "  " + imagem.getAbsolutePath());
        		resize(foto, foto, new Size(160,160));
        		fotos.put(contador, foto);
        		rotulosBuffer.put(contador, classe);
        		contador++;       
        	}
       //Instancia o Reconhecedor
        FaceRecognizer eigenfaces = EigenFaceRecognizer.create(10,1); 
       //Faz a classificação das informações presentes nas Matrizes
        eigenfaces.train(fotos, rotulos);
       //Gera um arquivo yml com as informações coletadas 
        eigenfaces.save("src\\recursos\\classificadorEigenFaces.yml");

        Reconhecimento reconhecimento = new Reconhecimento();
        try {
			reconhecimento.reconhecer(id);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//        FaceRecognizer Fisherfaces = FisherFaceRecognizer.create();
//        FaceRecognizer lbph = LBPHFaceRecognizer.create(2,9,9,9,1);
//        lbph.train(fotos, rotulos);
//        lbph.save("src\\recursos\\classificadorLBPH.yml");
//        Fisherfaces.train(fotos, rotulos);
//        Fisherfaces.save("src\\recursos\\classificadorFisherFaces.yml");
        
    }
    
}