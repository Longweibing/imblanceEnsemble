package application;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import bean.EvaluationInfo;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import thread.SaveFileThread;

public class SelectShowMethod extends Stage {
		@FXML ChoiceBox project;
		@FXML ChoiceBox method;
		@FXML ChoiceBox base;
		Stage stage;
		static Log log;
		CategoryAxis xlabel;
		BarChart chart;
		ListView textShow;
		TabPane tp;
		
		public SelectShowMethod(Stage parent,Log log,CategoryAxis xlabel,BarChart chart,ListView textShow,TabPane tp) throws MalformedURLException  {
			this.log = log;
			this.xlabel = xlabel;
			this.chart = chart;
			this.textShow = textShow;
			this.tp = tp;
			this.initOwner(parent);
			this.initModality(Modality.APPLICATION_MODAL);
			FXMLLoader fXMLLoader = new FXMLLoader(getClass().getResource("selectShowMethod.fxml"),ResourceBundle.getBundle("selectShowMethod"));
			 fXMLLoader.setController(this);
			 Parent root;
				try {  
		        	root = fXMLLoader.load();  
		        } catch (IOException exception) {  
		            throw new RuntimeException(exception);  
		        }  
				 Scene s = new Scene(root);
				 this.setScene(s);
				 stage = this;
				 stage.getIcons().add(new Image(new File(("resource/images/unbalance.png")).toURL().toString()));
				 
		}

		@FXML
		public void initialize() {
			// TODO Auto-generated method stub
		    List projectList = new ArrayList();
		    for(int i = 0 ; i < log.logList.size() ; i++){
		    	Log l = (Log)((List)((List)log.logList.get(i)).get(0)).get(0);
		    	projectList.add(l.getDataset().getDataSetName());
		    }
		    if(projectList.size() > 1){
		       projectList.add("All");
		    }
		    project.getItems().addAll(projectList);
		    
		    List methodList = new ArrayList();
		    List curr = ((List)log.logList.get(0));
		    for(int i = 0 ; i < curr.size() ; i++){
		    	Log l = (Log) ((List)curr.get(i)).get(0);
		    	methodList.add(l.getMethod().getEnsamble()+" "+l.getMethod().getIn()+" "+l.getMethod().getSample());
		    }
		    if(methodList.size() > 1){
		    	methodList.add("All");
		    }
		    method.getItems().addAll(methodList);
		    
		    List baseList = new ArrayList();
		    List currBase = (List) ((List)log.logList.get(0)).get(0);
		    for(int i = 0 ; i < currBase.size() ; i++){
		    	Log l = (Log) currBase.get(i);
		    	baseList.add(l.getMethod().getBase());
		    }
		    if(baseList.size() > 1){
		    	baseList.add("All");
		    }
		    base.getItems().addAll(baseList);
		    
		}
		
