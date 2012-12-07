import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.font.*;
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.filechooser.*;
import javax.swing.event.*;
import java.io.*;
import java.util.*;
import javax.swing.Timer;
import javax.imageio.*;



class Dfa{

	class DFAState implements Serializable{
		public transient ArrayList<Integer> id;
		public transient ArrayList<String> icon;
		public transient ArrayList<Integer> nextid0;
		public transient ArrayList<Integer> nextid1;
		public boolean astartSelected;
		public int anoFinals;
		public int length;

		private void writeObject(java.io.ObjectOutputStream out) throws IOException{
			out.writeObject(id);
			out.writeObject(icon);
			out.writeObject(nextid0);
			out.writeObject(nextid1);
			out.writeBoolean(astartSelected);
			out.writeInt(anoFinals);
			out.writeInt(length);
			
		}
		private void readObject(ObjectInputStream in)  throws IOException{
			try{
				id=(ArrayList<Integer>) in.readObject();
				icon=(ArrayList<String>)in.readObject();
				nextid0=(ArrayList<Integer>)in.readObject();
				nextid1=(ArrayList<Integer>)in.readObject();
				astartSelected=in.readBoolean();
				anoFinals=in.readInt();
				length=in.readInt();
			}
			catch(Exception e){};
		}


		DFAState(){
			id=new ArrayList<Integer>();
			icon=new ArrayList<String>();
			nextid0=new ArrayList<Integer>();
			nextid1=new ArrayList<Integer>();
			
			for (int i=0;i<nodes.length;++i){
				id.add(nodes[i].id);
				icon.add(nodes[i].icon.getDescription());
				nextid0.add(nodes[i].next0==null?0:nodes[i].next0.id);
				nextid1.add(nodes[i].next1==null?0:nodes[i].next1.id);

			}
			astartSelected=startSelected;
			anoFinals=noFinals;
			length=nodes.length;
		}
		


	}
			

	

	class Node extends JButton implements ActionListener,Serializable{

		public int id;
		public ImageIcon icon;
		public Node next0;
		public Node next1;
		public void actionPerformed(ActionEvent e){
		if (!isRunning){
			
			if (mode==0){
				if (icon==INACTIVE){
					icon=ACTIVE;
					this.setIcon(ACTIVE);
					status.setText("Node Activated");
				}
				else {
							
					for (int i=0;i<nodes.length;++i){
						if (nodes[i].next0==this)
							nodes[i].next0=null;
						if (nodes[i].next1==this)
							nodes[i].next1=null;
					}
										
					this.next0=null;
					this.next1=null;
					if (this.icon==START){
						startSelected=false;
						startNode=null;
					}else if(this.icon==FINAL){
						--noFinals;
					}else if(this.icon==STARTFINAL){
						--noFinals;
						startSelected=false;
						startNode=null;
					}
						
						
							
					this.setIcon(INACTIVE);
					this.icon=INACTIVE;
					status.setText("Node Deactivated");
					array.paintImmediately(0,0,FRAMEWIDTH,FRAMEHEIGHT);
					reload();
				}
					
				
				

			}
			if (mode==1||mode==2){
				
				if (InitialNode!=null){
					InitialNode.setIcon(InitialNode.icon);
					if (mode==1&&InitialNode.next0==null){
						if (this.next0==InitialNode)
							addEdge(InitialNode,this,false,1.5F,false);
						else
							addEdge(InitialNode,this,false,0.8F,false);
						InitialNode.next0=this;
						status.setText("Second Node Selected");
					}else if(mode==2&&InitialNode.next1==null){
						if (this.next1==InitialNode)
							addEdge(InitialNode,this,true,1.5F,false);
						else
							addEdge(InitialNode,this,true,0.8F,false);
						InitialNode.next1=this;
						status.setText("Second Node Selected");

					}
					else{
						status.setText("Transition function For The Node Already Sspecified.");
					}
					InitialNode=null;
				}
				else{
					InitialNode=this;	
					this.setIcon(SELECT);	
					status.setText("Second Node Selected");;
				}
					
				
			}
			if (mode==3){
				if (this.icon==START){
					startSelected=false;
					this.setIcon(ACTIVE);
					this.icon=ACTIVE;
					startNode=null;
					status.setText("Starting Node Removed");
				}else if (!startSelected&&this.icon==ACTIVE){
					this.setIcon(START);
					this.icon=START;
					startSelected=true;
					startNode=this;
					status.setText("Starting Node Selected");
				}else if (!startSelected&&this.icon==FINAL){
					this.icon=STARTFINAL;
					this.setIcon(STARTFINAL);
					startSelected=true;
					startNode=this;
					status.setText("Starting Node Selected");
					
				}else if (this.icon==STARTFINAL){
					this.setIcon(FINAL);
					this.icon=FINAL;
					startSelected=false;
					startNode=null;
					status.setText("Starting Node Removed");
					
				}
				
				else if(startSelected)
					status.setText("Starting Node Already Specified.");
				
			}

			if (mode==4){
				if (this.icon==FINAL){
					this.icon=ACTIVE;
					this.setIcon(ACTIVE);
					--noFinals;
					status.setText("Final Node Removed");
				}else if(this.icon==STARTFINAL){
					this.icon=START;
					this.setIcon(START);
					--noFinals;
					status.setText("Final Node Removed");
				}else if(this.icon==ACTIVE){
					this.icon=FINAL;
					this.setIcon(FINAL);
					++noFinals;
					status.setText("Final Node Added");
				}else if(this.icon==START){
					this.icon=STARTFINAL;
					this.setIcon(STARTFINAL);
					++noFinals;
					status.setText("Final Node Added. This Node Is Also The Starting Node.");
				}
			}
		}
				
			
		}



			
		public Node(int id){
			this.id=id;
			this.setPreferredSize(new Dimension(60,30));
			this.setFont(new Font("Monospaced",Font.BOLD,14));
			this.setText("  ");
			icon=INACTIVE;
			this.setIcon(INACTIVE);
			this.setContentAreaFilled(false);
			this.setBorderPainted(false);
			this.setMargin(new Insets(0,0,0,0));
			this.setPressedIcon(SELECT);
			this.setSelectedIcon(SELECT);
			this.setRolloverIcon(SELECT);
			this.next0=null;
			this.next1=null;
			
			
		}
	}


