package application;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;

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
				//ʹ�ò���+���ɷ���ѡ����
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
				//��������ѡ����
				if((projectName.equals("All"))&&(!methodName.equals("All"))&& (baseName.equals("All"))){
					analyseBaseOnDataSet();
				}
				//�����������
				if((projectName.equals("All"))&&(methodName.equals("All"))&&(!baseName.equals("All"))){
					analyseMethodOnDataSet();
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
		
		public void analyseBaseOnDataSet(){
			Tab tb = new Tab("�����෽���Ա�");
			tp.getTabs().add(tb);
			tb.setClosable(true);
			
		}
		
		public void analyseMethodOnDataSet(){
			Tab tb = new Tab("�������ɷ����Ա�");
			tp.getTabs().add(tb);
			tb.setClosable(true);
		}
		
}