		@FXML
		public void okAction(ActionEvent event){
			String projectName = (String) project.getSelectionModel().getSelectedItem();
			String methodName = (String) method.getSelectionModel().getSelectedItem();
			String baseName = (String) base.getSelectionModel().getSelectedItem();
			
			if((!projectName.equals("All"))&&(!methodName.equals("All"))&& (!baseName.equals("All"))){
				List curr = null;
				for(int i = 0 ; i < log.logList.size();i++){
					Log l = (Log) ((List)((List)log.logList.get(i)).get(0)).get(0);
					if(l.getDataset().getDataSetName().equals(projectName)){
						curr = (List) log.logList.get(i);
						break;
					}
				}
				
				List baseList = null;
				for(int j = 0 ; j < curr.size() ; j++){
					Log ml = (Log)((List)curr.get(j)).get(0);
					if(methodName.equals(ml.getMethod().getEnsamble()+" "+ml.getMethod().getIn()+" "+ml.getMethod().getSample())){
						baseList = (List) curr.get(j);
						break;
					}
				}
				
				Log findLog = null;
				for(int k = 0 ; k < baseList.size() ; k++){
					Log temL = (Log) baseList.get(k);
					if(temL.getMethod().getBase().equals(baseName)){
						findLog = temL;
					}
				}
				drawByLog(findLog);
			    stage.close();
			}else{
				//���ݼ�ѡ����
				if(projectName.equals("All")&&(!methodName.equals("All"))&& (!baseName.equals("All"))){
					List projectList = new ArrayList();
					for(int i = 0 ; i < log.logList.size() ; i++){
						for(int j = 0 ; j < ((List)log.logList.get(i)).size() ; j++){
							Method m = ((Log)((List)((List)log.logList.get(i)).get(j)).get(0)).getMethod();
							if(methodName.equals(m.getEnsamble()+" "+m.getIn()+" "+m.getSample())){
								for(int k = 0 ; k < ((List)((List)log.logList.get(i)).get(j)).size() ; k ++){
									Log l = (Log) ((List)((List)log.logList.get(i)).get(k)).get(k);
									if(l.getMethod().getBase().equals(baseName)){
										projectList.add(l);
									}
								}
							}
						}
					}
					drawByProjectList(projectList);
				}
				//ʹ�ò���+���ɷ���ѡ����ALL+""+""
				if((!projectName.equals("All"))&&(methodName.equals("All"))&& (!baseName.equals("All"))){
					List methodList = new ArrayList();
					for(int i = 0 ; i < log.logList.size() ; i++){
						List ml = (List) log.logList.get(i);
						if(((Log)((List)ml.get(0)).get(0)).getDataset().getDataSetName().equals(projectName)){
							for(int j = 0 ; j < ml.size() ; j++){
								for(int k = 0 ; k < ((List)ml.get(j)).size() ; k++){
									Method m = ((Log)((List)ml.get(j)).get(k)).getMethod();
									if(baseName.equals(m.getBase())){
										methodList.addAll((List)ml.get(j));
									}
								}
							}
						}
					}
					drawByMethodList(methodList);
				}
				//��������ѡ����""+""+All
				if((projectName.equals("All"))&&(!methodName.equals("All"))&& (baseName.equals("All"))){
					List newList = new ArrayList();
					for(int i = 0 ; i < log.logList.size() ; i++){
						List ml = (List)log.logList.get(i);
						List mlList = null;
						for(int j = 0 ; j < ml.size() ; j++){
							Method l = ((Log)((List)ml.get(i)).get(0)).getMethod();
							if(methodName.equals(l.getEnsamble()+" "+l.getIn()+" "+l.getBase())){
								mlList = ml;
							}
						}
						newList.add(mlList);
					}
					analyseBaseOnDataSet(newList);
				}
				//���ɷ����Ա�All+All+""
				//�÷�������ͳ��ĳ���������������з������������ݼ��ϵ���Ϣ
				if((projectName.equals("All"))&&(methodName.equals("All"))&&(!baseName.equals("All"))){
					List ml = new ArrayList();//ml��һά�����ݼ���ÿ�����ݼ�������һ���б��б��ÿһά��һ��
					// log.logList����������г��������з����������л��������ϵ����н������һά�����ݼ����ڶ�ά�Ƿ���������ά�ǻ�������
					for(int i = 0 ; i < log.logList.size();i++){
						List pl = (List)log.logList.get(i);
						List npl = new ArrayList();
						for(int j = 0 ; j < pl.size() ; j++){
							List bl = (List) pl.get(j);
							for(int k = 0 ; k < bl.size() ; k++){
								if(((Log)bl.get(k)).getMethod().getBase().equals(baseName)){
									npl.add((Log)bl.get(k));
								}
							}
						}
						ml.add(npl);//ml�ǲ�ͬ���ݼ�+��ͬ�����Ķ�ά�б�
					}
					analyseMethodOnDataSet(ml);
				}
				
				if((projectName.equals("All"))&&(!methodName.equals("All"))&&(baseName.equals("All"))){
					analyseAllOnDataSet(log.logList);
				}
			}
		}
		@FXML
		public void cancelAction(ActionEvent event){
			stage.close();
		}
		