	public void addEdge(Node start,Node end,boolean mode,float factor,boolean select){
		if(gContext==null){
			gContext=(Graphics2D)backPanel.getGraphics();
			gContext.setStroke(new BasicStroke(2.5F,BasicStroke.CAP_ROUND,BasicStroke.JOIN_BEVEL));
			

		}
		if (mode){
			gContext.setPaint(edge1Active);

			if (isRunning)
				gContext.setPaint(edge1Inactive);
		}

		else {
			gContext.setPaint(edge0Active);


			if (isRunning)
				gContext.setPaint(edge0Inactive);
		}



		if (select)
			gContext.setPaint(edgeSelected);

				
				
		
		float x1=start.getX()+start.getWidth()/2;
		float y1=start.getY()+start.getHeight()/2;

		float x2=end.getX()+end.getWidth()/2;
		float y2=end.getY()+end.getHeight()/2;

		float x3=0.0F,y3=0.0F,incrX=0.0F,incrY=0.0F,incr=0.0F;
		float x31=0.0F,y31=0.0F,x4=0.0F,y4=0.0F,x5=0.0F,y5=0.0F,x6=0.0F,y6=0.0F,x7=0.0F,y7=0.0F;
		float incr1X=0.0F,incr1Y=0.0F,incr2X=0.0F,incr2Y=0.0F;


		if (x1==x2&&y1==y2){
			incr=(GAP_SIZE/10+start.getWidth())/2;
			incr1X=ARROW/(float)Math.sqrt(2);
			if(mode){
				x3=x1;
				y3=y1;				
				x1=(int)(x1-1.4*incr);
				y1=(int)(y1-1.4*incr);
				x4=x1;
				y4=(y3+y1)/2;
				y5=y6=y4-incr1X;
				x5=x4-incr1X;
				x6=x4+incr1X;
					
			}
			else{
				x3=x1;
				y3=y1;				
				x1=(int)(x1-incr);
				y1=(int)(y1-incr);
				y4=y1;
				x4=(x3+x1)/2;
				x5=x6=x4-incr1X;
				y5=y4-incr1X;
				y6=y4+incr1X;	
			}
			x7=x1;
			y7=y1;
			
			gContext.draw(new Arc2D.Float(x1,y1,Math.abs(x3-x1),Math.abs(y3-y1),0F,360F,Arc2D.OPEN));
			gContext.draw(new Line2D.Float((float)x4,(float)y4,(float)x5,(float)y5));
			gContext.draw(new Line2D.Float((float)x4,(float)y4,(float)x6,(float)y6));


		} 
		else{	
			x3=(x1+x2)/2;
			y3=(y1+y2)/2;

			incr=start.getWidth()*factor;
			float length=(float)Math.sqrt(Math.pow(y2-y1,2)+Math.pow(x2-x1,2));	
		
	
			incrX=incr*Math.abs(y2-y1)/length;
			incrY=incr*Math.abs(x2-x1)/length;

			if ((x1<=x2&y1<=y2)||(x2<=x1&y2<=y1)){
				if (mode){
				x3+=incrX;
				y3-=incrY;
				}
				else{
				x3-=incrX;
				y3+=incrY;
				}
					
			}
			else if ((x1>=x2&y1<=y2)||(x2>=x1&y2<=y1)){
				if (mode){
				x3+=incrX;
				y3+=incrY;
				}
				else{
				x3-=incrX;
				y3-=incrY;
				}
			}

		
			QuadCurve2D.Float left=new QuadCurve2D.Float(),right=new QuadCurve2D.Float();
			QuadCurve2D.subdivide(new QuadCurve2D.Float((float)x1,(float)y1,x3,y3,(float)x2,(float)y2),left,right);
			
		

			if ((left.getX2()==right.getX1()&&left.getY2()==right.getY1())||(left.getX2()==right.getX2()&&left.getY2()==right.getY2())){
				x31=(float)left.getX2();
				y31=(float)left.getY2();
			}else if ((left.getX1()==right.getX1()&&left.getY1()==right.getY1())||(left.getX1()==right.getX2()&&left.getY1()==right.getY2())){
				x31=(float)left.getX1();
				y31=(float)left.getY1();
			}

		
	
			incr1X=ARROW*Math.abs(x2-x1)/((float)Math.sqrt(2)*length);
			incr1Y=ARROW*Math.abs(y2-y1)/((float)Math.sqrt(2)*length);
	
			incr2X=ARROW*Math.abs(y2-y1)/length;
			incr2Y=ARROW*Math.abs(x2-x1)/length;


			
			if ((x1<=x2&y1<=y2)){
				x4=x31-incr1X;
				y4=y31-incr1Y;
				x5=x4-incr2X;
				y5=y4+incr2Y;
				x6=x4+incr2X;
				y6=y4-incr2Y;
				x7=x31+incr2X;
				y7=y31-incr2Y;
					
			}
			else if ((x2<=x1&y2<=y1)){
				x4=x31+incr1X;
				y4=y31+incr1Y;
				x5=x4+incr2X;
				y5=y4-incr2Y;
				x6=x4-incr2X;
				y6=y4+incr2Y;
				x7=x31+incr2X;
				y7=y31-incr2Y;
			}
		
			else if ((x1>=x2&y1<=y2)){
			
				x4=x31+incr1X;
				y4=y31-incr1Y;
				x5=x4+incr2X;
				y5=y4+incr2Y;
				x6=x4-incr2X;
				y6=y4-incr2Y;
				x7=x31-incr2X;
				y7=y31-incr2Y;
			}
			else if ((x2>=x1&y2<=y1)){
				x4=x31-incr1X;
				y4=y31+incr1Y;
				x5=x4-incr2X;
				y5=y4-incr2Y;
				x6=x4+incr2X;
				y6=y4+incr2Y;
				x7=x31-incr2X;
				y7=y31-incr2Y;
			}


			gContext.draw(new QuadCurve2D.Float((float)x1,(float)y1,x3,y3,(float)x2,(float)y2));
			gContext.draw(new Line2D.Float((float)x31,(float)y31,(float)x5,(float)y5));
			gContext.draw(new Line2D.Float((float)x31,(float)y31,(float)x6,(float)y6));
			


		}

		if (mode)
			gContext.drawString("1",x7,y7);
		else
			gContext.drawString("0",x7,y7);
		array.invalidate();
	
							
	}


