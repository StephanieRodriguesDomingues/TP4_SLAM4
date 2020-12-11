import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

public class PhotoDB extends JFrame {
	JButton btnAjouter;
	JButton btnParcourir;
	JButton btnSupprimer;
	JLabel label;
	JTextField txtId;
	JTextField textNom;
	JTextArea area;
	String s;
	
	public PhotoDB() {
		super("Insérer une photo dans une base de données");
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		btnAjouter = new JButton("Ajouter");
		btnAjouter.setBounds(200, 300, 100, 40);
		
		btnParcourir = new JButton("Parcourir");
		btnParcourir.setBounds(80, 300, 100, 40);
		
		btnSupprimer = new JButton("Supprimer");
		btnSupprimer.setBounds(80, 300, 100, 40);
		
		txtId = new JTextField("ID");
		txtId.setBounds(320, 290, 100, 20);
		
		textNom = new JTextField("Nom");
		textNom.setBounds(320, 330, 100, 20);
		
		area = new JTextArea("DESCRIPTION", 100, 100);
		JScrollPane pane = new JScrollPane(area);
		pane.setBounds(450, 270, 200, 100);
		
		label = new JLabel();
		label.setBounds(10, 10, 670, 250);
		
		//button to browse the image into JLabel
		btnParcourir.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));//user.home
				FileNameExtensionFilter filter = new FileNameExtensionFilter("*.IMAGE", "jpg", "gif", "png");
				fileChooser.addChoosableFileFilter(filter);
				int result = fileChooser.showSaveDialog(null);
				if(result == JFileChooser.APPROVE_OPTION) {
					File selectedFile = fileChooser.getSelectedFile();
					String path = selectedFile.getAbsolutePath();
					label.setIcon(ResizeImage(path));
					s = path;
				}
				else if(result == JFileChooser.CANCEL_OPTION) {
					System.out.println("pas de données");
				}
			}
		});
		
		//button to insert image and some data into mysql database
		btnAjouter.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					Connection con = DriverManager.getConnection("jdbc:mysql://localhost/db_images", "root", "root");
					PreparedStatement ps = con.prepareStatement("insert into monimage(id, nom, description, image) values(?,?,?,?)");
					InputStream is = new FileInputStream(new File(s));
					ps.setString(1, txtId.getText());
					ps.setString(2, textNom.getText());
					ps.setString(3, area.getText());
					ps.setBlob(4, is);
					ps.executeUpdate();
					JOptionPane.showMessageDialog(null, "Enregistrement effectué");
				} catch(Exception ex) {
					ex.printStackTrace();
				}
			}
		});
		
		this.add(label);
		this.add(txtId);
		this.add(textNom);
		this.add(pane);
		this.add(btnAjouter);
		this.add(btnParcourir);
		this.add(btnSupprimer);
		this.setLayout(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(700, 420);
		this.setVisible(true);
	}
	
	//Method to resize the ImageIcon
	public ImageIcon ResizeImage(String imgPath) {
		ImageIcon MyImage = new ImageIcon(imgPath);
		Image img = MyImage.getImage();
		Image newImage = img.getScaledInstance(label.getWidth(), label.getHeight(),Image.SCALE_SMOOTH);
		ImageIcon image = new ImageIcon(newImage);
		return image;
	}
	
	public static void main(String[] args) {
		new PhotoDB();
	}
}