		public void drawByLog(Log log){
			ObservableList<String> names = FXCollections.observableArrayList();
			ObservableList<String> yint = FXCollections.observableArrayList();
			names.addAll(new String[]{"TP","FP","Precision","Recall","FMeasure","Gmeans","Acc","AUC"});
			
			 textShow.getItems().removeAll(textShow.getItems());
			 textShow.getItems().add("���ݼ�: "+log.getDataset().getDataSetName());
			 textShow.getItems().add("ʵ����: "+log.getDataset().getInstancesNum());
			 textShow.getItems().add("������: "+log.getDataset().getAttributesNum());
			 
			 textShow.getItems().add("��������: "+log.getMethod().getSample());
			 textShow.getItems().add("��ʽ: "+log.getMethod().getIn());
			 textShow.getItems().add("���ɷ���: "+log.getMethod().getEnsamble());
			 textShow.getItems().add("�����෽��: "+log.getMethod().getBase());
			 
			 textShow.getItems().add("");
		     DecimalFormat    df   = new DecimalFormat("######0.00");
			 
			 xlabel.setCategories(names);
			//ÿһ��Ϊһ��ϵ��
			for(int i = 0 ; i < log.getEi().get(0).classNum ; i++){
				textShow.getItems().add("��: "+i);
			 XYChart.Series<String, Double> series = new XYChart.Series<>();
			 for(int j = 0 ; j < names.size() ; j++){
				 series.getData().add(new XYChart.Data<>(names.get(j),log.get(j,i)));
				 textShow.getItems().add(names.get(j)+": "+df.format(log.get(j,i)));
			 }
			 series.setName(log.toString()+"-"+i);
			 chart.getData().add(series);
			}
		}
		
		public void drawByProjectList(List arrList){
			 ObservableList<String> names = FXCollections.observableArrayList();
			
			 names.addAll(new String[]{"TP","FP","Precision","Recall","FMeasure","Gmeans","Acc","AUC"});
			
			 textShow.getItems().removeAll(textShow.getItems());
			 textShow.getItems().add("���ɷ���: "+((Log)arrList.get(0)).getMethod().getEnsamble());
			 textShow.getItems().add("��ʽ: "+((Log)arrList.get(0)).getMethod().getIn());
			 textShow.getItems().add("��������: "+((Log)arrList.get(0)).getMethod().getSample());
			 textShow.getItems().add("�����෽��: "+((Log)arrList.get(0)).getMethod().getBase());
			 for(int m = 0 ; m < arrList.size() ; m++){
				 textShow.getItems().add("���ݼ�: "+((Log)arrList.get(m)).getDataset().getDataSetName());
				 textShow.getItems().add("ʵ����: "+((Log)arrList.get(m)).getDataset().getInstancesNum());
				 textShow.getItems().add("������: "+((Log)arrList.get(m)).getDataset().getAttributesNum());
				 
				 
				 textShow.getItems().add("");
			     DecimalFormat    df   = new DecimalFormat("######0.00");
				 
				 xlabel.setCategories(names);
				//ÿһ��Ϊһ��ϵ��
				for(int i = 0 ; i < ((Log)arrList.get(m)).getEi().get(0).classNum ; i++){
					textShow.getItems().add("��: "+i);
					
					 for(int j = 0 ; j < names.size() ; j++){
						 textShow.getItems().add(names.get(j)+": "+df.format(((Log)arrList.get(m)).get(j,i)));
//						 series.getData().add(new XYChart.Data<>(names.get(j),((Log)arrList.get(m)).get(j,i)));
					 }
//				 XYChart.Series<String, Double> series = new XYChart.Series<>();
//				 for(int j = 0 ; j < names.size() ; j++){
//					 textShow.getItems().add(names.get(j)+": "+df.format(((Log)arrList.get(m)).get(j,i)));
//					 series.getData().add(new XYChart.Data<>(names.get(j),((Log)arrList.get(m)).get(j,i)));
//				 }
//				 String dtName = ((Log)arrList.get(m)).getDataset().getDataSetName();
//				 series.setName(dtName.substring(0, dtName.lastIndexOf(".")) +"-"+i);
//				 chart.getData().add(series);
				}
				 XYChart.Series<String, Double> series = new XYChart.Series<>();
				 for(int j = 0 ; j < names.size() ; j++){
					 textShow.getItems().add(names.get(j)+": "+df.format(((Log)arrList.get(m)).get(j,0)));
					 series.getData().add(new XYChart.Data<>(names.get(j),((Log)arrList.get(m)).get(j,0)));
				 }
				 String dtName = ((Log)arrList.get(m)).getDataset().getDataSetName();
				 series.setName(dtName.substring(0, dtName.lastIndexOf(".")));
				chart.getData().add(series);
			 }
			 
			 textShow.getItems().add("");
			 textShow.getItems().add("");
			 for(int i = 0 ; i < names.size() ; i++){
			    textShow.getItems().add("*********");
			    textShow.getItems().add("	Max "+names.get(i)+"	");
			    textShow.getItems().add("---------");
			    Log l = (Log) arrList.get(findMax(arrList,0));
			    textShow.getItems().add(l.getDataset().getDataSetName());
			 }
			 stage.close();
		}
		