	class BackPanel extends JPanel{

		public void paintComponent(Graphics g){
			if (gContext==null)
				super.paintComponent(g);
			else{
				Graphics2D g1=(Graphics2D) g;
				g1.setPaint(new GradientPaint(0,0,new Color(255,0,0),0,FRAMEHEIGHT,new Color(0,255,0)));
				g1.drawRect(0,0,this.getWidth(),this.getHeight());
				super.paintComponent(gContext);
			}
		}	
	}

	
			
	
	
	
	class NodeArray extends JPanel{

		public NodeArray(){
			JPanel overPanel=new JPanel();
			GridLayout layout=new GridLayout(MAX_NO_NODE_VERT+2,MAX_NO_NODE_HORIZ+2,GAP_SIZE,GAP_SIZE);
			overPanel.setLayout(layout);
			nodes=new Node[MAX_NO_NODE_HORIZ*MAX_NO_NODE_VERT];
			int i=0;
			for (int j=0;j<(MAX_NO_NODE_HORIZ+2)*(MAX_NO_NODE_VERT+2);++j){
				if ((j>(MAX_NO_NODE_HORIZ+1))&&(j<(MAX_NO_NODE_HORIZ+2)*(MAX_NO_NODE_VERT+1))&&((j+1)%(MAX_NO_NODE_HORIZ+2)!=0)&&((j+1)%(MAX_NO_NODE_HORIZ+2)!=1)){
					overPanel.add(nodes[i]=new Node(i+1));
					nodes[i].addActionListener(nodes[i]);
					++i;
				
				}
				else
					overPanel.add(new JPanel());

			}
			this.setLayout(new OverlayLayout(this));
			this.add(overPanel);
			this.add(backPanel);				
		
		
		}
			
	}

	public void reload(){
		for (int i=0;i<nodes.length;++i){
			if (mode!=0){
				if (nodes[i].icon==INACTIVE)
					nodes[i].setVisible(false);
			}else
				nodes[i].setVisible(true);

			nodes[i].setIcon(nodes[i].icon);
			
			if (isRunning){
				if (nodes[i].icon!=FINAL&&nodes[i].icon!=STARTFINAL)
					nodes[i].setIcon(INACTIVE);
			}
					
			
		}
		
		for (int i=0;i<nodes.length;++i){
			if (nodes[i].next0!=null){
				if (nodes[i].next0.id<=i&&nodes[i].next0.next0==nodes[i])
					addEdge(nodes[i],nodes[i].next0,false,1.5F,false);
				else
					addEdge(nodes[i],nodes[i].next0,false,0.8F,false);

			}
			if (nodes[i].next1!=null)
				if (nodes[i].next1.id<=i&&nodes[i].next1.next1==nodes[i])
					addEdge(nodes[i],nodes[i].next1,true,1.5F,false);
				else
					addEdge(nodes[i],nodes[i].next1,true,0.8F,false);
			
		}
		InitialNode=null;
		array.invalidate();
	}
		
	void writeDFA(){
		JFileChooser chooser=new JFileChooser(System.getProperty("user.dir"));
		File f=null;
		chooser.setDialogTitle("Save DFA File");
		chooser.setDialogType(JFileChooser.SAVE_DIALOG);
		chooser.setApproveButtonText("Save");
		chooser.setFileFilter(new FileNameExtensionFilter("DFA Files","dfa"));
		int returnVal=chooser.showOpenDialog(null);
		if (returnVal==JFileChooser.APPROVE_OPTION)
			f=chooser.getSelectedFile();


		
		
		try{
			if (f!=null){
				ObjectOutputStream out=new ObjectOutputStream(new FileOutputStream(f));
				out.writeObject(new DFAState());
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	void readDFA(){
		JFileChooser chooser=new JFileChooser(System.getProperty("user.dir"));
		File f=null;
		chooser.setDialogTitle("Open DFA File");
		chooser.setDialogType(JFileChooser.OPEN_DIALOG);
		chooser.setApproveButtonText("Open");
		chooser.setFileFilter(new FileNameExtensionFilter("DFA Files","dfa"));
		int returnVal=chooser.showOpenDialog(null);
		if (returnVal==JFileChooser.APPROVE_OPTION){
			f=chooser.getSelectedFile();
			if (f.getName().indexOf(".dfa")==-1)
				status.setText("Please Specify A .dfa File");
			else{

				try{
					if (f!=null){
						ObjectInputStream in=new ObjectInputStream(new FileInputStream(f));
						DFAState state=(DFAState)in.readObject();
						for (int i=0;i<state.length;++i){
						nodes[i].id=state.id.get(i);
						if (state.icon.get(i).compareTo("START")==0||state.icon.get(i).compareTo("STARTFINAL")==0)
							startNode=nodes[i];
						if(state.nextid0.get(i)!=0)
							nodes[i].next0=nodes[state.nextid0.get(i)-1];
						else
							nodes[i].next0=null;

						if(state.nextid1.get(i)!=0)
							nodes[i].next1=nodes[state.nextid1.get(i)-1];
						else
							nodes[i].next1=null;
						if (state.icon.get(i).compareTo("INACTIVE")==0){
							nodes[i].icon=INACTIVE;
							nodes[i].setIcon(INACTIVE);
						}
						if (state.icon.get(i).compareTo("ACTIVE")==0){
							nodes[i].icon=ACTIVE;
							nodes[i].setIcon(ACTIVE);
	
						}
						if (state.icon.get(i).compareTo("START")==0){
							nodes[i].icon=START;
							nodes[i].setIcon(START);
		
						}
						if (state.icon.get(i).compareTo("FINAL")==0){
							nodes[i].icon=FINAL;
							nodes[i].setIcon(FINAL);

						}
						if (state.icon.get(i).compareTo("STARTFINAL")==0){
							nodes[i].icon=STARTFINAL;
							nodes[i].setIcon(STARTFINAL);

						}
					}
					startSelected=state.astartSelected;
					noFinals=state.anoFinals;
					status.setText("File Successfully Loaded");
					}
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}
	}	

	void displayHelp(){
		String helpMessage="A Deterministic finite automaton is a finite state machine defined by \n    K, the number of states,\n    \u03A3, an alphabet set,\n    s\u0404K, an initial state,\n    F, a subset ok K number of final states, and\n    \u03B4, a transition function";
		helpMessage+="\n\nThe program functions in 3 Steps:";
		helpMessage+="\n\n Step 1: Specifying a DFA using inbuilt creator or from an external file (.dfa).\n\n    1. In-built creator creates the DFA in 5 modes namely,\n        Selection : Used to specify the number of nodes. The nodes can be activated by clicking on any Inactive nodes.\n        Set Zero : Used to specify the transition function associated with the first symbol ('0')\n        Set One : Used to specify the transition function associated with the second symbol ('1')\n        Start Node : Used to specify the starting node. Any active or final node can be specified as the starting node. Only one starting node can be specified. \n        Final Nodes : To set the final nodes. Any active or starting node can be specified as final.";
		helpMessage+="\n\n    2. To select an external file, click on 'Load' button in the right panel. Only files saved using the program can be loaded.";
		helpMessage+="\n\n Step 2: Feeding the String - The input string can be feeded directly through the 'Text field' in the bottom or from an external .txt file.\nPrev and Next Buttons can be used to skip to the previous and next lines respectively.";
		helpMessage+="\n\n Step 3: Running - The Start, Stop and Pause buttons can be used for their natural operation. A slider is also given to adjust the speed.\nThe delay is inversely propotional to the input string length.";
		JOptionPane popup=new JOptionPane();
		popup.showMessageDialog(null,helpMessage,"Help",JOptionPane.INFORMATION_MESSAGE,new ImageIcon("images/help.png"));

	}
			

	class ControlPanel extends JPanel{
		private JPanel controlpanels[];
		private void setPanel(String panel,int index,int row,int col){
			controlpanels[index]=new JPanel();
			controlpanels[index].setLayout(new GridLayout(row,col));
			controlpanels[index].setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),panel));
			this.add(controlpanels[index]);
			

				
		}

		private JRadioButton addPanelRadioComponent(String text,final int no,boolean enabled){
			JRadioButton c=new JRadioButton(text,enabled);
			c.addActionListener(new ActionListener(){
								public void actionPerformed(ActionEvent e){
									mode=no;
									if (isRunning)
										controlAction(2);
									clearText();
									if (mode==0)
										status.setText("Start Building Your Own DFA By Clicking On Available Nodes.");
									else if(mode==1||mode==2)
										status.setText("Select Any Two Nodes To Add An Edge.");
									else if (mode==3)
										status.setText("Select A Starting Node.");
									else if(mode==4)
										status.setText("Select Final Nodes.");
									reload();
								}
								
								});								
			return c;
		}


		private JButton addControlButton(final String text){
			JButton b=new JButton(text);
			b.setMargin(new Insets(1,1,1,1));
			b.addActionListener(new ActionListener(){
								public void actionPerformed(ActionEvent e){
									if (text=="Start")
										controlAction(0);
									else if(text=="Pause")
										controlAction(1);
									else if(text=="Stop")
										controlAction(2);
									else if(text=="Refresh")
										controlAction(3);
									else if (text=="Reset")
										controlAction(4);
									else if (text=="Save"){
										writeDFA();
										reload();
									}
									else if (text=="Load"){
										controlAction(4);
										readDFA();
										JRadioButton b=(JRadioButton)modeButtons.getComponent(4);
										b.doClick();
									}
									else if(text=="Help")
										displayHelp();
								}
							});
			
			return b;
		}
	
		


		public ControlPanel(){
			this.setLayout(new GridLayout(3,1));
			controlpanels=new JPanel[3];
			setPanel("Mode",0,2,1);
			setPanel("Controls",1,3,1);
			setPanel("Status",2,3,1);
		

			modeButtons=new JPanel();
			modeButtons.setLayout(new GridLayout(2,3));
			controlpanels[0].add(modeButtons);
			
			JRadioButton b;
			ButtonGroup mode=new ButtonGroup();
			b=addPanelRadioComponent("Selection",0,true);
			modeButtons.add(b);
			mode.add(b);
			b=addPanelRadioComponent("Set Zero",1,false);
			modeButtons.add(b);
			mode.add(b);
			b=addPanelRadioComponent("Set One",2,false);
			modeButtons.add(b);
			mode.add(b);
			b=addPanelRadioComponent("Start Node",3,false);
			modeButtons.add(b);
			mode.add(b);
			b=addPanelRadioComponent("Final Nodes",4,false);
			modeButtons.add(b);
			mode.add(b);
			
			info=new JPanel(){
							public void paintComponent(Graphics g){
								super.paintComponent(g);
								Graphics2D g1=(Graphics2D) g;
								g1.drawImage(INFOIMAGE,0,0,null);
							}
						};
			controlpanels[0].add(info);
	

			controlButtons=new JPanel();
			controlButtons.setLayout(new GridLayout(2,4));
			controlpanels[1].add(controlButtons);

			controlButtons.add(addControlButton("Start"));
			controlButtons.add(addControlButton("Pause"));
			controlButtons.add(addControlButton("Stop"));
			controlButtons.add(addControlButton("Help"));
			controlButtons.add(addControlButton("Refresh"));
			controlButtons.add(addControlButton("Reset"));
			controlButtons.add(addControlButton("Save"));
			controlButtons.add(addControlButton("Load"));

			slider=new JSlider(JSlider.HORIZONTAL,SLIDER_MIN_VALUE,SLIDER_MAX_VALUE,50);
			slider.setPaintTicks(true);
			slider.setMajorTickSpacing(5);
			slider.setMinorTickSpacing(2);
			slider.setSnapToTicks(false);
			controlpanels[1].add(slider);
			slider.addChangeListener(new ChangeListener(){
							public void stateChanged(ChangeEvent e){
								
								float value=(float)slider.getValue();
								if (value==0)
									value=1.5F;
								delay=(int)(defaultDelay*Math.pow(50,2)/Math.pow(value,2));
								if (t!=null){
									controlAction(1);
									t=null;
									controlAction(0);
								}
									
									
								
								
									
									
								status.setText("Speed:"+((float)Math.pow(value,2)/Math.pow(50,2))+"X");
								
							}
						});
			
								
								
			themeChooser=new JComboBox();
			themeChooser.setEditable(false);
			themeChooser.addItem("Chroma Theme");
			themeChooser.addItem("Classic Theme");
			themeChooser.addActionListener(new ActionListener(){
							public void actionPerformed(ActionEvent e){
								int index=themeChooser.getSelectedIndex();
								if (index==0){
									try{	
										INACTIVE.setImage(ImageIO.read(new File("images/button_inactive_chroma.gif")));
										ACTIVE.setImage(ImageIO.read(new File("images/button_active_chroma.gif")));
										START.setImage(ImageIO.read(new File("images/button_start_chroma.gif")));
										FINAL.setImage(ImageIO.read(new File("images/button_final_chroma.gif")));
										STARTFINAL.setImage(ImageIO.read(new File("images/button_startfinal_chroma.gif")));
										SELECT.setImage(ImageIO.read(new File("images/button_select_chroma.gif")));
										INFOIMAGE=ImageIO.read(new File("images/info_chroma.gif"));
									}catch(Exception e2){}
									edge0Active=new Color(97,190,35);
									edge1Active=new Color(32,95,140);
									edge0Inactive=new Color(144,218,95);
									edge1Inactive=new Color(66,123,163);
									edgeSelected=new Color(236,11,11);
								}
								if (index==1){
									try{	
										ACTIVE.setImage(ImageIO.read(new File("images/button_active_classic.gif")));
										INACTIVE.setImage(ImageIO.read(new File("images/button_inactive_classic.gif")));					
										START.setImage(ImageIO.read(new File("images/button_start_classic.gif")));
										FINAL.setImage(ImageIO.read(new File("images/button_final_classic.gif")));
										STARTFINAL.setImage(ImageIO.read(new File("images/button_startfinal_classic.gif")));
										SELECT.setImage(ImageIO.read(new File("images/button_select_classic.gif")));
										INFOIMAGE=ImageIO.read(new File("images/info_classic.gif"));
									}catch(Exception e2){}
									edge0Active=new Color(200,200,200);
									edge1Active=new Color(132,132,132);
									edge0Inactive=new Color(200,200,200);
									edge1Inactive=new Color(132,132,132);
									edgeSelected=new Color(236,11,11);
								}
								for (int i=0;i<nodes.length;++i){
									nodes[i].setIcon(SELECT);
									nodes[i].setIcon(nodes[i].icon);
								}
								info.paintImmediately(0,0,info.getWidth(),info.getHeight());
								reload();
								

							
							}
						});
			
			controlpanels[1].add(themeChooser);



			status=new JTextArea(20,4);
			status.setSize(100,100);
			controlpanels[2].add(status);
			status.setEditable(false);
			status.setLineWrap(true);
			status.setWrapStyleWord(true);
			status.setText("Welcome To DFA Creator + Validatator.");

			progress=new JProgressBar();
			progress.setStringPainted(true);
			progress.setMinimum(0);
			controlpanels[2].add(progress);

			stringStatus=new JPanel(){
							public void paintComponent(Graphics g){
								Graphics2D g1=(Graphics2D) g;
								Color back=g1.getColor();
								g1.fillRect(0,0,this.getWidth(),this.getHeight());
								g1.setColor(new Color(255,0,0));
								Font f;
								g1.setFont(f=new Font("Monospaced",Font.BOLD,16));
								
								LineMetrics metrics=f.getLineMetrics(inputString.substring(0,inputString.length()),g1.getFontRenderContext());
								
								float y=(this.getHeight()-metrics.getHeight())/2+metrics.getAscent();
								float widthChar=(((Rectangle2D.Float)f.getStringBounds(inputString,g1.getFontRenderContext())).width)/metrics.getNumChars();
								float x=(this.getWidth()/2)-((stringPos+(float)0.5)*widthChar);
								g1.drawString(inputString,x,y);
								g1.setXORMode(back);
								g1.fill(new Rectangle2D.Float((this.getWidth()-widthChar)/2,(float)8,widthChar,this.getHeight()-16));
								
							}
					};
			
			controlpanels[2].add(stringStatus);


			
			
			

		
		}
			
	}

	class BottomPanel extends JPanel{
		public void update(){
			inputString=inputString.substring(0,stringPos)+stringBox.getText();
			if (inputString.length()!=0){
				defaultDelay=1000*5/inputString.length();
				
				float value=(float)slider.getValue();
				if (value==0)
					value=1.5F;
				delay=(int)(defaultDelay*Math.pow(50,2)/Math.pow(value,2));
				if (isRunning){
					t.stop();
					isRunning=false;
					t=null;
					controlAction(0);
				}
			}
			stringStatus.paintImmediately(0,0,stringStatus.getWidth(),stringStatus.getHeight());
			progress.setMaximum(inputString.length());
		}
		public BottomPanel(){

			this.setLayout(new GridLayout(2,1));
			stringBox=new JTextField("");
			this.add(stringBox);
			stringBox.setHorizontalAlignment(JTextField.CENTER);
			stringBox.getDocument().addDocumentListener(new DocumentListener(){
										public void insertUpdate(DocumentEvent event){
											update();
										}
										public void removeUpdate(DocumentEvent event){
											update();
										}
										public void changedUpdate(DocumentEvent event){}
									});
			JPanel fileControls=new JPanel();
			JButton prev=new JButton("Prev");
			JButton next=new JButton("Next");
			JButton fileButton=new JButton("Choose A File");
			prev.setPreferredSize(new Dimension(100,20));
			fileButton.setPreferredSize(new Dimension(400,20));
			next.setPreferredSize(new Dimension(100,20));

			fileControls.add(prev);
			fileControls.add(fileButton);
			fileControls.add(next);
			this.add(fileControls);
			fileButton.addActionListener(new ActionListener(){
								public void actionPerformed (ActionEvent e){
									fileChooser();
									if (file!=null){
										try{
											currentLine=0;
											fileLines=new ArrayList<String>();
											Scanner in=new Scanner(new FileInputStream(file));
											if (isRunning)
												controlAction(2);
											while (in.hasNextLine())
												fileLines.add(in.nextLine().trim());
											status.setText("The File Contains "+fileLines.size()+" Strings. Loading String 1");
											inputString=fileLines.get(currentLine);
											stringBox.setText(inputString);
											stringStatus.paintImmediately(0,0,stringStatus.getWidth(),stringStatus.getHeight());
											if (inputString.length()!=0){
												defaultDelay=1000*5/inputString.length();
				
												float value=(float)slider.getValue();
												if (value==0)
													value=1.5F;
												delay=(int)(defaultDelay*Math.pow(50,2)/Math.pow(value,2));
											}
										}catch(Exception e1){e1.printStackTrace();}
									}
			
								}
							});

			prev.addActionListener(new ActionListener(){
							public void actionPerformed(ActionEvent e){
								if (file!=null){
									if (currentLine>0){
										if (isRunning)
											controlAction(2);
										--currentLine;
										inputString=fileLines.get(currentLine);
										status.setText("Loaded String "+(currentLine+1));
										stringBox.setText(inputString);
										stringStatus.paintImmediately(0,0,stringStatus.getWidth(),stringStatus.getHeight());
										if (inputString.length()!=0){
											defaultDelay=1000*5/inputString.length();
				
											float value=(float)slider.getValue();
											if (value==0)
												value=1.5F;
											delay=(int)(defaultDelay*Math.pow(50,2)/Math.pow(value,2));
										}
									}
									else
										status.setText("This Is The First String");
							
								}
								else
									status.setText("Oops.. No File Loaded");
	
								}
						});

			next.addActionListener(new ActionListener(){
							public void actionPerformed(ActionEvent e){
								if (file!=null){
									if (currentLine<fileLines.size()-1){
										if (isRunning)
											controlAction(2);
										++currentLine;
										inputString=fileLines.get(currentLine);
										status.setText("Loaded String "+(currentLine+1));
										stringBox.setText(inputString);
										stringStatus.paintImmediately(0,0,stringStatus.getWidth(),stringStatus.getHeight());
										if (inputString.length()!=0){
											defaultDelay=1000*5/inputString.length();
				
											float value=(float)slider.getValue();
											if (value==0)
												value=1.5F;
											delay=(int)(defaultDelay*Math.pow(50,2)/Math.pow(value,2));
										}
									}
									else
										status.setText("This Is The Last String");
							
								}
								else
									status.setText("Oops.. No File Loaded");
	
								}
						});
									
			



				
		}
			

	}
		
	private void fileChooser(){
		JFileChooser chooser = new JFileChooser(System.getProperty("user.dir"));
		chooser.setDialogTitle("Choose a File");
   		FileNameExtensionFilter filter = new FileNameExtensionFilter("Text Files", "txt");
    		chooser.setFileFilter(filter);
   		int returnVal = chooser.showOpenDialog(null);
  		if(returnVal == JFileChooser.APPROVE_OPTION) {
      			file=chooser.getSelectedFile();
   			if (file.getName().indexOf(".txt")==-1){
				status.setText("Please Select A .txt File");
				file=null;
			}
		}
	}
 	








	public void disableControls(int action,boolean value){
		if ((action&1)==1)
			modeButtons.setVisible(value);			
		if ((action&2)==2)
			controlButtons.setVisible(value);
		if ((action&4)==4)
			status.setVisible(value);
	}


	private boolean validator(){
		int noActiveNodes=0;
		boolean all0=true,all1=true;
		for(int i=0;i<nodes.length;++i){
			if (nodes[i].icon!=INACTIVE){
				++noActiveNodes;
				if (nodes[i].next0==null)
					all0=false;
				if (nodes[i].next1==null)
					all1=false;
			}
		}
		if (noActiveNodes==0){
			status.setText("Please Select Atleast One Node");
			return false;
		}
		if (!all0||!all1){
			status.setText("Transition Function Not Complete");
			return false;
		}
		if (startNode==null){
			status.setText("Please Specify A Start Node");
			return false;
		}
		if (noFinals==0){
			status.setText("Please Select Atleast One Final Node");
			return false;
		}
		nodeNos.clear();

		for (int i=0;i<nodes.length;++i){
			if (nodes[i].icon!=INACTIVE){
				int name=iterator(nodes[i],startNode);
				if (name==0)
					nodes[i].setText(" D");
				else{
					int k=name;
					if (nodeNos.contains(name-1)){
						while(nodeNos.contains(k)){
							++k;
						}
					}
					else
						k=name-1;
					nodeNos.add(k);
					if (k<10)
						nodes[i].setText("0"+k);
					else
						nodes[i].setText(""+k);

				}
			}
		}
				
				
		return true;
		
	}
	ArrayList<Integer> nodeNos=new ArrayList<Integer>();

	ArrayList<Node> visitedNodes=new ArrayList<Node>();

	private int iterator(Node target,Node current){
		int NodeNo1=0,NodeNo2=0;

		if (current.id!=target.id){
			visitedNodes.add(current);
			if (visitedNodes.contains(current.next0)==false)
				NodeNo1=iterator(target,current.next0);
			if (visitedNodes.contains(current.next1)==false)
				NodeNo2=iterator(target,current.next1);
			visitedNodes.remove(current);
			if (NodeNo1!=0&&NodeNo2!=0)
				return Math.min(NodeNo1,NodeNo2)+1;
			else if(NodeNo1==0&&NodeNo2!=0)
				return NodeNo2+1;
			else if(NodeNo1!=0&&NodeNo2==0)
				return NodeNo1+1;
			else
				return 0;
			
		}
		
		return 1;

			
			

		
	}
			
		

			
		
	void clearText(){
		for (int i=0;i<nodes.length;++i)
			nodes[i].setText("  ");
	}
			
				
		

	public boolean controlAction(int action){
		
		if (action==0){
		if (!isRunning){
			clearText();
			if (!validator ())
				return false;

			disableControls(1,false);
			if (currentNode==null){
				currentNode=startNode;
				prevNode=null;
				
			}


			if (t==null){
					t=new Timer(delay,new ActionListener(){
							public void actionPerformed(ActionEvent e){

								
								if (stringPos<inputString.length()){
									
									if (prevNode!=null){
										prevNode.setIcon(prevNode.icon);
										if (prevNode.next0==currentNode){

											if (currentNode.id<=(prevNode.id-1)&&currentNode.next0==prevNode)
												addEdge(prevNode,currentNode,false,1.5F,false);
											else
												addEdge(prevNode,currentNode,false,0.8F,false);									
										}
										else{

											if (currentNode.id<=(prevNode.id-1)&&currentNode.next1==prevNode)
												addEdge(prevNode,currentNode,true,1.5F,false);
											else
												addEdge(prevNode,currentNode,true,0.8F,false);
										}

									}
									reload();
																
									stringBox.setText(inputString.substring(stringPos,inputString.length()));
									stringStatus.paintImmediately(0,0,stringStatus.getWidth(),stringStatus.getHeight());
									
									prevNode=currentNode;
									currentNode.setIcon(SELECT);
									char c=inputString.charAt(stringPos++);
									if (c=='0'){

										if (currentNode.next0.id<=(currentNode.id-1)&&currentNode.next0.next0==currentNode)
											addEdge(currentNode,currentNode.next0,false,1.5F,true);
										else
											addEdge(currentNode,currentNode.next0,false,0.8F,true);

										status.setText("Processing String..."+"\n\u03B4("+currentNode.getText()+","+c+","+currentNode.next0.getText()+")");
										currentNode=currentNode.next0;
										
									}
									else if(c=='1'){

										if (currentNode.next1.id<=(currentNode.id-1)&&currentNode.next1.next1==currentNode)
											addEdge(currentNode,currentNode.next1,true,1.5F,true);
										else
											addEdge(currentNode,currentNode.next1,true,0.8F,true);

										status.setText("Processing String..."+"\n\u03B4("+currentNode.getText()+","+c+","+currentNode.next1.getText()+")");
										currentNode=currentNode.next1;
									}
									else{
										controlAction(2);
										status.setText("Invalid Character In The Input String. String Rejected.");
									}
									progress.setValue(stringPos);
									
								}
								else{
									if (currentNode.icon==FINAL||currentNode.icon==STARTFINAL)
										stringAccepted=true;
									controlAction(1);
								}
							}
						});
			}

			status.setText("Process Started...");
			t.start();
			isRunning=true;
		}
		else
			status.setText("Process Already Started");
				
		}

		if (action==1){

			
			
			if (stringPos==inputString.length()){
				controlAction(2);
				
			}else if (t!=null){
				isRunning=false;
				t.stop();
				status.setText("Process Paused...");
			}
			else
				status.setText("Process Not Started!");
		}

		if (action==2){
				
			if (t!=null){
				t.stop();
				isRunning=false;
				
				status.setText("Process Stopped...");
				t=null;
				disableControls(1,true);
				
				if (stringPos==inputString.length()){
					if (stringAccepted)
							status.setText("String Accepted");
						else
							status.setText("String Rejected");
				}
				stringPos=0;
				if (prevNode!=null)
					prevNode.setIcon(prevNode.icon);
				if (currentNode!=null)
					currentNode.setIcon(currentNode.icon);
				currentNode=null;
				prevNode=null;
				stringAccepted=false;
				stringBox.setText(inputString);
				stringStatus.paintImmediately(0,0,stringStatus.getWidth(),stringStatus.getHeight());
				progress.setValue(0);
				reload();
				
			}else
				status.setText("Process Not Started...");

			
		}

		if (action==3){
			reload();
			status.setText("Refresh Complete...");
		}


		if (action==4){
			if (isRunning)
				controlAction(2);
			
			for (int i=0;i<nodes.length;++i){
				nodes[i].next0=null;
				nodes[i].next1=null;
				nodes[i].setIcon(INACTIVE);
				nodes[i].icon=INACTIVE;
			}	
			startNode=null;
			noFinals=0;
			startSelected=false;
			mode=0;
			JRadioButton b=(JRadioButton)modeButtons.getComponent(0);
			b.doClick();
			status.setText("Reset Successful...");
		
		}
			
				
		return true;


	}

	
	public Dfa(){
		backPanel=new BackPanel();
		
		mode=0;
		InitialNode=null;
		frame=new JFrame();
		
		frame.setTitle("Deterministic Finite Automata");
		frame.setSize(FRAMEWIDTH,FRAMEHEIGHT);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		array=new NodeArray();
		control=new ControlPanel();
		bottomPanel=new BottomPanel();
		Container container=frame.getContentPane();
	
		container.setLayout(new BorderLayout());
		container.add(array,BorderLayout.WEST);
		container.add(control,BorderLayout.EAST);
		container.add(bottomPanel,BorderLayout.SOUTH);

		JPanel about=new JPanel(){
				public void paintComponent(Graphics g){
				Graphics2D g1=(Graphics2D) g;
				try{
					g1.drawImage(ImageIO.read(new File("images/about.gif")),0,0,null);
				}catch(Exception e){}
				}
			};
		about.setPreferredSize(new Dimension(FRAMEWIDTH,60));

		JButton aboutus=new JButton("About");
		about.setLayout(new BorderLayout());
		about.add(aboutus,BorderLayout.EAST);
		aboutus.addActionListener(new ActionListener(){
						public void actionPerformed(ActionEvent e){
							JOptionPane popup=new JOptionPane();
							popup.showMessageDialog(null,"Program created by \n NIJITH JACOB (2007A7PS076G)\n APPU M. JOSEPH (2007A7PS022G)\n ANKIT MALPANI (2007A7PS013G)\n VASANTH (2007A7PS023G) \n\n as part of Theory of Computation Course Assignment.","About Us",JOptionPane.INFORMATION_MESSAGE,new ImageIcon("images/aboutico.gif"));
						}
		});


		container.add(about,BorderLayout.NORTH);
		INACTIVE.setDescription("INACTIVE");
		ACTIVE.setDescription("ACTIVE");
		START.setDescription("START");
		FINAL.setDescription("FINAL");
		STARTFINAL.setDescription("STARTFINAL");
		SELECT.setDescription("SELECT");
		try{
			frame.setIconImage(ImageIO.read(new File("images/aboutico.gif")));
		}catch(Exception ee){}
		frame.setVisible(true);

		}
 

	
	final static int FRAMEWIDTH=1010;
	final static int FRAMEHEIGHT=750;
	final static int MAX_NO_NODE_HORIZ=5;
	final static int MAX_NO_NODE_VERT=5;
	ImageIcon INACTIVE=new ImageIcon("images/button_inactive_chroma.gif");
	ImageIcon ACTIVE=new ImageIcon("images/button_active_chroma.gif");
	ImageIcon START=new ImageIcon("images/button_start_chroma.gif");
	ImageIcon FINAL=new ImageIcon("images/button_final_chroma.gif");
	ImageIcon STARTFINAL=new ImageIcon("images/button_startfinal_chroma.gif");
	ImageIcon SELECT=new ImageIcon("images/button_select_chroma.gif");
	Color edge0Active=new Color(97,190,35);
	Color edge1Active=new Color(32,95,140);
	Color edge0Inactive=new Color(144,218,95);
	Color edge1Inactive=new Color(66,123,163);
	Color edgeSelected=new Color(236,11,11);
	Image INFOIMAGE;
	{
		try{
			INFOIMAGE=ImageIO.read(new File("images/info_chroma.gif"));
		}
		catch(Exception e){}
	}
	final static int GAP_SIZE=50;
	final static int ARROW=5;
	final static int SLIDER_MIN_VALUE=0;
	final static int SLIDER_MAX_VALUE=100;
	private JFrame frame;
	private NodeArray array;
	private ControlPanel control;
	private JPanel controlButtons;
	private JSlider slider;
	private JPanel modeButtons;
	private JTextArea status;
	private JPanel stringStatus;
	private JComboBox themeChooser;
	private boolean startSelected=false;
	private int noFinals=0;
	private JTextField stringBox;
	private JProgressBar progress;
	private File file=null;
	private JPanel info;
	private ArrayList<String> fileLines;
	private int currentLine;
	
	private int mode;
	private Node[] nodes;
	private Node InitialNode;
	private Node currentNode=null;
	private Node prevNode=null; 
	private Node startNode=null; 
	private String inputString="";
	private int stringPos=0;
	private Timer t=null;
	private int delay=1000;
	private int defaultDelay=1000;
	private boolean isRunning=false;
	private boolean stringAccepted=false;
	
	private BackPanel backPanel;
	private BottomPanel bottomPanel;
	private Graphics2D gContext;




	public static void main(String[] args){
		Dfa dfa=new Dfa();
		
	}

}
		

	