		public int findMax(List arrList,int m){
			double max = 0;
			int index = 0;
			for(int i= 0 ; i < arrList.size() ; i++){
				Log l = (Log) arrList.get(i);
				if(l.get(m, 0) > max){
					max = l.get(m, 0);
					index = i;
				}
			}
			return index;
		}
		
		
		public void drawByMethodList(List ml){
			 ObservableList<String> names = FXCollections.observableArrayList();
				
			 names.addAll(new String[]{"TP","FP","Precision","Recall","FMeasure","Gmeans","Acc","AUC"});
			 
			 textShow.getItems().removeAll(textShow.getItems());
			 textShow.getItems().add("���ݼ�: "+((Log)ml.get(0)).getDataset().getDataSetName());
			 textShow.getItems().add("ʵ����: "+((Log)ml.get(0)).getDataset().getInstancesNum());
			 textShow.getItems().add("������: "+((Log)ml.get(0)).getDataset().getAttributesNum());
			 textShow.getItems().add("�����෽��: "+((Log)ml.get(0)).getMethod().getBase());
			 
			 textShow.getItems().add("");
			 DecimalFormat    df   = new DecimalFormat("######0.00");
			 
			 xlabel.setCategories(names);
			 for(int i = 0 ; i < ml.size() ; i++){
			    // int num[] = new int [];
				 XYChart.Series<String, Double> series = new XYChart.Series<>();
				 for(int j = 0 ; j < names.size() ; j++){
					 series.getData().add(new XYChart.Data<>(names.get(j),((Log)ml.get(i)).get(j,0)));
					 textShow.getItems().add(names.get(j)+": "+df.format(((Log)ml.get(i)).get(j,0)));
				 }
				 series.setName(((Log)ml.get(i)).getMethod().getEnsamble()+" "+((Log)ml.get(i)).getMethod().getIn()+" "+((Log)ml.get(i)).getMethod().getSample());
				 chart.getData().add(series);
			 }
			 
			 textShow.getItems().add("************");
			 for(int j = 0 ; j < names.size() ; j++){
				 textShow.getItems().add("*****Max"+names.get(j)+"****");
				 Method m = ((Log)ml.get(findMax(ml,j))).getMethod();
				 textShow.getItems().add(m.getEnsamble()+" "+m.getIn()+" "+m.getSample());
			 }
			 stage.close();
		}
		
		//All+""+All
		public void analyseBaseOnDataSet(List mlList){
			Tab tb = new Tab("�����෽���Ա�");
			tp.getTabs().add(tb);
			tb.setClosable(true);
			tb.setStyle("-fx-select:true");
			TextArea tx = new TextArea();
			tb.setContent(tx);
			stage.close();
		}
		
		//All+All+""
		public void analyseMethodOnDataSet(List ll){
			Tab tb = new Tab("�������ɷ����Ա�");
			tb.setClosable(true);
			TextArea tx = new TextArea();
			
			BorderPane bp = new BorderPane();
			bp.setCenter(tx);
			Button outputLog = new Button("������־");
			bp.setBottom(outputLog);
			
			tb.setContent(bp);
			
			outputLog.setOnAction(new EventHandler<ActionEvent>() {
				
				@Override
				public void handle(ActionEvent event) {
					// TODO Auto-generated method stub
					String result = tx.getText();
					FileChooser fc = new FileChooser();
					fc.setInitialDirectory(new File("/"));
					File f = fc.showSaveDialog(stage);
					if(f != null){
						SaveFileThread sft = new SaveFileThread(f, result);
						Thread t = new Thread(sft);
						t.start();
					}
				}
			});
			tp.getTabs().add(tb);
			textShow.getItems().add("�����෽��: "+((Log)(((List)ll.get(0)).get(0))).getMethod().getBase());
			tx.appendText("====================================\n");
			tx.appendText("�����෽��: "+((Log)(((List)ll.get(0)).get(0))).getMethod().getBase()+"\n");
			tx.appendText("====================================\n");
			tx.appendText("\n\n");
			tx.setEditable(false);
			
			
			ObservableList<String> names = FXCollections.observableArrayList();
			names.addAll(new String[]{"TP","FP","Precision","Recall","FMeasure","Gmeans","Acc","AUC"});
			 DecimalFormat    df   = new DecimalFormat("######0.00");
			 xlabel.setCategories(names);
			//��ȡ��ѡ��Ϸ���������
			List<String> methodNameList = new ArrayList();

		    for(int j = 0 ; j < ((List)ll.get(0)).size() ; j++){
				Log l = ((Log)((List)ll.get(0)).get(j));
				methodNameList.add(l.getMethodString());	
			}
			
		    Log temL = (Log)((List)ll.get(0)).get(0);
			int [][][] matrix = new int[ll.size()][((List)ll.get(0)).size()][8*((EvaluationInfo)temL.getEi().get(0)).classNum];
			//����ͳ�����з����ڸ������ݼ��ϵľ�ֵ
			
			double [][] average1 = new double[8][((List)ll.get(0)).size()];
			double [][] average2 = new double[8][((List)ll.get(0)).size()];
			for(int j = 0 ; j < ((List)ll.get(0)).size() ; j++){//����
				for(int i = 0 ; i < ll.size() ; i++){//���ݼ�
					Log l = (Log)((List)ll.get(i)).get(j);
					String m = l.getMethodString();
					for(int k= 0 ; k < 8 ; k++ ){//ָ��
				  	   average1[k][methodNameList.indexOf(m)] += l.get(k, 0)/ll.size();
				  	   average2[k][methodNameList.indexOf(m)] += l.get(k, 1)/ll.size();
					}
				}
			}
			
			for(int i = 0 ; i < ((List)ll.get(0)).size() ; i++){
				Log l = (Log)((List)ll.get(0)).get(i);
				textShow.getItems().add("-------------");
				textShow.getItems().add("����+���ɷ���");
				textShow.getItems().add(l.getMethodString());
				XYChart.Series<String, Double> series = new XYChart.Series<>();//ÿ������+����ѧϰ��������һ��ϵ��
				for(int j = 0 ; j < names.size() ; j++){
					  textShow.getItems().add("Class 0 Average "+names.get(j)+":"+df.format(average1[j][i]));
					  textShow.getItems().add("Class 1 Average "+names.get(j)+":"+df.format(average2[j][i]));
					  series.getData().add(new XYChart.Data<>(names.get(j),average1[j][i]));
				}
				series.setName(l.getMethodString());
				chart.getData().add(series);
			}
			
			int [][]finRes = new int[methodNameList.size()][8*((EvaluationInfo)temL.getEi().get(0)).classNum ];
			//�ڲ�ͬ���ݼ��Ͻ���ͳ�ơ�
			for(int i = 0 ; i < ll.size() ; i++){//���ݼ�
				double [] currRes = new double[8*((EvaluationInfo)temL.getEi().get(0)).classNum];
				for(int j = 0 ; j < ((List)ll.get(0)).size() ; j++){//����+����ѧϰ
					Log l = (Log) ((List)ll.get(0)).get(j);
					
					for(int ind = 0 ; ind < 8 ; ind ++){
					     for(int k = 0 ; k < ((EvaluationInfo)temL.getEi().get(0)).classNum  ; k++){
						   if(currRes [ind*((EvaluationInfo)temL.getEi().get(0)).classNum+k] < l.get(ind,k)){ 
							   currRes [ind*((EvaluationInfo)temL.getEi().get(0)).classNum+k] = l.get(ind,k);
						   }
						}
					}
				}
				
				//ͳ��
				for(int j = 0 ; j <((List)ll.get(0)).size() ; j++ ){
					Log l = (Log) ((List)ll.get(0)).get(j);
					for(int ind = 0 ; ind < 8 ; ind ++){
					     for(int k = 0 ; k < ((EvaluationInfo)temL.getEi().get(0)).classNum  ; k++){
					    	 if(currRes [ind*((EvaluationInfo)temL.getEi().get(0)).classNum+k] == l.get(ind,k)){
					    		 matrix[i][j][ind*((EvaluationInfo)temL.getEi().get(0)).classNum+k] += 1;
					    	 }
					     }
					}
				}
				
			}
			for(int j = 0 ; j < ((List)ll.get(0)).size() ; j ++){  //����
				for(int i = 0 ; i < ll.size() ; i++){    //���ݼ�
					for(int ind = 0 ; ind < 8 ; ind ++){   
					     for(int k = 0 ; k < ((EvaluationInfo)temL.getEi().get(0)).classNum  ; k++){
					         finRes[j][ind*((EvaluationInfo)temL.getEi().get(0)).classNum+k] += matrix[i][j][ind*((EvaluationInfo)temL.getEi().get(0)).classNum+k];
					     }
					}
				}
			}
			
			    tx.appendText("								");
			    for(int j = 0 ; j < 8 ; j ++){
					    for(int i = 0 ; i < ((EvaluationInfo)temL.getEi().get(0)).classNum  ; i ++){
					    	switch(j){
					    	case 0:
					    		tx.appendText("TP-C"+i+"		");
					    		break;
					    	case 1:
					    		tx.appendText("FP-C"+i+"		");
					    		break;
					    	case 2:
					    		tx.appendText("Precision-C"+i+"			");
					    		break;
					    	case 3:
					    		tx.appendText("Recall-C"+i+"			");
					    		break;
					    	case 4:
					    		tx.appendText("FMeasure-C"+i+"			");
					    		break;
					    	case 5:
					    		tx.appendText("Gmean-C"+i+"			");
					    		break;
					    	case 6:
					    		tx.appendText("Acc-C"+i+"			");
					    		break;
					    	case 7:
					    		tx.appendText("AUC-C"+i+"			");
					    		break;
					    	}
					    }
			    }
			tx.appendText("\n");
			for(int j = 0 ; j < finRes.length ; j ++){
				tx.appendText(methodNameList.get(j)+"			");
				for(int k = 0 ; k < finRes[0].length ; k++){
					tx.appendText(df.format(finRes[j][k])+"			");
				}
				tx.appendText("\n");
			}
			
			tx.appendText("\n\n");
			List<String> result = textShow.getItems();
			StringBuilder strBuild = new StringBuilder();
			
			for(String str : result){
				strBuild.append(str+"\n");
			}
			tx.appendText(strBuild.toString());
			stage.close();
		}
		
		public void analyseAllOnDataSet(List allList){
			
		}
		
}
